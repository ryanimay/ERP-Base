package com.erp.base.filter;

import com.erp.base.model.ClientIdentity;
import com.erp.base.model.entity.UserModel;
import com.erp.base.tool.LogFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
@Component
public class UserHandshakeInterceptor implements HandshakeInterceptor {
    LogFactory LOG = new LogFactory(UserHandshakeInterceptor.class);

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        UserModel user = ClientIdentity.getUser();
        if (user == null) {
            LOG.warn("UserNotFound");
            return false;
        }
        attributes.put("simpUser", user.getId());//方便後續靠userId送資料
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
