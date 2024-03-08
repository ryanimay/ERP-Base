package com.erp.base.config.init;

import com.erp.base.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class InitializationRunner implements CommandLineRunner {
    private final CacheService cacheService;

    @Autowired
    public InitializationRunner(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @Override
    public void run(String... args) {
        cacheService.refreshAllCache();//刷新所有緩存
    }
}
