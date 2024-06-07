package com.erp.base.filter;

import com.erp.base.config.websocket.WebsocketConstant;
import com.erp.base.model.MessageModel;
import com.erp.base.model.entity.NotificationModel;
import com.erp.base.service.ClientService;
import com.erp.base.service.MessageService;
import com.erp.base.service.NotificationService;
import com.erp.base.service.security.TokenService;
import com.erp.base.tool.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class UserHandshakeInterceptor implements HandshakeInterceptor {
    LogFactory LOG = new LogFactory(UserHandshakeInterceptor.class);
    private TokenService tokenService;
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
    public void setTokenService(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            if(isValidAuthToken(servletRequest.getServletRequest().getParameter("token"))){
                String userId = servletRequest.getServletRequest().getParameter("userId");
                attributes.put("userId", userId);
                return true;
            }
        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        //默認信息待修改，連接未完成不能這時候發送
//        String userId = ((ServletServerHttpRequest)request).getServletRequest().getParameter("userId");
//        sendNotification(Long.parseLong(userId));
    }

    //連結完先找歷史通知
    private void sendNotification(Long uid) {
        if(uid != null){
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
        }
    }

    private boolean isValidAuthToken(String authToken) {
        if (authToken == null) return false;
        authToken = authToken.replace(TokenService.TOKEN_PREFIX, "");
        try {
            tokenService.parseToken(authToken);
        } catch (Exception e) {
            LOG.error("User authentication failed");
            return false;
        }
        return true;
    }
}
