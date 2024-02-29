package com.erp.base.filter.jwt;

import com.erp.base.controller.Router;
import com.erp.base.enums.response.ApiResponseCode;
import com.erp.base.model.dto.response.FilterExceptionResponse;
import com.erp.base.model.dto.security.RolePermissionDto;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.entity.RoleModel;
import com.erp.base.service.CacheService;
import com.erp.base.service.security.TokenService;
import com.erp.base.service.security.UserDetailImpl;
import com.erp.base.tool.LogFactory;
import com.erp.base.tool.ObjectTool;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    LogFactory LOG = new LogFactory(JwtAuthenticationFilter.class);
    public static final String PRINCIPAL_CLIENT = "client";
    public static final String PRINCIPAL_LOCALE = "locale";
    private TokenService tokenService;
    private CacheService cacheService;
    private static final List<String> noRequiresAuthenticationList = new ArrayList<>();
    //不須驗證JWT的url
    static {
        noRequiresAuthenticationList.add(Router.CLIENT.OP_VALID);
        noRequiresAuthenticationList.add(Router.CLIENT.REGISTER);
        noRequiresAuthenticationList.add(Router.CLIENT.LOGIN);
        noRequiresAuthenticationList.add(Router.CLIENT.RESET_PASSWORD);
        noRequiresAuthenticationList.add(Router.ROLE.LIST);
    }
    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Autowired
    public void setTokenService(TokenService tokenService) {
        this.tokenService = tokenService;
    }
    @Autowired
    public void setCacheService(CacheService cacheService) {
        this.cacheService = cacheService;
    }
    /**
     * 針對已登入用戶後續存取api做token驗證和例外處理
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String url = ObjectTool.extractPath(request.getRequestURI()).replace(contextPath, "");
            String token = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (requiresAuthentication(url)) {
                if(token == null) throw new SignatureException("token is empty");
                authenticationToken(token);
            }
        } catch (SignatureException e) {
            try{
                LOG.info("AccessToken inValid, refresh");
                refreshToken(request, response);
            }catch (SignatureException e1){
                exceptionResponse(e1, response, ApiResponseCode.INVALID_SIGNATURE);
                return;
            }
        } catch (AccessDeniedException | MalformedJwtException e) {
            exceptionResponse(e, response, ApiResponseCode.ACCESS_DENIED);
            return;
        } catch (URISyntaxException e) {
            LOG.error("request path: [{0}] trans error", request.getRequestURI());
            FilterExceptionResponse.error(response, ApiResponseCode.ACCESS_DENIED);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private boolean requiresAuthentication(String url) {
        return !noRequiresAuthenticationList.contains(url);
    }

    /**
     * 驗證AccessToken
     */
    private void authenticationToken(String token) {
        String accessToken = token.replace(TokenService.TOKEN_PREFIX, "");
        tokenService.parseToken(accessToken);
    }

    private Collection<? extends GrantedAuthority> getRolePermission(Set<RoleModel> roles) {
        Set<RolePermissionDto> set = new HashSet<>();
        for (RoleModel role : roles) {
            set.addAll(cacheService.getRolePermission(role.getId()));
        }
        return set;
    }

    /**
     * AccessToken過期，驗證refreshToken是否過期，如未過期則一起刷新放行，過期就拋出
     */
    private void refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader(TokenService.REFRESH_TOKEN);
        if (token != null) {
            Map<String, Object> payload = tokenService.parseToken(token);
            String username = (String) payload.get(TokenService.TOKEN_PROPERTIES_USERNAME);
            String accessToken = tokenService.createToken(TokenService.REFRESH_TOKEN, username, TokenService.ACCESS_TOKEN_EXPIRE_TIME);
            response.setHeader(HttpHeaders.AUTHORIZATION, TokenService.TOKEN_PREFIX + accessToken);
            response.setHeader(TokenService.REFRESH_TOKEN, token);

            //刷新Token時進行權限刷新
            SecurityContext context = SecurityContextHolder.getContext();
            ClientModel client = cacheService.getClient(username);
            UserDetailImpl userDetail = new UserDetailImpl(client, cacheService);
            Collection<? extends GrantedAuthority> rolePermission = getRolePermission(client.getRoles());
            Map<String, Object> principalMap = userDetail.getDataMap();
            principalMap.put(PRINCIPAL_CLIENT, client);
            Authentication authentication = new UsernamePasswordAuthenticationToken(principalMap, null, rolePermission);
            context.setAuthentication(authentication);
        } else {
            LOG.warn(TokenService.REFRESH_TOKEN + " empty");
            throw new SignatureException("");
        }
    }

    private void exceptionResponse(Exception e, HttpServletResponse response, ApiResponseCode code) throws IOException {
        LOG.error(e.getMessage());
        FilterExceptionResponse.error(response, code);
    }
}
