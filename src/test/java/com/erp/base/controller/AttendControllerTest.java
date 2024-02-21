package com.erp.base.controller;

import com.erp.base.config.TestUtils;
import com.erp.base.config.redis.TestRedisConfiguration;
import com.erp.base.enums.response.ApiResponseCode;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.dto.response.ClientResponseModel;
import com.erp.base.model.entity.AttendModel;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.repository.AttendRepository;
import com.erp.base.repository.ClientRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@SpringBootTest(classes = TestRedisConfiguration.class)
@TestPropertySource(locations = {
        "classpath:application-redis-test.properties",
        "classpath:application-quartz-test.properties"
})
@AutoConfigureMockMvc
@Transactional
class AttendControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TestUtils testUtils;
    @Autowired
    private AttendRepository attendRepository;
    @Autowired
    private ClientRepository clientRepository;
    private static final String DEFAULT_USER_NAME = "test";

    @Test
    @DisplayName("簽到_找不到用戶_失敗")
    @WithMockUser(authorities = "ATTEND_SIGNIN")
    void signIn_unknownUser_error() throws Exception {
        ResponseEntity<ApiResponse> response = ApiResponse.error(ApiResponseCode.USER_NOT_FOUND);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.ATTEND.SIGN_IN)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("簽到_系統異常簽到失敗_失敗")
    @WithUserDetails("test")
    void signIn_signFailed_error() throws Exception {
        ResponseEntity<ApiResponse> response = ApiResponse.error(ApiResponseCode.SIGN_FAILED);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.ATTEND.SIGN_IN)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }
    
    @Test
    @DisplayName("簽到_成功")
    @WithUserDetails("test")
    void signIn_ok() throws Exception {
        AttendModel model = new AttendModel(new ClientModel(1L));
        attendRepository.save(model);
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.ATTEND.SIGN_IN)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        Optional<ClientModel> userOptional = clientRepository.findById(1L);
        Assertions.assertTrue(userOptional.isPresent());
        ClientResponseModel clientResponseModel = new ClientResponseModel(userOptional.get());
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(clientResponseModel.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.username").value(clientResponseModel.getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.roleId").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.email").value(clientResponseModel.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.lastLoginTime").value(clientResponseModel.getLastLoginTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.createTime").value(clientResponseModel.getCreateTime().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.createBy").value(clientResponseModel.getCreateBy()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.mustUpdatePassword").value(clientResponseModel.isMustUpdatePassword()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.attendStatus").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.department.id").value(clientResponseModel.getDepartment().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.department.name").value(clientResponseModel.getDepartment().getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.active").value(clientResponseModel.isActive()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.lock").value(clientResponseModel.isLock()));
        Optional<AttendModel> attendOptional = attendRepository.findById(model.getId());
        Assertions.assertTrue(attendOptional.isPresent());
        AttendModel attendModel = attendOptional.get();
        Assertions.assertNotNull(attendModel.getAttendTime());
        attendRepository.deleteById(model.getId());
    }

    @Test
    @DisplayName("簽退_找不到用戶_失敗")
    @WithMockUser(authorities = "ATTEND_SIGNOUT")
    void signOut_unknownUser_error() throws Exception {
        ResponseEntity<ApiResponse> response = ApiResponse.error(ApiResponseCode.USER_NOT_FOUND);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.ATTEND.SIGN_OUT)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("簽退_系統異常簽到失敗_失敗")
    @WithUserDetails("test")
    void signOut_signFailed_error() throws Exception {
        ResponseEntity<ApiResponse> response = ApiResponse.error(ApiResponseCode.SIGN_FAILED);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.ATTEND.SIGN_IN)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("簽退_成功")
    @WithUserDetails("test")
    void signOut_ok() throws Exception {
        AttendModel model = new AttendModel(new ClientModel(1L));
        attendRepository.save(model);
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.ATTEND.SIGN_OUT)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        Optional<ClientModel> userOptional = clientRepository.findById(1L);
        Assertions.assertTrue(userOptional.isPresent());
        ClientResponseModel clientResponseModel = new ClientResponseModel(userOptional.get());
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(clientResponseModel.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.username").value(clientResponseModel.getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.roleId").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.email").value(clientResponseModel.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.lastLoginTime").value(clientResponseModel.getLastLoginTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.createTime").value(clientResponseModel.getCreateTime().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.createBy").value(clientResponseModel.getCreateBy()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.mustUpdatePassword").value(clientResponseModel.isMustUpdatePassword()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.attendStatus").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.department.id").value(clientResponseModel.getDepartment().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.department.name").value(clientResponseModel.getDepartment().getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.active").value(clientResponseModel.isActive()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.lock").value(clientResponseModel.isLock()));
        Optional<AttendModel> attendOptional = attendRepository.findById(model.getId());
        Assertions.assertTrue(attendOptional.isPresent());
        AttendModel attendModel = attendOptional.get();
        Assertions.assertNotNull(attendModel.getLeaveTime());
        attendRepository.deleteById(model.getId());
    }
}