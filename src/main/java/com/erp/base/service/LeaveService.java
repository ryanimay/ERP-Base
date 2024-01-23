package com.erp.base.service;

import com.erp.base.config.websocket.WebsocketConstant;
import com.erp.base.controller.Router;
import com.erp.base.enums.JobStatusEnum;
import com.erp.base.enums.NotificationEnum;
import com.erp.base.enums.response.ApiResponseCode;
import com.erp.base.model.ClientIdentity;
import com.erp.base.model.MessageModel;
import com.erp.base.model.dto.request.PageRequestParam;
import com.erp.base.model.dto.request.leave.AddLeaveRequest;
import com.erp.base.model.dto.request.leave.UpdateLeaveRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.dto.response.PageResponse;
import com.erp.base.model.entity.LeaveModel;
import com.erp.base.model.entity.NotificationModel;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.repository.LeaveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return ApiResponse.success(new PageResponse<>(leaves, LeaveModel.class));
    }

    public ResponseEntity<ApiResponse> add(AddLeaveRequest request) {
        ClientModel user = ClientIdentity.getUser();
        if(user == null) return ApiResponse.error(ApiResponseCode.USER_NOT_FOUND);
        LeaveModel entity = request.toModel();
        LeaveModel saved = updateOrSave(entity, user);
        sendMessageToManager(user);
        return ApiResponse.success(ApiResponseCode.SUCCESS, saved);
    }

    private void sendMessageToManager(ClientModel user) {
        NotificationModel notification = notificationService.createNotification(NotificationEnum.ADD_LEAVE, user.getUsername());
        Set<Long> byHasAcceptPermission = clientService.findByHasAcceptPermission(Router.LEAVE.ACCEPT);
        byHasAcceptPermission.forEach(id -> {
            MessageModel messageModel = new MessageModel(user.getUsername(), id.toString(), WebsocketConstant.TOPIC.NOTIFICATION, notification);
            messageService.sendTo(messageModel);
        });
    }

    public ResponseEntity<ApiResponse> update(UpdateLeaveRequest request) {
        ClientModel user = ClientIdentity.getUser();
        if(user == null) return ApiResponse.error(ApiResponseCode.USER_NOT_FOUND);
        LeaveModel entity = request.toModel();
        LeaveModel saved = updateOrSave(entity, user);
        return ApiResponse.success(ApiResponseCode.SUCCESS, saved);
    }

    public ResponseEntity<ApiResponse> delete(long id) {
        int i = leaveRepository.deleteByIdIfStatus(id, JobStatusEnum.PENDING.getName(), JobStatusEnum.REMOVED.getName());
        if(i == 1) return ApiResponse.success(ApiResponseCode.SUCCESS);
        return ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "delete: " + i);
    }

    public ResponseEntity<ApiResponse> accept(long id, Long eventUserId) {
        int i = leaveRepository.accept(id, JobStatusEnum.PENDING.getName(), JobStatusEnum.APPROVED.getName());
        if(i == 1) {
            NotificationModel notification = notificationService.createNotification(NotificationEnum.ACCEPT_LEAVE);
            ClientModel user = ClientIdentity.getUser();
            MessageModel messageModel = new MessageModel(user.getUsername(), eventUserId.toString(), WebsocketConstant.TOPIC.NOTIFICATION, notification);
            messageService.sendTo(messageModel);
            return ApiResponse.success(ApiResponseCode.SUCCESS);
        }
        return ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "accept: " + i);
    }

    public ResponseEntity<ApiResponse> pendingList(PageRequestParam page) {
        Page<LeaveModel> allPending = leaveRepository.findAllByStatus(JobStatusEnum.PENDING.getName(), page.getPage());
        return ApiResponse.success(new PageResponse<>(allPending, LeaveModel.class));
    }

    private LeaveModel updateOrSave(LeaveModel model, ClientModel user){
        model.setUser(user);
        return leaveRepository.save(model);
    }
}
