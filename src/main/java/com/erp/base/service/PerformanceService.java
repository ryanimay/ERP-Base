package com.erp.base.service;

import com.erp.base.config.websocket.WebsocketConstant;
import com.erp.base.model.ClientIdentity;
import com.erp.base.model.MessageModel;
import com.erp.base.model.constant.NotificationEnum;
import com.erp.base.model.constant.RoleConstant;
import com.erp.base.model.constant.StatusConstant;
import com.erp.base.model.constant.response.ApiResponseCode;
import com.erp.base.model.dto.request.PageRequestParam;
import com.erp.base.model.dto.request.performance.PerformanceAcceptRequest;
import com.erp.base.model.dto.request.performance.PerformanceRequest;
import com.erp.base.model.dto.response.*;
import com.erp.base.model.dto.security.ClientIdentityDto;
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
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class PerformanceService {
    private PerformanceRepository performanceRepository;
    private MessageService messageService;
    private NotificationService notificationService;
    private ClientService clientService;
    private CacheService cacheService;
    @Autowired
    public void setCacheService(CacheService cacheService){
        this.cacheService = cacheService;
    }
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
        ClientIdentityDto user = ClientIdentity.getUser();
        if (user == null) {
            return ApiResponse.error(ApiResponseCode.ACCESS_DENIED, "User Identity Not Found");
        }
        PerformanceModel entity = request.toModel();
        Long userId = request.getUserId();
        entity.setUser(userId == null ? new ClientModel(user.getId()) : new ClientModel(userId));
        entity.setCreateBy(new ClientModel(user.getId()));
        performanceRepository.save(entity);
        sendMessageToManger(user);
        return ApiResponse.success(ApiResponseCode.SUCCESS);
    }

    private void sendMessageToManger(ClientIdentityDto user) {
        NotificationModel notification = notificationService.createNotification(NotificationEnum.ADD_PERFORMANCE, user.getUsername());
        Set<Long> byHasAcceptPermission = clientService.queryReviewer(user.getDepartment().getId());
        byHasAcceptPermission.forEach(id -> {
            MessageModel messageModel = new MessageModel(user.getUsername(), id.toString(), WebsocketConstant.TOPIC.NOTIFICATION, notification);
            messageService.sendTo(messageModel);
        });
    }

    public ResponseEntity<ApiResponse> save(PerformanceRequest request) {
        ClientIdentityDto user = ClientIdentity.getUser();
        if (user == null) {
            return ApiResponse.error(ApiResponseCode.ACCESS_DENIED, "User Identity Not Found");
        }
        Optional<PerformanceModel> byId = performanceRepository.findById(request.getId());
        if (byId.isPresent()) {
            PerformanceModel model = byId.get();
            if(model.getStatus() != StatusConstant.PENDING_NO) return ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "Can only modify performances in 'Pending' status.");
            if (request.getEvent() != null) model.setEvent(request.getEvent());
            Long userId = request.getUserId();
            model.setUser(userId == null ? new ClientModel(user.getId()) : new ClientModel(userId));
            if (request.getFixedBonus() != null) model.setFixedBonus(request.getFixedBonus());
            if (request.getPerformanceRatio() != null) model.setPerformanceRatio(request.getPerformanceRatio());
            if (request.getEventTime() != null) model.setEventTime(request.getEventTime());
            performanceRepository.save(model);
            return ApiResponse.success(ApiResponseCode.SUCCESS);
        }
        return ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "Performance not found: id[" + request.getId() + "]");
    }

    public ResponseEntity<ApiResponse> remove(Long eventId) {
        int i = performanceRepository.updateStatus(eventId, StatusConstant.PENDING_NO, StatusConstant.REMOVED_NO);
        if (i == 1) return ApiResponse.success(ApiResponseCode.SUCCESS);
        return ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "Performance id[" + eventId + "] not found");
    }

    public ResponseEntity<ApiResponse> accept(PerformanceAcceptRequest request) {
        ClientIdentityDto user = ClientIdentity.getUser();
        if (user == null) {
            return ApiResponse.error(ApiResponseCode.ACCESS_DENIED, "User Identity Not Found");
        }
        int i = performanceRepository.updateStatus(request.getEventId(), StatusConstant.PENDING_NO, StatusConstant.APPROVED_NO);
        if (i == 1) {
            NotificationModel notification = notificationService.createNotification(NotificationEnum.ACCEPT_PERFORMANCE);
            MessageModel messageModel = new MessageModel(user.getUsername(), request.getEventUserId().toString(), WebsocketConstant.TOPIC.NOTIFICATION, notification);
            messageService.sendTo(messageModel);
            return ApiResponse.success(ApiResponseCode.SUCCESS);
        }
        return ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "Performance id[" + request.getEventId() + "] not found");
    }

    public ResponseEntity<ApiResponse> pendingList(PageRequestParam request) {
        ClientIdentityDto user = ClientIdentity.getUser();
        if (user == null) {
            return ApiResponse.error(ApiResponseCode.ACCESS_DENIED, "User Identity Not Found");
        }
        ClientModel client = cacheService.getClient(user.getUsername());
        boolean isManager = client.getRoles().stream().anyMatch(model -> model.getLevel() == RoleConstant.LEVEL_3);
        Page<PerformanceModel> list;
        //管理權限全搜不分部門
        if (isManager) {
            list = performanceRepository.findAllByStatus(StatusConstant.PENDING_NO, user.getId(), request.getPage());
        } else {
            list = performanceRepository.findByStatusAndDepartment(user.getDepartment().getName(), StatusConstant.PENDING_NO, user.getId(), request.getPage());
        }
        return ApiResponse.success(new PageResponse<>(list, PerformanceResponse.class));
    }

    public ResponseEntity<ApiResponse> calculate(Long userId) {
        Set<Object[]> set = performanceRepository.calculateByCreateYear(userId, StatusConstant.APPROVED_NO);
        PerformanceCalculateResponse performanceCalculateResponse = new PerformanceCalculateResponse();
        set.forEach(obj -> {
            ClientModel user = (ClientModel) obj[0];
            performanceCalculateResponse.setUser(new ClientNameObject(user));
            performanceCalculateResponse.setFixedBonus((BigDecimal) obj[1]);
            performanceCalculateResponse.setPerformanceRatio((BigDecimal) obj[2]);
            performanceCalculateResponse.setSettleYear(String.valueOf(obj[3]));
            performanceCalculateResponse.setCount((Long) obj[4]);
        });
        return ApiResponse.success(ApiResponseCode.SUCCESS, performanceCalculateResponse);
    }
}
