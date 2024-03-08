package com.erp.base.filter.jwt;

import com.erp.base.config.security.SecurityConfig;
import com.erp.base.model.constant.response.ApiResponseCode;
import com.erp.base.model.dto.response.FilterExceptionResponse;
import com.erp.base.service.security.UserDetailImpl;
import com.erp.base.tool.LogFactory;
import com.erp.base.tool.ObjectTool;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.AccessDeniedException;

@Component
public class UserStatusFilter extends OncePerRequestFilter {
    LogFactory LOG = new LogFactory(UserStatusFilter.class);
    public static final String CLIENT_LOCK_URL = "/client/clientLock";
    public static final String CLIENT_STATUS_URL = "/client/clientStatus";

    /**
     * 針對已登入用戶後續存取api做狀態驗證和例外處理
     * */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String url;
        try {
            url = ObjectTool.extractPath(request.getRequestURI());
        } catch (URISyntaxException e) {
            LOG.error("request path: [{0}] trans error", request.getRequestURI());
            exceptionResponse(e, response, ApiResponseCode.ACCESS_DENIED);
            return;
        }
        //不包含在公開API才需要驗證
        if(!SecurityConfig.noRequiresAuthenticationSet.contains(url)){
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                try {
                    isUserLockedOrDisabled(url, authentication);
                } catch (LockedException e) {
                    exceptionResponse(e, response, ApiResponseCode.CLIENT_LOCKED);
                    return;
                } catch (DisabledException e) {
                    exceptionResponse(e, response, ApiResponseCode.CLIENT_DISABLED);
                    return;
                }
            }else {
                exceptionResponse(new AccessDeniedException("authentication null"), response, ApiResponseCode.ACCESS_DENIED);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private void isUserLockedOrDisabled(String requestURL, Authentication authentication) {
        UserDetailImpl principal = ObjectTool.convert(authentication.getPrincipal(), UserDetailImpl.class);
        if (!(requestURL.contains(CLIENT_LOCK_URL) || requestURL.contains(CLIENT_STATUS_URL))) checkClient(principal);//驗證使用者狀態
    }

    /**
     * 每次呼叫接口驗證使用者狀態
     */
    private void checkClient(UserDetailImpl userDetail) {
        if (!userDetail.isAccountNonLocked()) throw new LockedException("User Locked");
        if (!userDetail.isEnabled()) throw new DisabledException("User Disabled");
    }

    private void exceptionResponse(Exception e, HttpServletResponse response, ApiResponseCode code) throws IOException {
        LOG.error(e.getMessage());
        FilterExceptionResponse.error(response, code);
    }
}