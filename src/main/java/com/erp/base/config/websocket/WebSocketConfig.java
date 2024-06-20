package com.erp.base.config.websocket;

import com.erp.base.filter.UserHandshakeInterceptor;
import com.erp.base.service.CacheService;
import com.erp.base.service.security.TokenService;
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
    private TokenService tokenService;
    private CacheService cacheService;
    @Autowired
    public void setCacheService(CacheService cacheService) {
        this.cacheService = cacheService;
    }
    @Autowired
    public void setTokenService(TokenService tokenService) {
        this.tokenService = tokenService;
    }


    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker(WebsocketConstant.TOPIC.PREFIX);
        config.setApplicationDestinationPrefixes(WebsocketConstant.DESTINATION.PREFIX, WebsocketConstant.DESTINATION.USER);
        config.setUserDestinationPrefix(WebsocketConstant.DESTINATION.USER);
    }
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setHandshakeHandler(new DefaultHandshakeHandler() {
                    @Override
                    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
                        long userId = Long.parseLong((String) attributes.get(TokenService.TOKEN_PROPERTIES_UID));
                        return new CustomPrincipal(userId);
                    }
                })
                .setAllowedOriginPatterns("*")
                .withSockJS()
                .setInterceptors(new UserHandshakeInterceptor(tokenService, cacheService));
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