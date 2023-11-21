package com.ex.erp.controller;

import com.ex.erp.service.cache.ClientCache;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cache")
public class CacheController {
    private ClientCache clientCache;
    @Autowired
    public void setClientCache(ClientCache clientCache){
        this.clientCache = clientCache;
    }

    @GetMapping("/refresh")
    public void refresh(String cacheKey){
        if(StringUtils.isNotEmpty(cacheKey)){

            System.out.println("refresh cacheName:" + cacheKey);
        }else {
            clientCache.refreshClientAll();
            System.out.println("refresh all cache");
        }
    }
}
