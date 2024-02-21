package com.erp.base.controller;

import com.erp.base.config.TestUtils;
import com.erp.base.config.redis.TestRedisConfiguration;
import com.erp.base.enums.response.ApiResponseCode;
import com.erp.base.model.dto.response.ApiResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(classes = TestRedisConfiguration.class)
@TestPropertySource(locations = {
        "classpath:application-redis-test.properties",
        "classpath:application-quartz-test.properties"
})
@AutoConfigureMockMvc
@Transactional
@DirtiesContext
class DepartmentControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TestUtils testUtils;
    private static final String DEFAULT_USER_NAME = "test";

    @Test
    @DisplayName("部門清單_搜ID_成功")
    @WithUserDetails(DEFAULT_USER_NAME)
    void departmentList_findById_ok() throws Exception {
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.DEPARTMENT.LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .param("id", "1")
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        testUtils.comparePage(resultActions, 15, 1, 1, 1);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].name").value("departmentA"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].role.id").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].role.roleName").value("Basic"));
    }

    @Test
    @DisplayName("部門清單_模糊查詢名稱_成功")
    @WithUserDetails(DEFAULT_USER_NAME)
    void departmentList_findByNameLike_ok() throws Exception {
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.DEPARTMENT.LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .param("name", "depart")
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        testUtils.comparePage(resultActions, 15, 1, 3, 1);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].name").value("departmentA"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].role.id").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].role.roleName").value("Basic"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].id").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].name").value("departmentB"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].role.id").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].role.roleName").value("Basic"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[2].id").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[2].name").value("departmentC"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[2].role.id").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[2].role.roleName").value("Basic"));
    }

    @Test
    @DisplayName("部門清單_全搜_成功")
    @WithUserDetails(DEFAULT_USER_NAME)
    void departmentList_findAll_ok() throws Exception {
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.DEPARTMENT.LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        testUtils.comparePage(resultActions, 15, 1, 3, 1);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].name").value("departmentA"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].role.id").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].role.roleName").value("Basic"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].id").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].name").value("departmentB"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].role.id").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].role.roleName").value("Basic"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[2].id").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[2].name").value("departmentC"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[2].role.id").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[2].role.roleName").value("Basic"));
    }

    @Test
    @DisplayName("部門清單_全搜_分頁1筆_成功")
    @WithUserDetails(DEFAULT_USER_NAME)
    void departmentList_findAll_PageSize1_ok() throws Exception {
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.DEPARTMENT.LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .param("pageSize", "1")
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        testUtils.comparePage(resultActions, 1, 3, 3, 1);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].name").value("departmentA"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].role.id").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].role.roleName").value("Basic"));
    }

    @Test
    @DisplayName("部門清單_全搜_分頁1筆_第二頁_成功")
    @WithUserDetails(DEFAULT_USER_NAME)
    void departmentList_findAll_PageSize1_Page2_ok() throws Exception {
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.DEPARTMENT.LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .param("pageSize", "1")
                .param("pageNum", "2")
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        testUtils.comparePage(resultActions, 1, 3, 3, 2);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].id").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].name").value("departmentB"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].role.id").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].role.roleName").value("Basic"));
    }

    @Test
    @DisplayName("部門清單_查無資料_成功")
    @WithUserDetails(DEFAULT_USER_NAME)
    void departmentList_find0Result_ok() throws Exception {
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.DEPARTMENT.LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .param("id", "99")
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        testUtils.comparePage(resultActions, 15, 0, 0, 1);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data").isEmpty());
    }

    @Test
    @DisplayName("部門員工清單_未知ID_錯誤")
    @WithUserDetails(DEFAULT_USER_NAME)
    void departmentStaff_unknownId_error() throws Exception {
        ResponseEntity<ApiResponse> response = ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "DepartmentId Not Found.");
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.DEPARTMENT.STAFF)
                .contentType(MediaType.APPLICATION_JSON)
                .param("id", "99")
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("部門員工清單_成功")
    @WithUserDetails(DEFAULT_USER_NAME)
    void departmentStaff_ok() throws Exception {
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.DEPARTMENT.STAFF)
                .contentType(MediaType.APPLICATION_JSON)
                .param("id", "1")
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].username").value("test"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].level").value(1));
    }
}