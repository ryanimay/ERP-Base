package com.erp.base.controller;

import com.erp.base.model.constant.response.ApiResponseCode;
import com.erp.base.model.dto.request.permission.BanRequest;
import com.erp.base.model.dto.request.permission.SecurityConfirmRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.entity.PermissionModel;
import com.erp.base.repository.PermissionRepository;
import com.erp.base.testConfig.TestUtils;
import com.erp.base.testConfig.redis.TestRedisConfiguration;
import com.erp.base.tool.ObjectTool;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@SpringBootTest(classes = TestRedisConfiguration.class)
@TestPropertySource(locations = {
        "classpath:application-redis-test.properties",
        "classpath:application-quartz-test.properties"
})
@AutoConfigureMockMvc
@Transactional
@DirtiesContext
class PermissionControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TestUtils testUtils;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private PermissionRepository permissionRepository;
    @Value("${security.password}")
    private String securityPwd;
    private static final long DEFAULT_UID = 1L;
    private final List<Integer> permissionArray = List.of(35,18,5,9,31,59,49,37,40,44,52,63,3,56,39,34,29,11,30,42,28,58,1,68,36,12,4,7,21,19,27,6,64,10,13,41,17,62,57,48,32,51,2,67,47,50,43,14,46,20,66,24,60,55,15,23,45,54,22,53,38,65,16,25,26,61,33,8,69,70,71,72);

    @Test
    @DisplayName("權限清單_成功")
    void permissionList_ok() throws Exception {
        Optional<PermissionModel> byId = permissionRepository.findById(1L);
        Assertions.assertTrue(byId.isPresent());
        PermissionModel model = byId.get();
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.PERMISSION.LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].children[0].id").value(model.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].children[0].authority").value(model.getAuthority()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].children[0].info").value(model.getInfo()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].children[0].url").value(model.getUrl()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].children[0].status").value(model.getStatus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].children", Matchers.hasSize(5)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].children", Matchers.hasSize(5)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[2].children", Matchers.hasSize(5)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[3].children", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[4].children", Matchers.hasSize(7)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[5].children", Matchers.hasSize(4)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[6].children", Matchers.hasSize(4)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[7].children", Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[8].children", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[9].children", Matchers.hasSize(7)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[10].children", Matchers.hasSize(7)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[11].children", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[12].children", Matchers.hasSize(4)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[13].children", Matchers.hasSize(7)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[14].children", Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[15].children", Matchers.hasSize(4)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[16].children", Matchers.hasSize(6)));
    }

    @Test
    @DisplayName("角色權限_無權限角色_成功")
    void rolePermission_noPermissionRole_ok() throws Exception {
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.PERMISSION.ROLE)
                .contentType(MediaType.APPLICATION_JSON)
                .param("roleId", "1")
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data", Matchers.hasSize(0)));
    }

    @Test
    @DisplayName("角色權限_成功")
    void rolePermission_ok() throws Exception {
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.PERMISSION.ROLE)
                .contentType(MediaType.APPLICATION_JSON)
                .param("roleId", "2")
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data", Matchers.hasSize(72)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data", Matchers.containsInAnyOrder(permissionArray.toArray())));
    }

    @Test
    @DisplayName("角色權限_未知角色_成功")
    void rolePermission_unknownRole_ok() throws Exception {
        ResponseEntity<ApiResponse> response = ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.PERMISSION.ROLE)
                .contentType(MediaType.APPLICATION_JSON)
                .param("roleId", "99")
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("權限停用/啟用_成功")
    void banPermission_ok() throws Exception {
        BanRequest banRequest = new BanRequest();
        banRequest.setId(1);
        banRequest.setStatus(false);
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.PERMISSION.BAN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(banRequest))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
        entityManager.flush();
        entityManager.clear();
        Optional<PermissionModel> byId = permissionRepository.findById(1L);
        Assertions.assertTrue(byId.isPresent());
        PermissionModel model = byId.get();
        Assertions.assertFalse(model.getStatus());
        requestBuilder = MockMvcRequestBuilders.get(Router.CLIENT.OP_VALID);
        response = ApiResponse.error(ApiResponseCode.ACCESS_DENIED);
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
        banRequest.setStatus(true);
        requestBuilder = MockMvcRequestBuilders.put(Router.PERMISSION.BAN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(banRequest))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        response = ApiResponse.success(ApiResponseCode.SUCCESS);
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
        entityManager.flush();
        entityManager.clear();
        byId = permissionRepository.findById(1L);
        Assertions.assertTrue(byId.isPresent());
        model = byId.get();
        Assertions.assertTrue(model.getStatus());
        requestBuilder = MockMvcRequestBuilders.get(Router.CLIENT.OP_VALID);
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("安全認證_成功")
    void securityConfirm_ok() throws Exception {
        SecurityConfirmRequest request = new SecurityConfirmRequest();
        request.setSecurityPassword(securityPwd);
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS, true);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(Router.PERMISSION.SECURITY_CONFIRM)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(request))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("安全認證_失敗")
    void securityConfirm_wrongPwd_error() throws Exception {
        SecurityConfirmRequest request = new SecurityConfirmRequest();
        request.setSecurityPassword("zzzzzzzzzzzzzzz");
        ResponseEntity<ApiResponse> response = ApiResponse.error(ApiResponseCode.SECURITY_ERROR, false);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(Router.PERMISSION.SECURITY_CONFIRM)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(request))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("獲取公鑰_成功")
    void getPublicKey_ok() throws Exception {
        SecurityConfirmRequest request = new SecurityConfirmRequest();
        request.setSecurityPassword(securityPwd);
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS, true);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(Router.PERMISSION.GET_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(request))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isNotEmpty());
    }

    @Test
    @DisplayName("獲取公鑰_安全碼錯誤_失敗")
    void getPublicKey_wrongPwd_error() throws Exception {
        SecurityConfirmRequest request = new SecurityConfirmRequest();
        request.setSecurityPassword("zzzzzzzzzzzzzzz");
        ResponseEntity<ApiResponse> response = ApiResponse.error(ApiResponseCode.SECURITY_ERROR, false);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(Router.PERMISSION.GET_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(request))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }
}