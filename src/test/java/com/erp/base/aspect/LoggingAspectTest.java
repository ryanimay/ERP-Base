package com.erp.base.aspect;

import com.erp.base.controller.Router;
import com.erp.base.model.ClientIdentity;
import com.erp.base.model.constant.response.ApiResponseCode;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.dto.security.ClientIdentityDto;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.service.LogService;
import com.erp.base.tool.ObjectTool;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@ExtendWith(MockitoExtension.class)
class LoggingAspectTest {
    private static final String testUrl = "http://localhost:8080/erp_base/client/opValid";
    private static final String testIP = "127.0.0.1";
    private static final Object[] testArg = {"arg"};
    private static final ClientModel client = new ClientModel();
    @Mock
    private LogService logService;
    @InjectMocks
    private LoggingAspect loggingAspect;
    private ResponseEntity<ApiResponse> response;
    private static ProceedingJoinPoint joinPoint;
    private static ServletRequestAttributes servletRequestAttributes;

    @BeforeAll
    static void beforeAll() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn(testUrl);
        Mockito.when(request.getHeader(Mockito.any())).thenReturn(testIP);
        servletRequestAttributes = Mockito.mock(ServletRequestAttributes.class);
        Mockito.when(servletRequestAttributes.getRequest()).thenReturn(request);
        joinPoint = Mockito.mock(ProceedingJoinPoint.class);
        Mockito.when(joinPoint.getArgs()).thenReturn(testArg);
        client.setUsername("testName");
    }

    @Test
    @DisplayName("日誌AOP_返回成功")
    void logAround_success() throws Throwable {
        try (MockedStatic<RequestContextHolder> requestContextHolderMockedStatic = Mockito.mockStatic(RequestContextHolder.class)) {
            requestContextHolderMockedStatic.when(RequestContextHolder::currentRequestAttributes).thenReturn(servletRequestAttributes);
            try (MockedStatic<ClientIdentity> clientIdentityMockedStatic = Mockito.mockStatic(ClientIdentity.class)) {
                clientIdentityMockedStatic.when(ClientIdentity::getUser).thenReturn(new ClientIdentityDto(client));
                response = ApiResponse.success(ApiResponseCode.SUCCESS);
                Mockito.when(joinPoint.proceed()).thenReturn(response);

                loggingAspect.logAround(joinPoint);

                Mockito.verify(logService).save(Mockito.argThat(model -> {
                    Assertions.assertEquals(Router.CLIENT.OP_VALID, model.getUrl());
                    Assertions.assertEquals(ObjectTool.toJson(testArg), model.getParams());
                    Assertions.assertEquals(client.getUsername(), model.getUserName());
                    Assertions.assertEquals(testIP, model.getIp());
                    Assertions.assertEquals(true, model.getStatus());
                    Assertions.assertNotNull(response.getBody());
                    Assertions.assertEquals(response.getBody().getMessage(), model.getResult());
                    return true;
                }));
            }
        }
    }

    @Test
    @DisplayName("日誌AOP_返回錯誤")
    void logAround_error() throws Throwable {
        try (MockedStatic<RequestContextHolder> requestContextHolderMockedStatic = Mockito.mockStatic(RequestContextHolder.class)) {
            requestContextHolderMockedStatic.when(RequestContextHolder::currentRequestAttributes).thenReturn(servletRequestAttributes);
            try (MockedStatic<ClientIdentity> clientIdentityMockedStatic = Mockito.mockStatic(ClientIdentity.class)) {
                clientIdentityMockedStatic.when(ClientIdentity::getUser).thenReturn(new ClientIdentityDto(client));
                response = ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR);
                Mockito.when(joinPoint.proceed()).thenReturn(response);

                loggingAspect.logAround(joinPoint);

                Mockito.verify(logService).save(Mockito.argThat(model -> {
                    Assertions.assertEquals(Router.CLIENT.OP_VALID, model.getUrl());
                    Assertions.assertEquals(ObjectTool.toJson(testArg), model.getParams());
                    Assertions.assertEquals(client.getUsername(), model.getUserName());
                    Assertions.assertEquals(testIP, model.getIp());
                    Assertions.assertEquals(false, model.getStatus());
                    Assertions.assertNotNull(response.getBody());
                    Assertions.assertEquals(ObjectTool.toJson(response.getBody()), model.getResult());
                    return true;
                }));
            }
        }
    }

    @Test
    @DisplayName("日誌AOP_返回拋出例外")
    void logAround_exception_error() throws Throwable {
        try (MockedStatic<RequestContextHolder> requestContextHolderMockedStatic = Mockito.mockStatic(RequestContextHolder.class)) {
            requestContextHolderMockedStatic.when(RequestContextHolder::currentRequestAttributes).thenReturn(servletRequestAttributes);
            try (MockedStatic<ClientIdentity> clientIdentityMockedStatic = Mockito.mockStatic(ClientIdentity.class)) {
                clientIdentityMockedStatic.when(ClientIdentity::getUser).thenReturn(new ClientIdentityDto(client));
                response = ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR);
                Mockito.when(joinPoint.proceed()).thenThrow(new RuntimeException("test error"));

                Assertions.assertThrows(RuntimeException.class, () -> loggingAspect.logAround(joinPoint));

                Mockito.verify(logService).save(Mockito.argThat(model -> {
                    Assertions.assertEquals(Router.CLIENT.OP_VALID, model.getUrl());
                    Assertions.assertEquals(ObjectTool.toJson(testArg), model.getParams());
                    Assertions.assertEquals(client.getUsername(), model.getUserName());
                    Assertions.assertEquals(testIP, model.getIp());
                    Assertions.assertEquals(false, model.getStatus());
                    Assertions.assertNotNull(response.getBody());
                    Assertions.assertEquals("ERROR: test error", model.getResult());
                    return true;
                }));
            }
        }
    }

    @Test
    @DisplayName("日誌AOP_找不到用戶_拋出例外")
    void logAround_noUser_throw() throws Throwable {
        try (MockedStatic<RequestContextHolder> requestContextHolderMockedStatic = Mockito.mockStatic(RequestContextHolder.class)) {
            requestContextHolderMockedStatic.when(RequestContextHolder::currentRequestAttributes).thenReturn(servletRequestAttributes);
            try (MockedStatic<ClientIdentity> clientIdentityMockedStatic = Mockito.mockStatic(ClientIdentity.class)) {
                clientIdentityMockedStatic.when(ClientIdentity::getUser).thenReturn(null);
                response = ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR);
                Mockito.when(joinPoint.proceed()).thenReturn(response);

                Assertions.assertThrows(UsernameNotFoundException.class, () -> loggingAspect.logAround(joinPoint));
            }
        }
    }
}