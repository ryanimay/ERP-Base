package com.ex.erp.filter.jwt;

import com.ex.erp.dto.response.ApiResponseCode;
import com.ex.erp.dto.response.FilterExceptionResponse;
import com.ex.erp.model.ClientModel;
import com.ex.erp.tool.LogFactory;
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
import java.util.Map;

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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            try {
                isUserLockedOrDisabled(request, authentication);
            } catch (LockedException e) {
                exceptionResponse(e, response, ApiResponseCode.CLIENT_LOCKED);
                return;
            } catch (DisabledException e) {
                exceptionResponse(e, response, ApiResponseCode.CLIENT_DISABLED);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
    @SuppressWarnings("unchecked")
    private void isUserLockedOrDisabled(HttpServletRequest request, Authentication authentication) {
        Map<String, Object> principal = (Map<String, Object>) (authentication.getPrincipal());
        ClientModel client = (ClientModel) principal.get(JwtAuthenticationFilter.PRINCIPAL_CLIENT);
        String requestURL = request.getRequestURL().toString();
        if (!(requestURL.contains(CLIENT_LOCK_URL) || requestURL.contains(CLIENT_STATUS_URL))) checkClient(client);//驗證使用者狀態
    }

    /**
     * 每次呼叫接口驗證使用者狀態
     */
    private void checkClient(ClientModel client) {
        if (client.isLock()) throw new LockedException("User Locked");
        if (!client.isActive()) throw new DisabledException("User Disabled");
    }

    private void exceptionResponse(Exception e, HttpServletResponse response, ApiResponseCode code) throws IOException {
        LOG.error(e.getMessage());
        FilterExceptionResponse.error(response, code);
    }
}