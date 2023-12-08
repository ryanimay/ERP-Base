package com.erp.base.filter.jwt;

import com.erp.base.dto.response.FilterExceptionResponse;
import com.erp.base.model.ClientModel;
import com.erp.base.enums.response.ApiResponseCode;
import com.erp.base.service.cache.ClientCache;
import com.erp.base.service.security.TokenService;
import com.erp.base.tool.LogFactory;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    LogFactory LOG = new LogFactory(JwtAuthenticationFilter.class);
    public static final String PRINCIPAL_CLIENT = "client";
    public static final String PRINCIPAL_LOCALE = "locale";
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

    /**
     * 針對已登入用戶後續存取api做token驗證和例外處理
     * */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            String token = request.getHeader(HttpHeaders.AUTHORIZATION);
            if(token != null){
                authenticationToken(token);
                refreshToken(request, response);
            }
        }catch (SignatureException e){
            exceptionResponse(e, response, ApiResponseCode.INVALID_SIGNATURE);
            return;
        }catch (AccessDeniedException e){
            exceptionResponse(e, response, ApiResponseCode.ACCESS_DENIED);
            return;
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
        ClientModel client = clientCache.getClient(username);
        Collection<? extends GrantedAuthority> rolePermission = clientCache.getRolePermission(client.getRole());
        HashMap<String, Object> principalMap = new HashMap<>();
        principalMap.put(PRINCIPAL_CLIENT, client);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principalMap, null, rolePermission);
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
            LOG.warn(TokenService.REFRESH_TOKEN + " empty");
        }
    }

    private void exceptionResponse(Exception e, HttpServletResponse response, ApiResponseCode code) throws IOException {
        LOG.error(e.getMessage());
        FilterExceptionResponse.error(response, code);
    }
}
