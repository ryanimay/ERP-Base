package com.erp.base.controller;

import com.erp.base.config.TestUtils;
import com.erp.base.config.redis.TestRedisConfiguration;
import com.erp.base.enums.response.ApiResponseCode;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.entity.RoleModel;
import com.erp.base.repository.RoleRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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

import java.util.List;

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
    @PersistenceContext
    private EntityManager entityManager;
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
}