package com.erp.base.service.cache;

import com.erp.base.model.constant.cache.CacheConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = CacheConstant.TOKEN_BLACK_LIST.TOKEN_BLACK_LIST)
public class TokenBlackList {

    private Cache cache;
    @Autowired
    public void setCacheManager(CacheManager cacheManager){
        this.cache = cacheManager.getCache(CacheConstant.TOKEN_BLACK_LIST.TOKEN_BLACK_LIST);
    }

    @Cacheable(key = "#token")
    public String add(String token) {
        return token;
    }

    public boolean exists(String token){
        return cache.get(token) != null;
    }
}
