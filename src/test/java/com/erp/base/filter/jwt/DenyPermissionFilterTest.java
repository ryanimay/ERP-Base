package com.erp.base.filter.jwt;

import com.erp.base.controller.Router;
import com.erp.base.model.constant.response.ApiResponseCode;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.service.CacheService;
import com.erp.base.tool.ObjectTool;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

@ExtendWith(MockitoExtension.class)
class DenyPermissionFilterTest {
    @Mock
    private CacheService cacheService;
    @InjectMocks
    private DenyPermissionFilter denyPermissionFilter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        denyPermissionFilter = new DenyPermissionFilter();
        MockitoAnnotations.openMocks(this);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = Mockito.mock(FilterChain.class);
        request.setRequestURI("http://localhost:8080/erp_base" + Router.CLIENT.OP_VALID);
    }

    @Test
    @DisplayName("權限狀態filter_通過_成功")
    void testDenyPermission_pass() throws ServletException, IOException {
        Mockito.when(cacheService.permissionStatus(Mockito.any())).thenReturn(Boolean.TRUE);
        denyPermissionFilter.doFilterInternal(request, response, filterChain);
        Mockito.verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("權限狀態filter_url錯誤")
    void testDenyPermission_error() throws ServletException, IOException {
        request.setRequestURI("// // /");
        denyPermissionFilter.doFilterInternal(request, response, filterChain);
        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
        Assertions.assertEquals("application/json; charset=utf-8", response.getContentType());

        ApiResponse expectedApiResponse = new ApiResponse(ApiResponseCode.ACCESS_DENIED);
        String expectedErrorMessage = ObjectTool.toJson(expectedApiResponse);
        Assertions.assertEquals(expectedErrorMessage, response.getContentAsString());
    }

    @Test
    @DisplayName("權限狀態filter_狀態deny_錯誤")
    void testDenyPermission_permissionDeny() throws ServletException, IOException {
        Mockito.when(cacheService.permissionStatus(Mockito.any())).thenReturn(Boolean.FALSE);
        denyPermissionFilter.doFilterInternal(request, response, filterChain);

        ApiResponse expectedApiResponse = new ApiResponse(ApiResponseCode.ACCESS_DENIED);
        String expectedErrorMessage = ObjectTool.toJson(expectedApiResponse);
        Assertions.assertEquals(expectedErrorMessage, response.getContentAsString());
    }
}