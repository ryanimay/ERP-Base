package com.erp.base.config.init;

import com.erp.base.model.dto.request.client.RegisterRequest;
import com.erp.base.service.CacheService;
import com.erp.base.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class InitializationRunner implements CommandLineRunner {
    private final CacheService cacheService;
    private final ClientService clientService;

    @Autowired
    public InitializationRunner(CacheService cacheService, ClientService clientService) {
        this.cacheService = cacheService;
        this.clientService = clientService;
    }

    @Override
    public void run(String... args) {
        insertInitSuperAccount();
        cacheService.refreshAllCache();//刷新所有緩存
    }

    /**
     * 初始化插入管理帳號
     * */
    private void insertInitSuperAccount() {
        clientService.register(new RegisterRequest("root", 0L, 2L));
    }
}
