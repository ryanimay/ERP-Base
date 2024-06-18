package com.erp.base.filter;

import com.erp.base.model.dto.security.ClientIdentityDto;
import com.erp.base.service.CacheService;
import com.erp.base.service.security.TokenService;
import com.erp.base.service.security.UserDetailImpl;
import com.erp.base.tool.LogFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Collection;
import java.util.Map;

public class UserHandshakeInterceptor implements HandshakeInterceptor {
    LogFactory LOG = new LogFactory(UserHandshakeInterceptor.class);
    private final TokenService tokenService;
    private final CacheService cacheService;

    public UserHandshakeInterceptor(TokenService tokenService, CacheService cacheService) {
        this.tokenService = tokenService;
        this.cacheService = cacheService;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            return verifyToken(servletRequest.getServletRequest().getParameter("token"), attributes);
        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        LOG.info("Socket Connected");
    }

    private boolean verifyToken(String authToken, Map<String, Object> attributes) {
        if (authToken == null) return false;
        authToken = authToken.replace(TokenService.TOKEN_PREFIX, "");
        try {
            Map<String, Object> payload = tokenService.parseToken(authToken);
            String userId = String.valueOf(payload.get(TokenService.TOKEN_PROPERTIES_UID));
            createAuthentication(userId);
            attributes.put(TokenService.TOKEN_PROPERTIES_UID, userId);
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("User authentication failed");
            return false;
        }
        return true;
    }

    private void createAuthentication(String userId) {
        ClientIdentityDto client = cacheService.getClient(Long.valueOf(userId));
        if(client == null) return;
        UserDetailImpl userDetail = new UserDetailImpl(client, cacheService);
        Collection<? extends GrantedAuthority> rolePermission = userDetail.getAuthorities();
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetail, null, rolePermission);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
