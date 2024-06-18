package com.erp.base.filter.jwt;

import com.erp.base.config.security.SecurityConfig;
import com.erp.base.model.constant.response.ApiResponseCode;
import com.erp.base.model.dto.response.FilterExceptionResponse;
import com.erp.base.model.dto.security.ClientIdentityDto;
import com.erp.base.service.CacheService;
import com.erp.base.service.security.TokenService;
import com.erp.base.service.security.UserDetailImpl;
import com.erp.base.tool.LogFactory;
import com.erp.base.tool.ObjectTool;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Map;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    LogFactory LOG = new LogFactory(JwtAuthenticationFilter.class);
    private final TokenService tokenService;
    private final CacheService cacheService;

    public JwtAuthenticationFilter(TokenService tokenService, CacheService cacheService) {
        this.tokenService = tokenService;
        this.cacheService = cacheService;
    }

    /**
     * 針對已登入用戶後續存取api做token驗證和例外處理
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String url = ObjectTool.extractPath(request.getRequestURI());
            if (requiresAuthentication(url) && notEqualWebsocketUrl(url)) {
                String token = request.getHeader(HttpHeaders.AUTHORIZATION);
                if(token == null || cacheService.existsTokenBlackList(token)) throw new AccessDeniedException("token error");
                Map<String, Object> payload = authenticationToken(token);
                String uid = String.valueOf(payload.get(TokenService.TOKEN_PROPERTIES_UID));
                createAuthentication(Long.parseLong(uid), request);
            }else{
                createEmptyUserAuth(request);
            }
        } catch (ExpiredJwtException e) {
            try{
                LOG.info("AccessToken inValid, refresh: " + e.getMessage());
                refreshToken(request, response);
            }catch (SignatureException | ExpiredJwtException e1){
                exceptionResponse(e1, response, ApiResponseCode.INVALID_SIGNATURE);
                return;
            }
        } catch (SignatureException | AccessDeniedException | MalformedJwtException e) {
            exceptionResponse(e, response, ApiResponseCode.ACCESS_DENIED);
            return;
        } catch (URISyntaxException e) {
            LOG.error("request path: [{0}] trans error", request.getRequestURI());
            exceptionResponse(e, response, ApiResponseCode.ACCESS_DENIED);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private boolean requiresAuthentication(String url) {
        return !SecurityConfig.noRequiresAuthenticationSet.contains(url);
    }

    /**
     * 驗證AccessToken
     */
    private Map<String, Object> authenticationToken(String token) {
        String accessToken = token.replace(TokenService.TOKEN_PREFIX, "");
        return tokenService.parseToken(accessToken);
    }

    /**
     * AccessToken過期，驗證refreshToken是否過期，如未過期則一起刷新放行，過期就拋出
     * 舊Token加黑名單
     */
    private void refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader(TokenService.REFRESH_TOKEN);
        //不在黑名單才可刷新
        if (token != null && !cacheService.existsTokenBlackList(token)) {
            Map<String, Object> payload = tokenService.parseToken(token);
            String id = String.valueOf(payload.get(TokenService.TOKEN_PROPERTIES_UID));
            long uid = Long.parseLong(id);
            String accessToken = tokenService.createToken(TokenService.ACCESS_TOKEN, uid, TokenService.ACCESS_TOKEN_EXPIRE_TIME);
            response.setHeader(HttpHeaders.AUTHORIZATION, TokenService.TOKEN_PREFIX + accessToken);
            String refreshToken = tokenService.createToken(TokenService.REFRESH_TOKEN, uid, TokenService.REFRESH_TOKEN_EXPIRE_TIME);
            response.setHeader(TokenService.REFRESH_TOKEN, refreshToken);
            cacheService.addTokenBlackList(token);
            createAuthentication(uid, request);
        } else {
            LOG.warn(TokenService.REFRESH_TOKEN + " empty");
            throw new SignatureException("");
        }
    }

    private void createAuthentication(Long id, HttpServletRequest request) {
        String lang = request.getHeader("User-Lang");
        ClientIdentityDto client = cacheService.getClient(id);
        if(client == null) return;
        UserDetailImpl userDetail = lang == null ? new UserDetailImpl(client, cacheService) : new UserDetailImpl(lang, client, cacheService);
        Collection<? extends GrantedAuthority> rolePermission = userDetail.getAuthorities();
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetail, null, rolePermission);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * 在lang不為null的情況下
     * 創建不需驗證的api用的auth
     * 只放帶有lang的principal，用於控制接口返回語系
     * */
    private void createEmptyUserAuth(HttpServletRequest request) {
        String lang = request.getHeader("User-Lang");
        if(lang != null){
            UserDetailImpl userDetail = new UserDetailImpl(lang, new ClientIdentityDto(), cacheService);
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetail, null, null);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }

    private void exceptionResponse(Exception e, HttpServletResponse response, ApiResponseCode code) throws IOException {
        LOG.error(e.getMessage());
        FilterExceptionResponse.error(response, code);
    }

    private boolean notEqualWebsocketUrl(String requestedUrl) {
        return !requestedUrl.contains("/ws");
    }
}
