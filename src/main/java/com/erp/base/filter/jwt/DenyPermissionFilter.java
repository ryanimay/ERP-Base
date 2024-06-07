package com.erp.base.filter.jwt;

import com.erp.base.model.constant.response.ApiResponseCode;
import com.erp.base.model.dto.response.FilterExceptionResponse;
import com.erp.base.service.CacheService;
import com.erp.base.tool.LogFactory;
import com.erp.base.tool.ObjectTool;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URISyntaxException;

public class DenyPermissionFilter extends OncePerRequestFilter {
    LogFactory LOG = new LogFactory(DenyPermissionFilter.class);
    private final CacheService cacheService;

    public DenyPermissionFilter(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestedUrl;
        try {
            requestedUrl = ObjectTool.extractPath(request.getRequestURI());
        } catch (URISyntaxException e) {
            LOG.error("request path: [{0}] trans error", request.getRequestURI());
            FilterExceptionResponse.error(response, ApiResponseCode.ACCESS_DENIED);
            return;
        }

        if (!StringUtils.isEmpty(requestedUrl) && notEqualSwaggerUrl(requestedUrl) && notEqualWebsocketUrl(requestedUrl)) {
            //檢查路徑狀態是否為deny
            Boolean status = cacheService.permissionStatus(requestedUrl);
            if (status == null || Boolean.FALSE.equals(status)) {
                LOG.error("request path: [{0}] is Disable", requestedUrl);
                FilterExceptionResponse.error(response, ApiResponseCode.ACCESS_DENIED);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean notEqualSwaggerUrl(String requestedUrl) {
        return !requestedUrl.contains("swagger");
    }

    private boolean notEqualWebsocketUrl(String requestedUrl) {
        return !requestedUrl.contains("/ws");
    }
}
