package com.erp.base.service.security;

import com.erp.base.model.UserModel;
import com.erp.base.service.cache.ClientCache;
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
    public UserDetailImpl build( UserModel client) {
        return new UserDetailImpl(client, ClientCache);
    }
}
