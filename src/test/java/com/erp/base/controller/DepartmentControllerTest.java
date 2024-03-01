package com.erp.base.controller;

import com.erp.base.testConfig.TestUtils;
import com.erp.base.testConfig.redis.TestRedisConfiguration;
import com.erp.base.enums.response.ApiResponseCode;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.entity.DepartmentModel;
import com.erp.base.model.entity.RoleModel;
import com.erp.base.repository.DepartmentRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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

import java.util.Optional;

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
    @Autowired
    private DepartmentRepository departmentRepository;
    @PersistenceContext
    private EntityManager entityManager;
    private static final String DEFAULT_USER_NAME = "test";

    @Test
    @DisplayName("部門清單_搜ID_成功")
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

    @Test
    @DisplayName("編輯/新增部門_編輯部門_成功")
    void editDepartment_updateDepartment_ok() throws Exception {
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(Router.DEPARTMENT.EDIT)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "id": 3,
                        "name": "departmentTest",
                        "defaultRoleId": 1
                        }
                        """)
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
        Optional<DepartmentModel> departmentOptional = departmentRepository.findById(3L);
        Assertions.assertTrue(departmentOptional.isPresent());
        DepartmentModel model = departmentOptional.get();
        Assertions.assertEquals(3L, model.getId());
        Assertions.assertEquals("departmentTest", model.getName());
        Assertions.assertEquals(1L, model.getDefaultRole().getId());
        Assertions.assertEquals("visitor", model.getDefaultRole().getRoleName());
    }

    @Test
    @DisplayName("編輯/新增部門_新增部門_成功")
    void editDepartment_addDepartment_ok() throws Exception {
        Optional<DepartmentModel> departmentOptional = departmentRepository.findById(4L);
        Assertions.assertTrue(departmentOptional.isEmpty());
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(Router.DEPARTMENT.EDIT)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "name": "newDepartmentTest",
                        "defaultRoleId": 1
                        }
                        """)
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
        entityManager.clear();
        departmentOptional = departmentRepository.findById(4L);
        Assertions.assertTrue(departmentOptional.isPresent());
        DepartmentModel model = departmentOptional.get();
        Assertions.assertEquals("newDepartmentTest", model.getName());
        Assertions.assertEquals(1L, model.getDefaultRole().getId());
        Assertions.assertEquals("visitor", model.getDefaultRole().getRoleName());
    }

    @Test
    @DisplayName("移除部門_部門使用中無法刪除_錯誤")
    void removeDepartment_departmentInUsed_error() throws Exception {
        ResponseEntity<ApiResponse> response = ApiResponse.error(ApiResponseCode.DEPARTMENT_IN_USE);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete(Router.DEPARTMENT.REMOVE)
                .contentType(MediaType.APPLICATION_JSON)
                .param("id", "1")
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("移除部門_成功")
    void removeDepartment_ok() throws Exception {
        DepartmentModel entity = new DepartmentModel();
        entity.setName("testDelete");
        entity.setDefaultRole(new RoleModel(1));
        entity = departmentRepository.save(entity);
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete(Router.DEPARTMENT.REMOVE)
                .contentType(MediaType.APPLICATION_JSON)
                .param("id", String.valueOf(entity.getId()))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
        Optional<DepartmentModel> byId = departmentRepository.findById(entity.getId());
        Assertions.assertTrue(byId.isEmpty());
    }
}