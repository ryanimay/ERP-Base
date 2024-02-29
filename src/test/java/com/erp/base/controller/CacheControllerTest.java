package com.erp.base.controller;

import com.erp.base.config.TestUtils;
import com.erp.base.config.redis.TestRedisConfiguration;
import com.erp.base.enums.response.ApiResponseCode;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.entity.DepartmentModel;
import com.erp.base.model.entity.RoleModel;
import com.erp.base.repository.ClientRepository;
import com.erp.base.repository.DepartmentRepository;
import com.erp.base.service.CacheService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
class CacheControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TestUtils testUtils;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private DepartmentRepository departmentRepository;
    private static final String DEFAULT_USER_NAME = "test";

    @Test
    @DisplayName("刷緩存_全刷_成功")
    void refreshCache_refreshAll_ok() throws Exception {
        cacheService.refreshAllCache();
        DepartmentModel department = cacheService.getDepartment(5L);
        Assertions.assertNull(department);
        DepartmentModel newDepartment = new DepartmentModel();
        newDepartment.setId(5L);
        newDepartment.setName("testDepartment");
        newDepartment.setDefaultRole(new RoleModel(1));
        newDepartment = departmentRepository.save(newDepartment);

        ClientModel client = cacheService.getClient(DEFAULT_USER_NAME);
        Assertions.assertNotNull(client);
        client.setEmail("testEdit@gmail.com");
        client = clientRepository.save(client);
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.REFRESH_CACHE_SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.CACHE.REFRESH)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
        department = cacheService.getDepartment(5L);
        Assertions.assertEquals(department.getId(), newDepartment.getId());
        Assertions.assertEquals(department.getName(), newDepartment.getName());
        Assertions.assertEquals(department.getClientModelList(), newDepartment.getClientModelList());
        Assertions.assertEquals(department.getDefaultRole().getRoleName(), newDepartment.getDefaultRole().getRoleName());
        Assertions.assertEquals(department.getDefaultRole().getId(), newDepartment.getDefaultRole().getId());

        ClientModel newClient = cacheService.getClient(DEFAULT_USER_NAME);
        Assertions.assertEquals(newClient.getEmail(), client.getEmail());
        departmentRepository.deleteById(5L);
    }

    @Test
    @DisplayName("刷緩存_刷新用戶緩存_成功")
    void refreshCache_refreshClient_ok() throws Exception {
        cacheService.refreshClient();
        ClientModel client = cacheService.getClient(DEFAULT_USER_NAME);
        Assertions.assertNotNull(client);
        client.setEmail("testEdit2@gmail.com");
        client = clientRepository.save(client);
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.REFRESH_CACHE_SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.CACHE.REFRESH)
                .contentType(MediaType.APPLICATION_JSON)
                .param("cacheKey", "CLIENT")
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
        ClientModel newClient = cacheService.getClient(DEFAULT_USER_NAME);
        Assertions.assertEquals(newClient.getEmail(), client.getEmail());
    }

    @Test
    @DisplayName("刷緩存_刷新權限緩存_成功")
    void refreshCache_refreshRolePermission_ok() throws Exception {
        cacheService.refreshRolePermission();
        DepartmentModel department = cacheService.getDepartment(4L);
        Assertions.assertNull(department);
        DepartmentModel newDepartment = new DepartmentModel();
        newDepartment.setId(4L);
        newDepartment.setName("testDepartment");
        newDepartment.setDefaultRole(new RoleModel(1));
        newDepartment = departmentRepository.save(newDepartment);
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.REFRESH_CACHE_SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.CACHE.REFRESH)
                .contentType(MediaType.APPLICATION_JSON)
                .param("cacheKey", "ROLE_PERMISSION")
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
        department = cacheService.getDepartment(4L);
        Assertions.assertEquals(department.getId(), newDepartment.getId());
        Assertions.assertEquals(department.getName(), newDepartment.getName());
        Assertions.assertEquals(department.getClientModelList(), newDepartment.getClientModelList());
        Assertions.assertEquals(department.getDefaultRole().getRoleName(), newDepartment.getDefaultRole().getRoleName());
        Assertions.assertEquals(department.getDefaultRole().getId(), newDepartment.getDefaultRole().getId());
        departmentRepository.deleteById(4L);
    }

    @Test
    @DisplayName("刷緩存_key不存在_錯誤")
    void refreshCache_unknownCacheKey_error() throws Exception {
        ResponseEntity<ApiResponse> response = ApiResponse.errorMsgFormat(ApiResponseCode.CACHE_KEY_ERROR, "No cache found for key: testKey");
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.CACHE.REFRESH)
                .contentType(MediaType.APPLICATION_JSON)
                .param("cacheKey", "testKey")
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }
}