package com.erp.base.service.cache;

import com.erp.base.model.dto.response.ClientNameObject;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.entity.RouterModel;
import com.erp.base.service.ClientService;
import com.erp.base.service.RouterService;
import com.erp.base.tool.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用戶相關緩存
 */
@Service
@CacheConfig(cacheNames = "client")
@Transactional
public class ClientCache {
    LogFactory LOG = new LogFactory(ClientCache.class);
    private RouterService routerService;
    private ClientService clientService;

    @Autowired
    public void setClientService(@Lazy ClientService clientService) {
        this.clientService = clientService;
    }

    @Autowired
    public void setRouterService(@Lazy RouterService routerService) {
        this.routerService = routerService;
    }

    //有關使用者資訊，密碼有經過springSecurity加密
    @Cacheable(key = "'clientCache_' + #username")
    public ClientModel getClient(String username) {
        return clientService.findByUsername(username);
    }

    @CacheEvict(key = "'clientCache_' + #username")
    public void refreshClient(String username) {
    }

    @CacheEvict(allEntries = true)
    public void refreshAll() {
        LOG.info("refresh all client cache");
    }

    @Cacheable(key = "'routers'")
    public List<RouterModel> getRouters() {
        return routerService.findAll();
    }

    @CacheEvict(key = "'routers'")
    public void refreshRouters() {
    }

    @Cacheable(key = "'clientNameList'")
    public List<ClientNameObject> getClientNameList() {
        return clientService.getClientNameList();
    }
}
