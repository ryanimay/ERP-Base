package com.erp.base.controller;

import com.erp.base.model.constant.cache.CacheConstant;
import com.erp.base.model.constant.response.ApiResponseCode;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.dto.security.ClientIdentityDto;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.entity.DepartmentModel;
import com.erp.base.model.entity.RoleModel;
import com.erp.base.repository.ClientRepository;
import com.erp.base.repository.DepartmentRepository;
import com.erp.base.service.CacheService;
import com.erp.base.testConfig.TestUtils;
import com.erp.base.testConfig.redis.TestRedisConfiguration;
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
    private static final long DEFAULT_UID = 1L;

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

        ClientIdentityDto client = cacheService.getClient(DEFAULT_UID);
        Assertions.assertNotNull(client);
        client.setEmail("testEdit@gmail.com");
        ClientModel entity = client.toEntity();
        entity = clientRepository.save(entity);
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.REFRESH_CACHE_SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.CACHE.REFRESH)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
        department = cacheService.getDepartment(5L);
        Assertions.assertEquals(department.getId(), newDepartment.getId());
        Assertions.assertEquals(department.getName(), newDepartment.getName());
        Assertions.assertEquals(department.getClientModelList(), newDepartment.getClientModelList());
        Assertions.assertEquals(department.getDefaultRole().getRoleName(), newDepartment.getDefaultRole().getRoleName());
        Assertions.assertEquals(department.getDefaultRole().getId(), newDepartment.getDefaultRole().getId());

        ClientIdentityDto newClient = cacheService.getClient(DEFAULT_UID);
        Assertions.assertEquals(newClient.getEmail(), entity.getEmail());
        departmentRepository.deleteById(5L);
    }

    @Test
    @DisplayName("刷緩存_刷新用戶緩存_成功")
    void refreshCache_refreshClient_ok() throws Exception {
        cacheService.refreshCache(CacheConstant.CLIENT.NAME_CLIENT);
        ClientIdentityDto client = cacheService.getClient(DEFAULT_UID);
        Assertions.assertNotNull(client);
        client.setEmail("testEdit2@gmail.com");
        ClientModel entity = client.toEntity();
        entity = clientRepository.save(entity);
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.REFRESH_CACHE_SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.CACHE.REFRESH)
                .contentType(MediaType.APPLICATION_JSON)
                .param("cacheKey", CacheConstant.CLIENT.NAME_CLIENT)
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
        ClientIdentityDto newClient = cacheService.getClient(DEFAULT_UID);
        Assertions.assertEquals(newClient.getEmail(), entity.getEmail());
    }

    @Test
    @DisplayName("刷緩存_刷新權限緩存_成功")
    void refreshCache_refreshRolePermission_ok() throws Exception {
        cacheService.refreshCache(CacheConstant.ROLE_PERMISSION.NAME_ROLE_PERMISSION);
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
                .param("cacheKey", CacheConstant.ROLE_PERMISSION.NAME_ROLE_PERMISSION)
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
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
    @DisplayName("刷緩存_刷新單一key緩存_成功")
    void refreshCache_refreshCacheKey_ok() throws Exception {
        cacheService.refreshCache(CacheConstant.CLIENT.NAME_CLIENT);
        ClientIdentityDto client = cacheService.getClient(DEFAULT_UID);
        Assertions.assertNotNull(client);
        client.setEmail("testEdit2@gmail.com");
        ClientModel entity = client.toEntity();
        entity = clientRepository.save(entity);
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.REFRESH_CACHE_SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.CACHE.REFRESH)
                .contentType(MediaType.APPLICATION_JSON)
                .param("cacheKey", CacheConstant.CLIENT.NAME_CLIENT + ":" + CacheConstant.CLIENT.CLIENT + DEFAULT_UID)
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
        ClientIdentityDto newClient = cacheService.getClient(DEFAULT_UID);
        Assertions.assertEquals(newClient.getEmail(), entity.getEmail());
    }

//    @Test
//    @DisplayName("刷緩存_key不存在_錯誤")
//    void refreshCache_unknownCacheKey_error() throws Exception {
//        ResponseEntity<ApiResponse> response = ApiResponse.errorMsgFormat(ApiResponseCode.CACHE_KEY_ERROR, "No cache found for key: testKey");
//        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.CACHE.REFRESH)
//                .contentType(MediaType.APPLICATION_JSON)
//                .param("cacheKey", "testKey")
//                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
//        testUtils.performAndExpect(mockMvc, requestBuilder, response);
//    }
}