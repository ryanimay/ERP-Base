package com.ex.erp.service.security;

import com.ex.erp.model.ClientModel;
import com.ex.erp.service.cache.ClientCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 登入驗證使用者的類
 * */
@Service
@Transactional
public class UserDetailFactory {
    private ClientCache ClientCache;
    @Autowired
    public void setCache(ClientCache ClientCache) {
        this.ClientCache = ClientCache;
    }
    public UserDetailImpl build( ClientModel client) {
        return new UserDetailImpl(client, ClientCache);
    }
}
