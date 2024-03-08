package com.erp.base.filter.jwt;

import com.erp.base.controller.Router;
import com.erp.base.model.constant.response.ApiResponseCode;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.service.security.UserDetailImpl;
import com.erp.base.tool.ObjectTool;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

@ExtendWith(MockitoExtension.class)
class UserStatusFilterTest {
    @InjectMocks
    private UserStatusFilter userStatusFilter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private FilterChain filterChain;


    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        userStatusFilter = new UserStatusFilter();
        MockitoAnnotations.openMocks(this);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = Mockito.mock(FilterChain.class);
        request.setRequestURI("http://localhost:8080/erp_base" + Router.CLIENT.GET_CLIENT);
    }

    @Test
    @DisplayName("用戶狀態驗證_url錯誤_錯誤")
    void testDenyPermission_urlError_error() throws ServletException, IOException {
        request.setRequestURI("// // /");
        userStatusFilter.doFilterInternal(request, response, filterChain);
        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
        Assertions.assertEquals("application/json; charset=utf-8", response.getContentType());

        ApiResponse expectedApiResponse = new ApiResponse(ApiResponseCode.ACCESS_DENIED);
        String expectedErrorMessage = ObjectTool.toJson(expectedApiResponse);
        Assertions.assertEquals(expectedErrorMessage, response.getContentAsString());
    }

    @Test
    @DisplayName("用戶狀態驗證_成功")
    void userStatus_ok() throws ServletException, IOException {
        UserDetailImpl userDetail = new UserDetailImpl();
        userDetail.setClientModel(new ClientModel());
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetail, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        userStatusFilter.doFilterInternal(request, response, filterChain);
        Mockito.verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("用戶狀態驗證_用戶鎖定_失敗")
    void userStatus_userLock_error() throws ServletException, IOException {
        UserDetailImpl userDetail = new UserDetailImpl();
        ClientModel clientModel = new ClientModel();
        clientModel.setLock(true);
        userDetail.setClientModel(clientModel);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetail, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        userStatusFilter.doFilterInternal(request, response, filterChain);
        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
        Assertions.assertEquals("application/json; charset=utf-8", response.getContentType());
        ApiResponse expectedApiResponse = new ApiResponse(ApiResponseCode.CLIENT_LOCKED);
        String expectedErrorMessage = ObjectTool.toJson(expectedApiResponse);
        Assertions.assertEquals(expectedErrorMessage, response.getContentAsString());
    }

    @Test
    @DisplayName("用戶狀態驗證_用戶關閉_失敗")
    void userStatus_userDisable_error() throws ServletException, IOException {
        UserDetailImpl userDetail = new UserDetailImpl();
        ClientModel clientModel = new ClientModel();
        clientModel.setActive(false);
        userDetail.setClientModel(clientModel);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetail, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        userStatusFilter.doFilterInternal(request, response, filterChain);
        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
        Assertions.assertEquals("application/json; charset=utf-8", response.getContentType());
        ApiResponse expectedApiResponse = new ApiResponse(ApiResponseCode.CLIENT_DISABLED);
        String expectedErrorMessage = ObjectTool.toJson(expectedApiResponse);
        Assertions.assertEquals(expectedErrorMessage, response.getContentAsString());
    }

    @Test
    @DisplayName("用戶狀態驗證_未經驗證_失敗")
    void userStatus_authenticationNull_error() throws ServletException, IOException {
        userStatusFilter.doFilterInternal(request, response, filterChain);
        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
        Assertions.assertEquals("application/json; charset=utf-8", response.getContentType());
        ApiResponse expectedApiResponse = new ApiResponse(ApiResponseCode.ACCESS_DENIED);
        String expectedErrorMessage = ObjectTool.toJson(expectedApiResponse);
        Assertions.assertEquals(expectedErrorMessage, response.getContentAsString());
    }
}