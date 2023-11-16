package com.ex.erp.service.cache;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;
import java.util.Set;

public abstract class ICache {

    private final String REDIS_KEY;
    protected RedisTemplate<String, Object> redis;

    //帶入Key和redisTemplate
    protected ICache(RedisTemplate<String, Object> redis, String key){
        this.redis = redis;
        this.REDIS_KEY = key;
    }

    /**
     * 拿快取資料
     * */
    public Map<String, Object> loadData(){
        Map<String, Object> roleMap = getRedisData();
        if(roleMap != null){
            return roleMap;
        }
        return findData();
    }
    /**
     * 刷新快取
     * */
    public void refresh(){
        redis.delete(REDIS_KEY);
    }

    public void refreshAll(){
        Set<String> keys = redis.keys("*");
        if (keys != null) redis.delete(keys);
    }

    @SuppressWarnings("unchecked")
    protected Map<String, Object> getRedisData() {
        return (Map<String, Object>) redis.opsForValue().get(REDIS_KEY);
    }

    protected abstract Map<String, Object> findData();
}
