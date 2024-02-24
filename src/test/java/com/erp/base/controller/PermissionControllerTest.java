package com.erp.base.controller;

import com.erp.base.config.TestUtils;
import com.erp.base.config.redis.TestRedisConfiguration;
import com.erp.base.enums.response.ApiResponseCode;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.entity.DepartmentModel;
import com.erp.base.model.entity.PermissionModel;
import com.erp.base.model.entity.RoleModel;
import com.erp.base.repository.PermissionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
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

import java.util.*;

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
    private static final String DEFAULT_USER_NAME = "test";
    private static ClientModel me;

    @BeforeAll
    static void beforeAll(){
        me = new ClientModel(1L);
        me.setUsername(DEFAULT_USER_NAME);
        me.setRoles(Set.of(new RoleModel(2L)));
        me.setDepartment(new DepartmentModel(1L));
    }

    @Test
    @DisplayName("權限清單_成功")
    @WithUserDetails(DEFAULT_USER_NAME)
    void performancePendingList_managerSearch_ok() throws Exception {
        List<PermissionModel> all = permissionRepository.findAll();
        Map<String, List<PermissionModel>> map = new HashMap<>();
        for (PermissionModel permission : all) {
            String key = permission.getAuthority().split("_")[0];
            List<PermissionModel> list = map.computeIfAbsent(key, k -> new ArrayList<>());
            list.add(permission);
        }
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.PERMISSION.LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data['*'][0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data['*'][0].authority").value("*"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data['*'][0].info").value("用戶:測試接口"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data['*'][0].url").value("/client/opValid"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data['*'][0].status").value("true"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.PERMISSION", Matchers.hasSize(4)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.PROJECT", Matchers.hasSize(5)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.LOG", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.PERFORMANCE", Matchers.hasSize(7)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.ROUTER", Matchers.hasSize(3)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.DEPARTMENT", Matchers.hasSize(4)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.PROCUREMENT", Matchers.hasSize(4)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data['*']", Matchers.hasSize(5)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.ATTEND", Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.CACHE", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.CLIENT", Matchers.hasSize(7)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.ROLE", Matchers.hasSize(5)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.SALARY", Matchers.hasSize(4)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.LEAVE", Matchers.hasSize(7)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.JOB", Matchers.hasSize(4)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.QUARTZ", Matchers.hasSize(6)));
    }

}