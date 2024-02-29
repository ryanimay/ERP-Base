package com.erp.base.controller;

import com.erp.base.config.TestUtils;
import com.erp.base.config.redis.TestRedisConfiguration;
import com.erp.base.enums.response.ApiResponseCode;
import com.erp.base.model.dto.request.role.RolePermissionRequest;
import com.erp.base.model.dto.request.role.RoleRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.entity.RoleModel;
import com.erp.base.repository.RoleRepository;
import com.erp.base.tool.ObjectTool;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
class RoleControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TestUtils testUtils;
    @Autowired
    private RoleRepository roleRepository;
    private static final String DEFAULT_USER_NAME = "test";

    @Test
    @DisplayName("角色清單_成功")
    void roleList_ok() throws Exception {
        List<RoleModel> all = roleRepository.findAll();
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.ROLE.LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].id").value(all.get(0).getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].roleName").value(all.get(0).getRoleName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].id").value(all.get(1).getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].roleName").value(all.get(1).getRoleName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[2].id").value(all.get(2).getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[2].roleName").value(all.get(2).getRoleName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[3].id").value(all.get(3).getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[3].roleName").value(all.get(3).getRoleName()));
    }

    @Test
    @DisplayName("編輯角色_成功")
    void updateRole_ok() throws Exception {
        RoleRequest roleRequest = new RoleRequest();
        roleRequest.setId(3L);
        roleRequest.setName("測試角色更新");
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.ROLE.UPDATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(roleRequest))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
        Optional<RoleModel> byId = roleRepository.findById(3L);
        Assertions.assertTrue(byId.isPresent());
        RoleModel model = byId.get();
        Assertions.assertEquals(roleRequest.getName(), model.getRoleName());
    }

    @Test
    @DisplayName("編輯角色_角色名重複_錯誤")
    void updateRole_existsName_error() throws Exception {
        RoleRequest roleRequest = new RoleRequest();
        roleRequest.setId(3L);
        roleRequest.setName("Basic");
        ResponseEntity<ApiResponse> response = ApiResponse.error(ApiResponseCode.NAME_ALREADY_EXIST);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.ROLE.UPDATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(roleRequest))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("編輯角色_未知ID_錯誤")
    void updateRole_unknownId_error() throws Exception {
        RoleRequest roleRequest = new RoleRequest();
        roleRequest.setId(99L);
        roleRequest.setName("測試角色更新");
        ResponseEntity<ApiResponse> response = ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.ROLE.UPDATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(roleRequest))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("新增角色_成功")
    void addRole_ok() throws Exception {
        RoleRequest roleRequest = new RoleRequest();
        roleRequest.setName("測試角色新增");
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(Router.ROLE.ADD)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(roleRequest))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
        List<RoleModel> all = roleRepository.findAll();
        Optional<RoleModel> first = all.stream().filter(r -> r.getRoleName().equals(roleRequest.getName())).findFirst();
        Assertions.assertTrue(first.isPresent());
        RoleModel model = first.get();
        Assertions.assertEquals(roleRequest.getName(), model.getRoleName());
    }

    @Test
    @DisplayName("新增角色_角色名重複_錯誤")
    void addRole_existsName_error() throws Exception {
        RoleRequest roleRequest = new RoleRequest();
        roleRequest.setName("Basic");
        ResponseEntity<ApiResponse> response = ApiResponse.error(ApiResponseCode.NAME_ALREADY_EXIST);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(Router.ROLE.ADD)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(roleRequest))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("編輯角色權限_成功")
    void rolePermission_ok() throws Exception {
        Optional<RoleModel> byId = roleRepository.findById(1L);
        Assertions.assertTrue(byId.isPresent());
        Assertions.assertTrue(byId.get().getPermissions().isEmpty());
        RolePermissionRequest roleRequest = new RolePermissionRequest();
        roleRequest.setId(1L);
        List<Long> list = new ArrayList<>();
        list.add(1L);
        list.add(2L);
        list.add(3L);
        roleRequest.setPermissionIds(list);
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(Router.ROLE.ROLE_PERMISSION)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(roleRequest))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
        byId = roleRepository.findById(1L);
        Assertions.assertTrue(byId.isPresent());
        RoleModel model = byId.get();
        Assertions.assertEquals(3, model.getPermissions().size());
    }

    @Test
    @DisplayName("編輯角色權限_未知ID_錯誤")
    void rolePermission_unknownId_error() throws Exception {
        RolePermissionRequest roleRequest = new RolePermissionRequest();
        roleRequest.setId(99L);
        List<Long> list = new ArrayList<>();
        list.add(1L);
        list.add(2L);
        list.add(3L);
        roleRequest.setPermissionIds(list);
        ResponseEntity<ApiResponse> response = ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(Router.ROLE.ROLE_PERMISSION)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(roleRequest))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("移除角色_使用中不可移除_錯誤")
    void removeRole_roleOnUsed_error() throws Exception {
        ResponseEntity<ApiResponse> response = ApiResponse.error(ApiResponseCode.ROLE_IN_USE);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete(Router.ROLE.REMOVE)
                .contentType(MediaType.APPLICATION_JSON)
                .param("id", "2")
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("移除角色_成功")
    void removeRole_ok() throws Exception {
        Optional<RoleModel> byId = roleRepository.findById(3L);
        Assertions.assertTrue(byId.isPresent());
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete(Router.ROLE.REMOVE)
                .contentType(MediaType.APPLICATION_JSON)
                .param("id", "3")
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
        byId = roleRepository.findById(3L);
        Assertions.assertTrue(byId.isEmpty());
    }
}