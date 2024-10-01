package com.erp.base.service.cache;

import com.erp.base.model.constant.cache.CacheConstant;
import com.erp.base.model.dto.response.ClientNameObject;
import com.erp.base.model.dto.security.ClientIdentityDto;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.service.ClientService;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用戶相關緩存
 */
@Service
@CacheConfig(cacheNames = CacheConstant.CLIENT.NAME_CLIENT)
public class ClientCache{
    private ClientService clientService;

    @Autowired
    public void setClientService(@Lazy ClientService clientService) {
        this.clientService = clientService;
    }

    //有關使用者資訊，密碼有經過springSecurity加密
    @Cacheable(key = "'" +CacheConstant.CLIENT.CLIENT + "'" + " + #id")
    public ClientIdentityDto getClient(Long id) {
        ClientModel model = clientService.findById(id);
        if(model == null) return null;
        Hibernate.initialize(model.getRoles());
        return new ClientIdentityDto(model);
    }

    @Cacheable(key = "'" +CacheConstant.CLIENT.CLIENT_NAME_LIST+ "'")
    public List<ClientNameObject> getClientNameList() {
        return clientService.getClientNameList();
    }

    //系統用戶數量
    @Cacheable(key = "'" +CacheConstant.CLIENT.SYSTEM_USER+ "'")
    public String getSystemUser() {
        return clientService.getSystemUser();
    }
}
