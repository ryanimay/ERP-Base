package com.erp.base.filter.jwt;

import com.erp.base.controller.Router;
import com.erp.base.enums.response.ApiResponseCode;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.service.CacheService;
import com.erp.base.service.security.TokenService;
import com.erp.base.tool.ObjectTool;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {
    @Spy
    private TokenService tokenService;
    @Mock
    private CacheService cacheService;
    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private FilterChain filterChain;
    private static final String contextPath = "/erp_base";


    @BeforeEach
    void setUp() {
        jwtAuthenticationFilter = new JwtAuthenticationFilter();
        ReflectionTestUtils.setField(jwtAuthenticationFilter, "contextPath", contextPath);
        MockitoAnnotations.openMocks(this);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = Mockito.mock(FilterChain.class);
        request.setRequestURI("http://localhost:8080" + contextPath + Router.CLIENT.CLIENT_STATUS);
    }

    @Test
    @DisplayName("JWT驗證_url不須驗證_成功")
    void testDenyPermission_unRequiresAuthentication_pass() throws ServletException, IOException {
        request.setRequestURI("http://localhost:8080" + contextPath + Router.CLIENT.OP_VALID);
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        Mockito.verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("JWT驗證_通過驗證_成功")
    void testDenyPermission_pass() throws ServletException, IOException {
        Map<String, Object> map = new HashMap<>();
        map.put(TokenService.TOKEN_PROPERTIES_USERNAME, "test");
        request.addHeader(HttpHeaders.AUTHORIZATION, "testToken");
        Mockito.doReturn(map).when(tokenService).parseToken(Mockito.any());
        Mockito.when(cacheService.getClient(Mockito.any())).thenReturn(new ClientModel(1));
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        Mockito.verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("JWT驗證_沒token_錯誤")
    void testDenyPermission_noToken_error() throws ServletException, IOException {
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
        Assertions.assertEquals("application/json; charset=utf-8", response.getContentType());
        ApiResponse expectedApiResponse = new ApiResponse(ApiResponseCode.INVALID_SIGNATURE);
        String expectedErrorMessage = ObjectTool.toJson(expectedApiResponse);
        Assertions.assertEquals(expectedErrorMessage, response.getContentAsString());
    }

    @Test
    @DisplayName("JWT驗證_token格式問題_錯誤")
    void testDenyPermission_malformedToken_error() throws ServletException, IOException {
        request.addHeader(HttpHeaders.AUTHORIZATION, "testToken");
        Mockito.doThrow(new MalformedJwtException("")).when(tokenService).parseToken(Mockito.any());
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
        Assertions.assertEquals("application/json; charset=utf-8", response.getContentType());
        ApiResponse expectedApiResponse = new ApiResponse(ApiResponseCode.ACCESS_DENIED);
        String expectedErrorMessage = ObjectTool.toJson(expectedApiResponse);
        Assertions.assertEquals(expectedErrorMessage, response.getContentAsString());
    }

    @Test
    @DisplayName("JWT驗證_refreshToken刷新_成功")
    void testDenyPermission_refreshToken_pass() throws ServletException, IOException {
        Map<String, Object> map = new HashMap<>();
        map.put(TokenService.TOKEN_PROPERTIES_USERNAME, "test");
        request.addHeader(TokenService.REFRESH_TOKEN, "testToken");
        Mockito.doReturn(map).when(tokenService).parseToken(Mockito.any());
        Mockito.when(cacheService.getClient(Mockito.any())).thenReturn(new ClientModel(1));
        Mockito.doReturn("testToken").when(tokenService).createToken(TokenService.REFRESH_TOKEN, "test", TokenService.ACCESS_TOKEN_EXPIRE_TIME);
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        Assertions.assertEquals(TokenService.TOKEN_PREFIX + "testToken", response.getHeader(HttpHeaders.AUTHORIZATION));
        Mockito.verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("JWT驗證_url錯誤_錯誤")
    void testDenyPermission_urlError_error() throws ServletException, IOException {
        request.setRequestURI("// // /");
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
        Assertions.assertEquals("application/json; charset=utf-8", response.getContentType());

        ApiResponse expectedApiResponse = new ApiResponse(ApiResponseCode.ACCESS_DENIED);
        String expectedErrorMessage = ObjectTool.toJson(expectedApiResponse);
        Assertions.assertEquals(expectedErrorMessage, response.getContentAsString());
    }
}