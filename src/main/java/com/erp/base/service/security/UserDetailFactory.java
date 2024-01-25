package com.erp.base.service.security;

import com.erp.base.model.entity.ClientModel;
import com.erp.base.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 登入驗證使用者的類
 */
@Service
@Transactional
public class UserDetailFactory {
    private CacheService cacheService;

    @Autowired
    public void setCache(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    public UserDetailImpl build(ClientModel client) {
        return new UserDetailImpl(client, cacheService);
    }
}
