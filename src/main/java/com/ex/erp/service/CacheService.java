package com.ex.erp.service;

import com.ex.erp.service.cache.ClientCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CacheService {
    private ClientCache clientCache;
    @Autowired
    public void setClientCache(ClientCache clientCache){
        this.clientCache = clientCache;
    }

    public void refreshAllCache(){
        clientCache.refreshClientAll();
    }
}
