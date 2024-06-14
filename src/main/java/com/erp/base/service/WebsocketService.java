package com.erp.base.service;

import com.erp.base.config.websocket.WebsocketConstant;
import com.erp.base.model.MessageModel;
import com.erp.base.model.entity.NotificationModel;
import com.erp.base.tool.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
public class WebsocketService {
    LogFactory LOG = new LogFactory(WebsocketService.class);
    private final NotificationService notificationService;
    private final ClientService clientService;
    private final MessageService messageService;

    @Autowired
    public WebsocketService(NotificationService notificationService, ClientService clientService, MessageService messageService) {
        this.notificationService = notificationService;
        this.clientService = clientService;
        this.messageService = messageService;
    }

    //找所有歷史通知
    public void sendNotification(String uid) {
        Set<NotificationModel> notifications = notificationService.findGlobal();//全域通知
        Set<NotificationModel> userNotification = clientService.findNotificationByUserId(Long.valueOf(uid));//個人通知
        notifications.addAll(userNotification);//個人通知
        //整理排序
        List<NotificationModel> notificationList = notifications.stream()
                .sorted(Comparator
                        .comparing(NotificationModel::isStatus).reversed()
                        .thenComparing(NotificationModel::getCreateTime).reversed()
                ).toList();
        messageService.sendTo(new MessageModel(WebsocketConstant.FROM.SYSTEM, uid, WebsocketConstant.TOPIC.NOTIFICATION, notificationList));
        LOG.info("send notification");
    }
}
