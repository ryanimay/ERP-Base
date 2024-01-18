package com.erp.base.service;

import com.erp.base.config.websocket.WebsocketConstant;
import com.erp.base.enums.NotificationEnum;
import com.erp.base.enums.response.ApiResponseCode;
import com.erp.base.model.ClientIdentity;
import com.erp.base.model.MessageModel;
import com.erp.base.model.dto.request.PageRequestParam;
import com.erp.base.model.dto.request.performance.AddPerformanceRequest;
import com.erp.base.model.dto.request.performance.PerformanceListRequest;
import com.erp.base.model.dto.request.performance.UpdatePerformanceRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.entity.NotificationModel;
import com.erp.base.model.entity.PerformanceModel;
import com.erp.base.model.entity.UserModel;
import com.erp.base.repository.PerformanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional
public class PerformanceService {
    private PerformanceRepository performanceRepository;
    private MessageService messageService;
    private NotificationService notificationService;
    private ClientService clientService;
    @Autowired
    public void setClientService(ClientService clientService){
        this.clientService = clientService;
    }    @Autowired
    public void setNotificationService(NotificationService notificationService){
        this.notificationService = notificationService;
    }
    @Autowired
    public void setMessageService(MessageService messageService){
        this.messageService = messageService;
    }
    @Autowired
    public void setPerformanceRepository(PerformanceRepository performanceRepository){
        this.performanceRepository = performanceRepository;
    }

    public ResponseEntity<ApiResponse> getAllList(PerformanceListRequest request) {
        List<PerformanceModel> allPerformance = performanceRepository.findAllPerformance(request.getUserId(), request.getStartTime(), request.getEndTime(), request.getPage());
        return ApiResponse.success(ApiResponseCode.SUCCESS, allPerformance);
    }

    public ResponseEntity<ApiResponse> getList(PerformanceListRequest request) {
        UserModel user = ClientIdentity.getUser();
        if(user == null){
            return ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "User Not Found");
        }
        request.setUserId(user.getId());//限搜本人
        return getAllList(request);
    }

    public ResponseEntity<ApiResponse> add(AddPerformanceRequest request) {
        UserModel user = ClientIdentity.getUser();
        if(user == null){
            return ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "User Not Found");
        }
        request.setCreateBy(user.getId());
        performanceRepository.save(request.toModel());
        sendMessage(user);
        return ApiResponse.success(ApiResponseCode.SUCCESS);
    }

    private void sendMessage(UserModel user) {
        NotificationModel notification = notificationService.createNotification(NotificationEnum.ADD_PERFORMANCE);
        Set<Long> byHasAcceptPermission = clientService.findByHasAcceptPermission();
        byHasAcceptPermission.forEach(id -> {
            MessageModel messageModel = new MessageModel(user.getUsername(), id.toString(), WebsocketConstant.TOPIC.NOTIFICATION, notification);
            messageService.sendTo(messageModel);
        });
    }

    public ResponseEntity<ApiResponse> save(UpdatePerformanceRequest request) {
        performanceRepository.save(request.toModel());
        return ApiResponse.success(ApiResponseCode.SUCCESS);
    }

    public ResponseEntity<ApiResponse> remove(Long eventId) {
        int i = performanceRepository.updateStateRemoved(eventId);
        if(i ==1) return ApiResponse.success(ApiResponseCode.SUCCESS);
        return ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "removed:" + i);
    }

    public ResponseEntity<ApiResponse> accept(Long eventId, Long eventUserId) {
        int i = performanceRepository.updateStateAccept(eventId);
        if(i == 1) {
            NotificationModel notification = notificationService.createNotification(NotificationEnum.ACCEPT_PERFORMANCE);
            UserModel user = ClientIdentity.getUser();
            MessageModel messageModel = new MessageModel(user.getUsername(), eventUserId.toString(), WebsocketConstant.TOPIC.NOTIFICATION, notification);
            messageService.sendTo(messageModel);
            return ApiResponse.success(ApiResponseCode.SUCCESS);
        }
        return ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "accept:" + i);
    }

    public ResponseEntity<ApiResponse> pendingList(PageRequestParam request) {
        List<PerformanceModel> list = performanceRepository.findAllByStatus(request.getPage());
        return ApiResponse.success(ApiResponseCode.SUCCESS, list);
    }
}
