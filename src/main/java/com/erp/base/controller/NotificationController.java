package com.erp.base.controller;

import com.erp.base.model.dto.request.IdRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "NotificationController", description = "系統通知相關API")
public class NotificationController {

    private NotificationService notificationService;

    @Autowired
    public void setNotificationService(NotificationService notificationService){
        this.notificationService = notificationService;
    }

    //設置菜單權限展示用
    @PostMapping(Router.NOTIFICATION.STATUS)
    @Operation(summary = "更新通知狀態")
    public ResponseEntity<ApiResponse> notificationStatus(@RequestBody IdRequest request) {
        return notificationService.notificationStatus(request);
    }
}
