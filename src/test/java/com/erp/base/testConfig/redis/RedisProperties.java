package com.erp.base.testConfig.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisProperties {
    private final int redisPort;
    private final String redisHost;

    public RedisProperties(
            @Value("${spring.data.redis.port}") int redisPort,
            @Value("${spring.data.redis.host}") String redisHost) {
        this.redisPort = redisPort;
        this.redisHost = redisHost;
    }

    public int getRedisPort() {
        return redisPort;
    }

    public String getRedisHost() {
        return redisHost;
    }
// getters
}