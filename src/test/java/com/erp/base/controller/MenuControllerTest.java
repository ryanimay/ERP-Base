package com.erp.base.controller;


import com.erp.base.model.constant.response.ApiResponseCode;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.testConfig.TestUtils;
import com.erp.base.testConfig.redis.TestRedisConfiguration;
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

@SpringBootTest(classes = TestRedisConfiguration.class)
@TestPropertySource(locations = {
        "classpath:application-redis-test.properties",
        "classpath:application-quartz-test.properties"
})
@AutoConfigureMockMvc
@Transactional
@DirtiesContext
class MenuControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TestUtils testUtils;
    private static final long DEFAULT_UID = 1L;

    @Test
    @DisplayName("完整菜單_成功")
    void findAll_ok() throws Exception {
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.MENU.ALL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));

        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].name").value("用戶管理"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].path").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].icon").value("User"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].parentsId").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].child[0].id").value(10))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].child[0].name").value("用戶清單"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].child[0].path").value("client"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].child[0].icon").value("List"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].child[0].parentsId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].child[0].child").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].name").value("權限管理"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].child").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[2].name").value("薪資管理"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[2].child").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[3].name").value("績效管理"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[3].child").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[4].name").value("休假管理"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[4].child").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[5].name").value("專案管理"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[5].child").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[6].name").value("採購管理"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[6].child").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[7].name").value("任務管理"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[7].child").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[8].name").value("排程管理"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[8].child").isNotEmpty());
    }

    @Test
    @DisplayName("用戶菜單_成功")
    void pMenu_ok() throws Exception {
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.MENU.P_MENU)
                .contentType(MediaType.APPLICATION_JSON)
                .param("roleIds", "1")
                .param("roleIds", "2")
                .param("roleIds", "3")
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));

        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].name").value("用戶管理"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].path").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].icon").value("User"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].parentsId").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].child[0].id").value(10))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].child[0].name").value("用戶清單"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].child[0].path").value("client"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].child[0].icon").value("List"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].child[0].parentsId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].child[0].child").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].name").value("權限管理"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].child").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[2].name").value("薪資管理"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[2].child").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[3].name").value("績效管理"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[3].child").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[4].name").value("休假管理"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[4].child").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[5].name").value("專案管理"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[5].child").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[6].name").value("採購管理"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[6].child").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[7].name").value("任務管理"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[7].child").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[8].name").value("排程管理"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[8].child").isNotEmpty());
    }
}