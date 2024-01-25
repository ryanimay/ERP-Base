package com.erp.base.filter.jwt;

import com.erp.base.enums.response.ApiResponseCode;
import com.erp.base.model.dto.response.FilterExceptionResponse;
import com.erp.base.service.CacheService;
import com.erp.base.tool.LogFactory;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class DenyPermissionFilter extends OncePerRequestFilter {
    LogFactory LOG = new LogFactory(DenyPermissionFilter.class);
    private CacheService cacheService;

    @Autowired
    public void setClientCache(CacheService cacheService) {
        this.cacheService = cacheService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestedUrl = request.getServletPath();

        if (requestedUrl != null) {
            //檢查路徑狀態是否為deny
            Boolean status = cacheService.permissionStatus(requestedUrl);
            if (status == null) {
                LOG.error("request path: [{0}] is Disable", requestedUrl);
                FilterExceptionResponse.error(response, ApiResponseCode.ACCESS_DENIED);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
