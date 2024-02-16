package com.erp.base.controller;

import com.erp.base.config.redis.TestRedisConfiguration;
import com.erp.base.enums.response.ApiResponseCode;
import com.erp.base.model.dto.response.ApiResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import redis.embedded.RedisServer;

@SpringBootTest(classes = TestRedisConfiguration.class)
@TestPropertySource(locations = {
        "classpath:application-redis-test.properties",
        "classpath:application-quartz-test.properties"
})
@AutoConfigureMockMvc
class ClientControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RedisServer redisServer;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Test
    void testRedisConnection() {
        Assertions.assertEquals(redisPort, redisServer.ports().get(0));
    }

    @Test
    void simpleTestApi_OK() throws Exception {
        ApiResponse responseBody = ApiResponse.success(ApiResponseCode.SUCCESS).getBody();
        assert responseBody != null;
        mockMvc.perform(MockMvcRequestBuilders.get(Router.CLIENT.OP_VALID))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(responseBody.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(responseBody.getMessage()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(responseBody.getData()));
    }
}