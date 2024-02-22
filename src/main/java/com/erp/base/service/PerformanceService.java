package com.erp.base.service;

import com.erp.base.config.websocket.WebsocketConstant;
import com.erp.base.enums.NotificationEnum;
import com.erp.base.enums.RoleConstant;
import com.erp.base.enums.StatusConstant;
import com.erp.base.enums.response.ApiResponseCode;
import com.erp.base.model.ClientIdentity;
import com.erp.base.model.MessageModel;
import com.erp.base.model.dto.request.PageRequestParam;
import com.erp.base.model.dto.request.performance.PerformanceAcceptRequest;
import com.erp.base.model.dto.request.performance.PerformanceRequest;
import com.erp.base.model.dto.response.*;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.entity.NotificationModel;
import com.erp.base.model.entity.PerformanceModel;
import com.erp.base.repository.PerformanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class PerformanceService {
    private PerformanceRepository performanceRepository;
    private MessageService messageService;
    private NotificationService notificationService;
    private ClientService clientService;

    @Autowired
    public void setClientService(ClientService clientService) {
        this.clientService = clientService;
    }

    @Autowired
    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Autowired
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @Autowired
    public void setPerformanceRepository(PerformanceRepository performanceRepository) {
        this.performanceRepository = performanceRepository;
    }

    public ResponseEntity<ApiResponse> getList(PerformanceRequest request) {
        Page<PerformanceModel> allPerformance = performanceRepository.findAll(request.getSpecification(), request.getPage());
        return ApiResponse.success(new PageResponse<>(allPerformance, PerformanceResponse.class));
    }

    public ResponseEntity<ApiResponse> add(PerformanceRequest request) {
        ClientModel user = ClientIdentity.getUser();
        if (user == null) {
            return ApiResponse.error(ApiResponseCode.ACCESS_DENIED, "User Identity Not Found");
        }
        PerformanceModel entity = request.toModel();
        Long userId = request.getUserId();
        entity.setUser(userId == null ? user : new ClientModel(userId));
        entity.setCreateBy(user);
        performanceRepository.save(entity);
        sendMessageToManger(user);
        return ApiResponse.success(ApiResponseCode.SUCCESS);
    }

    private void sendMessageToManger(ClientModel user) {
        NotificationModel notification = notificationService.createNotification(NotificationEnum.ADD_PERFORMANCE, user.getUsername());
        Set<Long> byHasAcceptPermission = clientService.queryReviewer(user.getDepartment().getId());
        byHasAcceptPermission.forEach(id -> {
            MessageModel messageModel = new MessageModel(user.getUsername(), id.toString(), WebsocketConstant.TOPIC.NOTIFICATION, notification);
            messageService.sendTo(messageModel);
        });
    }

    public ResponseEntity<ApiResponse> save(PerformanceRequest request) {
        ClientModel user = ClientIdentity.getUser();
        Optional<PerformanceModel> byId = performanceRepository.findById(request.getId());
        if (byId.isPresent()) {
            PerformanceModel model = byId.get();
            if (request.getEvent() != null) model.setEvent(request.getEvent());
            Long userId = request.getUserId();
            model.setUser(userId == null ? user : new ClientModel(userId));
            if (request.getFixedBonus() != null) model.setFixedBonus(request.getFixedBonus());
            if (request.getPerformanceRatio() != null) model.setPerformanceRatio(request.getPerformanceRatio());
            if (request.getEventTime() != null) model.setEventTime(request.getEventTime());
            if (request.getStatus() != null) model.setStatus(request.getStatus());
            performanceRepository.save(model);
            return ApiResponse.success(ApiResponseCode.SUCCESS);
        }
        return ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR);
    }

    public ResponseEntity<ApiResponse> remove(Long eventId) {
        int i = performanceRepository.updateStatus(eventId, StatusConstant.PENDING_NO, StatusConstant.REMOVED_NO);
        if (i == 1) return ApiResponse.success(ApiResponseCode.SUCCESS);
        return ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "removed:" + i);
    }

    public ResponseEntity<ApiResponse> accept(PerformanceAcceptRequest request) {
        int i = performanceRepository.updateStatus(request.getEventId(), StatusConstant.PENDING_NO, StatusConstant.APPROVED_NO);
        if (i == 1) {
            NotificationModel notification = notificationService.createNotification(NotificationEnum.ACCEPT_PERFORMANCE);
            ClientModel user = ClientIdentity.getUser();
            MessageModel messageModel = new MessageModel(user.getUsername(), request.getEventUserId().toString(), WebsocketConstant.TOPIC.NOTIFICATION, notification);
            messageService.sendTo(messageModel);
            return ApiResponse.success(ApiResponseCode.SUCCESS);
        }
        return ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "accept:" + i);
    }

    public ResponseEntity<ApiResponse> pendingList(PageRequestParam request) {
        ClientModel user = ClientIdentity.getUser();
        boolean isManager = user.getRoles().stream().anyMatch(model -> model.getLevel() == RoleConstant.LEVEL_3);
        Page<PerformanceModel> list;
        //管理權限全搜不分部門
        if (isManager) {
            list = performanceRepository.findAllByStatus(StatusConstant.PENDING_NO, user.getId(), request.getPage());
        } else {
            list = performanceRepository.findByStatusAndDepartment(user.getDepartment().getName(), StatusConstant.PENDING_NO, user.getId(), request.getPage());
        }
        return ApiResponse.success(new PageResponse<>(list, PerformanceResponse.class));
    }

    public ResponseEntity<ApiResponse> calculate(Long id) {
        List<PerformanceCalculateResponse> resultList = new ArrayList<>();
        Set<Object[]> set = performanceRepository.calculateByCreateYear(id, StatusConstant.APPROVED_NO);
        set.forEach(obj -> {
            PerformanceCalculateResponse performanceCalculateResponse = new PerformanceCalculateResponse();
            ClientModel user = (ClientModel) obj[0];
            performanceCalculateResponse.setUser(new ClientNameObject(user));
            performanceCalculateResponse.setFixedBonus((BigDecimal) obj[1]);
            performanceCalculateResponse.setPerformanceRatio((BigDecimal) obj[2]);
            performanceCalculateResponse.setSettleYear(String.valueOf(obj[3]));
            performanceCalculateResponse.setCount((Long) obj[4]);
            resultList.add(performanceCalculateResponse);
        });
        return ApiResponse.success(ApiResponseCode.SUCCESS, resultList);
    }
}
