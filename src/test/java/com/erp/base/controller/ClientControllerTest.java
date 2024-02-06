package com.erp.base.controller;

import com.erp.base.config.redis.TestRedisConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import redis.embedded.RedisServer;

@SpringBootTest(classes = TestRedisConfiguration.class)
@TestPropertySource(locations = {
        "classpath:application-redis-test.properties",
        "classpath:application-quartz-test.properties"
})
class ClientControllerTest {

    @Autowired
    private RedisServer redisServer;

    @Test
    void testConnection() {
        System.out.println("port:::::" + redisServer.ports().get(0));
    }
}