package com.erp.base.service;

import com.erp.base.config.websocket.WebsocketConstant;
import com.erp.base.model.ClientIdentity;
import com.erp.base.model.MessageModel;
import com.erp.base.model.dto.security.ClientIdentityDto;
import com.erp.base.model.entity.NotificationModel;
import com.erp.base.service.security.UserDetailImpl;
import com.erp.base.tool.LogFactory;
import com.erp.base.tool.ObjectTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public void sendNotification() {
        ClientIdentityDto user = ClientIdentity.getUser();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        System.out.println(ObjectTool.toJson(ObjectTool.convert(principal, UserDetailImpl.class)));
        if(user != null){
            long uid = user.getId();
            String userId = Long.toString(uid);
            Set<NotificationModel> notifications = notificationService.findGlobal();//全域通知
            Set<NotificationModel> userNotification = clientService.findNotificationByUserId(uid);//個人通知
            notifications.addAll(userNotification);//個人通知
            //整理排序
            List<NotificationModel> notificationList = notifications.stream()
                    .sorted(Comparator
                            .comparing(NotificationModel::isStatus).reversed()
                            .thenComparing(NotificationModel::getCreateTime).reversed()
                    ).toList();
            messageService.sendTo(new MessageModel(WebsocketConstant.FROM.SYSTEM, userId, WebsocketConstant.TOPIC.NOTIFICATION, notificationList));
            LOG.info("send notification");
        }else {
            LOG.info("userEmpty");
        }
    }
}
