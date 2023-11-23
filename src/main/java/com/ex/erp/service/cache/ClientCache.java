package com.ex.erp.service.cache;

import com.ex.erp.dto.response.ClientResponse;
import com.ex.erp.model.ClientModel;
import com.ex.erp.model.PermissionModel;
import com.ex.erp.model.RoleModel;
import com.ex.erp.service.ClientService;
import com.ex.erp.service.PermissionService;
import com.ex.erp.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
@CacheConfig(cacheNames = "client")
@Transactional
public class ClientCache {
    private ClientService clientService;
    private RoleService roleService;
    private PermissionService permissionService;
    @Autowired
    public void setPermissionRepository(PermissionService permissionService){
        this.permissionService = permissionService;
    }
    @Autowired
    public void setRoleRepository(RoleService roleService){
        this.roleService = roleService;
    }
    @Autowired
    public void setClientService(ClientService clientService){
        this.clientService = clientService;
    }

    //有關使用者資訊，不存密碼
    @Cacheable(key = "'clientCache_' + #username")
    public ClientResponse getClient(String username) {
        ClientModel clientModel = clientService.findByUsername(username);
        return new ClientResponse(clientModel);
    }

    @CacheEvict(key = "'clientCache_' + #username")
    public void refreshClient(String username) {
    }

    @CacheEvict(allEntries = true)
    public void refreshClientAll() {
    }

    @Cacheable(key = "'roleCache'")
    public List<RoleModel>  getRole() {
        return roleService.findAll();
    }

    @Cacheable(key = "'permissionCache'")
    public List<PermissionModel> getPermission() {
        return permissionService.findAll();
    }

    //角色擁有權限
    @Cacheable(key = "'rolePermission_' + #role.id")
    public Collection<? extends GrantedAuthority> getRolePermission(RoleModel role) {
        return role.getPermissions();
    }
}
