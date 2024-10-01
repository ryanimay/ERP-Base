package com.erp.base.service.cache;

import com.erp.base.testConfig.redis.TestRedisConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(classes = TestRedisConfiguration.class)
@TestPropertySource(locations = {
        "classpath:application-redis-test.properties",
        "classpath:application-quartz-test.properties"
})
@AutoConfigureMockMvc
@Transactional
@DirtiesContext
class TokenBlackListTest {
    @Autowired
    private TokenBlackList tokenBlackList;

    @Test
    void add_and_exists_ok() {
        tokenBlackList.add("test");
        Assertions.assertTrue(tokenBlackList.exists("test"));
    }
}