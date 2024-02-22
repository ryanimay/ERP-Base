package com.erp.base.service;

import com.erp.base.config.websocket.WebsocketConstant;
import com.erp.base.enums.NotificationEnum;
import com.erp.base.enums.RoleConstant;
import com.erp.base.enums.StatusConstant;
import com.erp.base.enums.response.ApiResponseCode;
import com.erp.base.model.ClientIdentity;
import com.erp.base.model.MessageModel;
import com.erp.base.model.dto.request.PageRequestParam;
import com.erp.base.model.dto.request.leave.LeaveAcceptRequest;
import com.erp.base.model.dto.request.leave.LeaveRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.dto.response.LeaveResponse;
import com.erp.base.model.dto.response.PageResponse;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.entity.LeaveModel;
import com.erp.base.model.entity.NotificationModel;
import com.erp.base.repository.LeaveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public ResponseEntity<ApiResponse> list(PageRequestParam page) {
        ClientModel user = ClientIdentity.getUser();
        if(user == null) return ApiResponse.error(ApiResponseCode.USER_NOT_FOUND);
        Page<LeaveModel> leaves = leaveRepository.findAllByUser(user.getId(), page.getPage());
        return ApiResponse.success(new PageResponse<>(leaves, LeaveResponse.class));
    }

    public ResponseEntity<ApiResponse> add(LeaveRequest request) {
        ClientModel user = ClientIdentity.getUser();
        if(user == null) return ApiResponse.error(ApiResponseCode.USER_NOT_FOUND);
        LeaveModel entity = request.toModel();
        LeaveModel saved = updateOrSave(entity, user);
        sendMessageToManager(user);
        return ApiResponse.success(ApiResponseCode.SUCCESS, new LeaveResponse(saved));
    }

    private void sendMessageToManager(ClientModel user) {
        NotificationModel notification = notificationService.createNotification(NotificationEnum.ADD_LEAVE, user.getUsername());
        Set<Long> byHasAcceptPermission = clientService.queryReviewer(user.getDepartment().getId());
        byHasAcceptPermission.forEach(id -> {
            MessageModel messageModel = new MessageModel(user.getUsername(), id.toString(), WebsocketConstant.TOPIC.NOTIFICATION, notification);
            messageService.sendTo(messageModel);
        });
    }

    public ResponseEntity<ApiResponse> update(LeaveRequest request) {
        ClientModel user = ClientIdentity.getUser();
        if(user == null) return ApiResponse.error(ApiResponseCode.USER_NOT_FOUND);
        Optional<LeaveModel> byId = leaveRepository.findById(request.getId());
        if(byId.isPresent()){
            LeaveModel leaveModel = byId.get();
            if(request.getType() != null) leaveModel.setType(request.getType());
            if(request.getStartTime() != null) leaveModel.setStartTime(request.getStartTime());
            if(request.getEndTime() != null) leaveModel.setEndTime(request.getEndTime());
            if(request.getInfo() != null) leaveModel.setInfo(request.getInfo());
            LeaveModel saved = updateOrSave(leaveModel, user);
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
            NotificationModel notification = notificationService.createNotification(NotificationEnum.ACCEPT_LEAVE);
            ClientModel user = ClientIdentity.getUser();
            MessageModel messageModel = new MessageModel(user.getUsername(), request.getEventUserId().toString(), WebsocketConstant.TOPIC.NOTIFICATION, notification);
            messageService.sendTo(messageModel);
            return ApiResponse.success(ApiResponseCode.SUCCESS);
        }
        return ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "accept: " + i);
    }

    public ResponseEntity<ApiResponse> pendingList(PageRequestParam page) {
        ClientModel user = ClientIdentity.getUser();
        boolean isManager = user.getRoles().stream().anyMatch(model -> model.getLevel() == RoleConstant.LEVEL_3);
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
