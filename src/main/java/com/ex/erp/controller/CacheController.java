package com.ex.erp.controller;

import com.ex.erp.config.redis.cache.ICache;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cache")
public class CacheController {

    private ApplicationContext applicationContext;

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @GetMapping("/refresh")
    public void refresh(String cacheName){
        if(StringUtils.isNotEmpty(cacheName)){
            ICache cache = (ICache) applicationContext.getBean(cacheName);
            cache.refresh();
            System.out.println("refresh cacheName:" + cacheName);
        }else {
            ICache cache = (ICache) applicationContext.getBean("baseCache");
            cache.refreshAll();
            System.out.println("refresh all cache");
        }
    }
}
