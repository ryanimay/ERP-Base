package com.ex.erp.service.cache;

import com.ex.erp.model.ClientModel;
import com.ex.erp.model.PermissionModel;
import com.ex.erp.model.RoleModel;
import com.ex.erp.service.ClientService;
import com.ex.erp.service.PermissionService;
import com.ex.erp.service.RoleService;
import com.ex.erp.tool.JsonTool;
import com.ex.erp.tool.LogFactory;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
@CacheConfig(cacheNames = "client")
@Transactional
public class ClientCache {
    LogFactory LOG = new LogFactory(ClientCache.class);
    private ClientService clientService;
    private RoleService roleService;
    private PermissionService permissionService;
    @Autowired
    public void setPermissionService(PermissionService permissionService){
        this.permissionService = permissionService;
    }
    @Autowired
    public void setRoleService(RoleService roleService){
        this.roleService = roleService;
    }
    @Autowired
    public void setClientService(@Lazy ClientService clientService){
        this.clientService = clientService;
    }

    //有關使用者資訊，不存密碼
    @Cacheable(key = "'clientCache_' + #username")
    public ClientModel getClient(String username) {
        ClientModel model = clientService.findByUsername(username);
        Hibernate.initialize(model.getRole().getPermissions());//確保保存在redis的實體完整加載
        return model;
    }

    @CacheEvict(key = "'clientCache_' + #username")
    public void refreshClient(String username) {
    }

    @CacheEvict(allEntries = true)
    public void refreshClientAll() {
        LOG.info("refresh all cache");
    }

    @Cacheable(key = "'roleCache'")
    public List<RoleModel>  getRole() {
        return roleService.findAll();
    }

    //返回結構是由父->子
    @Cacheable(key = "'permissionCache'")
    public List<PermissionModel> getPermission() {
        List<PermissionModel> allPermission = permissionService.findAll();
        LOG.info("all permission: {0}", JsonTool.toJson(allPermission));
        return allPermission;
    }

    //角色擁有權限
    @Cacheable(key = "'rolePermission_' + #role.id")
    public Collection<? extends GrantedAuthority> getRolePermission(RoleModel role) {
        return role.getRolePermissionsDto();
    }
}
