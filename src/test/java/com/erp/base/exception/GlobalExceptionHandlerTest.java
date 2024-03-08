package com.erp.base.exception;

import com.erp.base.controller.JobController;
import com.erp.base.controller.Router;
import com.erp.base.model.constant.response.ApiResponseCode;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.testConfig.TestUtils;
import com.erp.base.testConfig.redis.TestRedisConfiguration;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(classes = TestRedisConfiguration.class)
@TestPropertySource(locations = {
        "classpath:application-redis-test.properties",
        "classpath:application-quartz-test.properties"
})
@AutoConfigureMockMvc
@Transactional
@DirtiesContext
class GlobalExceptionHandlerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TestUtils testUtils;
    @MockBean
    private JobController jobController;

    @Test
    @DisplayName("異常處理_用戶鎖定")
    void exceptionHandler_LockedException() throws Exception {
        exceptionTest(LockedException.class, ApiResponse.error(ApiResponseCode.CLIENT_LOCKED));
    }

    @Test
    @DisplayName("異常處理_用戶關閉")
    void exceptionHandler_DisabledException() throws Exception {
        exceptionTest(DisabledException.class, ApiResponse.error(ApiResponseCode.CLIENT_DISABLED));
    }

    @Test
    @DisplayName("異常處理_無效輸入")
    void exceptionHandler_BadCredentialsException() throws Exception {
        exceptionTest(BadCredentialsException.class, ApiResponse.error(ApiResponseCode.INVALID_LOGIN));
    }

    @Test
    @DisplayName("異常處理_無權限")
    void exceptionHandler_AccessDeniedException() throws Exception {
        exceptionTest(AccessDeniedException.class, ApiResponse.error(ApiResponseCode.ACCESS_DENIED));
    }

    @Test
    @DisplayName("異常處理_簽名無效")
    void exceptionHandler_SignatureException() throws Exception {
        exceptionTest(SignatureException.class, ApiResponse.error(ApiResponseCode.INVALID_SIGNATURE));
    }

    @Test
    @DisplayName("異常處理_找不到用戶")
    void exceptionHandler_UsernameNotFoundException() throws Exception {
        exceptionTest(UsernameNotFoundException.class, ApiResponse.error(ApiResponseCode.USER_NOT_FOUND));
    }

    @Test
    @DisplayName("異常處理_驗證參數錯誤")
    void exceptionHandler_MethodArgumentNotValidException() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(Router.CLIENT.REGISTER)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken("test"));
        testUtils.performAndExpect(mockMvc, requestBuilder, ApiResponse.error(HttpStatus.BAD_REQUEST, "用戶名不得為空"));
    }

    @Test
    @DisplayName("異常處理_權限不完整")
    void exceptionHandler_InsufficientAuthenticationException() throws Exception {
        exceptionTest(InsufficientAuthenticationException.class, ApiResponse.error(ApiResponseCode.ACCESS_DENIED));
    }

    @Test
    @DisplayName("異常處理_無效輸入")
    void exceptionHandler_IllegalStateException() throws Exception {
        exceptionTest(IllegalStateException.class, ApiResponse.error(ApiResponseCode.INVALID_INPUT));
    }

    @Test
    @DisplayName("異常處理_未知錯誤")
    void exceptionHandler_Exception() throws Exception {
        exceptionTest(NullPointerException.class, ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR));
    }

    private void exceptionTest(Class<? extends RuntimeException> exception, ResponseEntity<ApiResponse> response) throws Exception {
        Mockito.when(jobController.add(Mockito.any())).thenThrow(exception);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(Router.JOB.ADD)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken("test"));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }
}