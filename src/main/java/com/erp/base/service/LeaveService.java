package com.erp.base.service;

import com.erp.base.config.websocket.WebsocketConstant;
import com.erp.base.model.ClientIdentity;
import com.erp.base.model.MessageModel;
import com.erp.base.model.constant.NotificationEnum;
import com.erp.base.model.constant.RoleConstant;
import com.erp.base.model.constant.StatusConstant;
import com.erp.base.model.constant.response.ApiResponseCode;
import com.erp.base.model.dto.request.PageRequestParam;
import com.erp.base.model.dto.request.leave.LeaveAcceptRequest;
import com.erp.base.model.dto.request.leave.LeaveRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.dto.response.LeaveResponse;
import com.erp.base.model.dto.response.PageResponse;
import com.erp.base.model.dto.security.ClientIdentityDto;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.entity.LeaveModel;
import com.erp.base.model.entity.NotificationModel;
import com.erp.base.repository.LeaveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class LeaveService {
    private LeaveRepository leaveRepository;
    private MessageService messageService;
    private NotificationService notificationService;
    private ClientService clientService;
    @Autowired
    public void setClientService(ClientService clientService){
        this.clientService = clientService;
    }
    @Autowired
    public void setNotificationService(NotificationService notificationService){
        this.notificationService = notificationService;
    }
    @Autowired
    public void setMessageService(MessageService messageService){
        this.messageService = messageService;
    }
    @Autowired
    public void setLeaveRepository(LeaveRepository leaveRepository){
        this.leaveRepository = leaveRepository;
    }

    public ResponseEntity<ApiResponse> list(LeaveRequest request) {
        Page<LeaveModel> leaves = leaveRepository.findAll(request.getSpecification(), request.getPage());
        return ApiResponse.success(new PageResponse<>(leaves, LeaveResponse.class));
    }

    public ResponseEntity<ApiResponse> add(LeaveRequest request) {
        ClientIdentityDto user = ClientIdentity.getUser();
        if(user == null) return ApiResponse.error(ApiResponseCode.USER_NOT_FOUND);
        LeaveModel entity = request.toModel();
        LeaveModel saved = updateOrSave(entity, user.toEntity());
        sendMessageToManager(user);
        return ApiResponse.success(ApiResponseCode.SUCCESS, new LeaveResponse(saved));
    }

    private void sendMessageToManager(ClientIdentityDto user) {
        Set<ClientModel> byHasAcceptPermission = clientService.queryReviewer(user.getDepartment().getId());
        NotificationModel notification = notificationService.createNotificationToUser(NotificationEnum.ADD_LEAVE, byHasAcceptPermission, user.getUsername());
        byHasAcceptPermission.forEach(client -> {
            MessageModel messageModel = new MessageModel(user.getUsername(), String.valueOf(client.getId()), WebsocketConstant.TOPIC.NOTIFICATION, notification);
            messageService.sendTo(messageModel);
        });
    }

    public ResponseEntity<ApiResponse> update(LeaveRequest request) {
        ClientIdentityDto user = ClientIdentity.getUser();
        if(user == null) return ApiResponse.error(ApiResponseCode.USER_NOT_FOUND);
        Optional<LeaveModel> byId = leaveRepository.findById(request.getId());
        if(byId.isPresent()){
            LeaveModel leaveModel = byId.get();
            if(request.getType() != null) leaveModel.setType(request.getType());
            if(request.getStartTime() != null) leaveModel.setStartTime(request.getStartTime());
            if(request.getEndTime() != null) leaveModel.setEndTime(request.getEndTime());
            if(request.getInfo() != null) leaveModel.setInfo(request.getInfo());
            LeaveModel saved = updateOrSave(leaveModel, new ClientModel(user.getId()));
            return ApiResponse.success(ApiResponseCode.SUCCESS, new LeaveResponse(saved));
        }
        return ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "Id Not Found");
    }

    public ResponseEntity<ApiResponse> delete(long id) {
        int i = leaveRepository.deleteByIdAndStatus(id, StatusConstant.PENDING_NO);
        if(i == 1) return ApiResponse.success(ApiResponseCode.SUCCESS);
        return ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "Id Not Found");
    }

    public ResponseEntity<ApiResponse> accept(LeaveAcceptRequest request) {
        int i = leaveRepository.updateLeaveStatus(request.getId(), StatusConstant.PENDING_NO, StatusConstant.APPROVED_NO);
        if(i == 1) {
            Long eventUserId = request.getEventUserId();
            Set<ClientModel> userSet = new HashSet<>();
            userSet.add(new ClientModel(eventUserId));
            NotificationModel notification = notificationService.createNotificationToUser(NotificationEnum.ACCEPT_LEAVE, userSet);
            ClientIdentityDto user = ClientIdentity.getUser();
            MessageModel messageModel = new MessageModel(Objects.requireNonNull(user).getUsername(), String.valueOf(eventUserId), WebsocketConstant.TOPIC.NOTIFICATION, notification);
            messageService.sendTo(messageModel);
            return ApiResponse.success(ApiResponseCode.SUCCESS);
        }
        return ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "Id Not Found");
    }

    public ResponseEntity<ApiResponse> reject(LeaveAcceptRequest request) {
        int i = leaveRepository.updateLeaveStatus(request.getId(), StatusConstant.PENDING_NO, StatusConstant.REMOVED_NO);
        if(i == 1) {
            Long eventUserId = request.getEventUserId();
            Set<ClientModel> userSet = new HashSet<>();
            userSet.add(new ClientModel(eventUserId));
            NotificationModel notification = notificationService.createNotificationToUser(NotificationEnum.REJECT_LEAVE, userSet);
            ClientIdentityDto user = ClientIdentity.getUser();
            MessageModel messageModel = new MessageModel(Objects.requireNonNull(user).getUsername(), String.valueOf(request.getEventUserId()), WebsocketConstant.TOPIC.NOTIFICATION, notification);
            messageService.sendTo(messageModel);
            return ApiResponse.success(ApiResponseCode.SUCCESS);
        }
        return ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "Id Not Found");
    }

    public ResponseEntity<ApiResponse> pendingList(PageRequestParam page) {
        ClientIdentityDto user = ClientIdentity.getUser();
        if(user == null) return ApiResponse.error(ApiResponseCode.USER_NOT_FOUND);
        ClientModel client = clientService.findById(user.getId());
        boolean isManager = client.getRoles().stream().anyMatch(model -> model.getLevel() == RoleConstant.LEVEL_3);
        Page<LeaveModel> allPending;
        //管理權限全搜不分部門
        if(isManager){
            allPending = leaveRepository.findByStatus(user.getId(), StatusConstant.PENDING_NO, page.getPage());
        }else{
            allPending = leaveRepository.findByStatusAndDepartment(user.getId(), user.getDepartment().getId(), StatusConstant.PENDING_NO, page.getPage());
        }
        return ApiResponse.success(new PageResponse<>(allPending, LeaveResponse.class));
    }

    private LeaveModel updateOrSave(LeaveModel model, ClientModel user){
        if(model.getUser() == null) {
            model.setUser(user);
        }
        return leaveRepository.save(model);
    }
}
