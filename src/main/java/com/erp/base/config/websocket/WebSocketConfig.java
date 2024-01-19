package com.erp.base.config.websocket;

import com.erp.base.filter.UserHandshakeInterceptor;
import com.erp.base.model.ClientIdentity;
import com.erp.base.model.entity.ClientModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import javax.security.auth.Subject;
import java.security.Principal;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final UserHandshakeInterceptor userHandshakeInterceptor;
    @Autowired
    public WebSocketConfig(UserHandshakeInterceptor userHandshakeInterceptor){
        this.userHandshakeInterceptor = userHandshakeInterceptor;
    }


    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker(WebsocketConstant.TOPIC.PREFIX);
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setHandshakeHandler(new DefaultHandshakeHandler() {
                    @Override
                    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
                        ClientModel user = ClientIdentity.getUser();
                        long userId = 0L;
                        if (user != null) {
                            userId = user.getId();
                        }
                        return new CustomPrincipal(userId);
                    }
                })
                .setAllowedOrigins("*")
                .withSockJS()
                .setInterceptors(userHandshakeInterceptor);
    }

    private class CustomPrincipal implements Principal{

        private final long userId;

        public CustomPrincipal(long userId) {
            this.userId = userId;
        }

        @Override
        public String getName() {
            return Long.toString(userId);
        }

        @Override
        public boolean implies(Subject subject) {
            return Principal.super.implies(subject);
        }
    }
}