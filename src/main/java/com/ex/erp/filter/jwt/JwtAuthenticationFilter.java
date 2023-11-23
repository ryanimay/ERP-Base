package com.ex.erp.filter.jwt;

import com.ex.erp.dto.response.ClientResponse;
import com.ex.erp.service.cache.ClientCache;
import com.ex.erp.service.security.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private TokenService tokenService;
    private ClientCache clientCache;
    @Autowired
    public void setTokenService(TokenService tokenService){
        this.tokenService = tokenService;
    }
    @Autowired
    public void setClientCache(ClientCache clientCache){
        this.clientCache = clientCache;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(token != null){
            authenticationToken(token);
            refreshToken(request, response);
        }
        filterChain.doFilter(request, response);
    }

    /**
     * 驗證AccessToken
     * */
    private void authenticationToken(String token){
        String accessToken = token.replace("Bearer ", "");
        Map<String, Object> tokenDetail = tokenService.parseToken(accessToken);
        String username = (String) tokenDetail.get("username");
        ClientResponse client = clientCache.getClient(username);
        Collection<? extends GrantedAuthority> rolePermission = clientCache.getRolePermission(client.getRoleModel());
        Authentication authentication = new UsernamePasswordAuthenticationToken(client, null, rolePermission);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * 每次用戶有動作就刷新AccessToken時效，避免用到一半過期要重登
     * */
    private void refreshToken(HttpServletRequest request, HttpServletResponse response){
        String token = request.getHeader(TokenService.REFRESH_TOKEN);
        if (token != null) {
            String accessToken = tokenService.refreshAccessToken(token);
            response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
            response.setHeader(TokenService.REFRESH_TOKEN, token);
        }else{
            System.out.println(TokenService.REFRESH_TOKEN + " empty");
        }
    }
}
