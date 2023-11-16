package com.ex.erp.config.redis.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class BaseCache extends ICache{
    private static final String key = "";

    @Autowired
    protected BaseCache(RedisTemplate<String, Object> redis) {
        super(redis, key);
    }

    @Override
    protected Map<String, Object> findData() {
        return null;
    }
}
