package com.erp.base.service.cache;

import com.erp.base.model.constant.cache.CacheConstant;
import com.erp.base.model.dto.response.ClientNameObject;
import com.erp.base.model.dto.security.ClientIdentityDto;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.service.ClientService;
import com.erp.base.tool.LogFactory;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用戶相關緩存
 */
@Service
@CacheConfig(cacheNames = CacheConstant.CLIENT.NAME_CLIENT)
public class ClientCache implements ICache{
    LogFactory LOG = new LogFactory(ClientCache.class);
    private ClientService clientService;

    @Autowired
    public void setClientService(@Lazy ClientService clientService) {
        this.clientService = clientService;
    }

    //有關使用者資訊，密碼有經過springSecurity加密
    @Cacheable(key = CacheConstant.CLIENT.CLIENT + " + #id")
    public ClientIdentityDto getClient(Long id) {
        ClientModel model = clientService.findById(id);
        if(model == null) return null;
        Hibernate.initialize(model.getRoles());
        Hibernate.initialize(model.getNotifications());
        return new ClientIdentityDto(model);
    }

    @CacheEvict(key = CacheConstant.CLIENT.CLIENT + " + #id")
    public void refreshClient(Long id) {
    }

    @CacheEvict(allEntries = true)
    public void refreshAll() {
        LOG.info("refresh all client cache");
    }

    @Cacheable(key = CacheConstant.CLIENT.CLIENT_NAME_LIST)
    public List<ClientNameObject> getClientNameList() {
        return clientService.getClientNameList();
    }
}
