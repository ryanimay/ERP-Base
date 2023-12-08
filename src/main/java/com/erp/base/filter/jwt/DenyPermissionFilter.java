package com.erp.base.filter.jwt;

import com.erp.base.dto.response.FilterExceptionResponse;
import com.erp.base.enums.response.ApiResponseCode;
import com.erp.base.service.PermissionService;
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
    private PermissionService permissionService;
    @Autowired
    public void setPermissionService(PermissionService permissionService){
        this.permissionService = permissionService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestedUrl = request.getServletPath();

        if (requestedUrl != null){
            //檢查路徑狀態是否為deny
            ApiResponseCode code = permissionService.checkPermissionIfDeny(requestedUrl);
            if(code != null) {
                LOG.error("request path: [{0}] is Disable", requestedUrl);
                FilterExceptionResponse.error(response, code);
            }
        }

        filterChain.doFilter(request, response);
    }
}
