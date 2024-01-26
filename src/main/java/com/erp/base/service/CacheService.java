package com.erp.base.service;

import com.erp.base.model.dto.response.ClientNameObject;
import com.erp.base.model.dto.security.RolePermissionDto;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.entity.PermissionModel;
import com.erp.base.model.entity.RoleModel;
import com.erp.base.model.entity.RouterModel;
import com.erp.base.service.cache.ClientCache;
import com.erp.base.service.cache.RolePermissionCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 所有緩存相關的調用都集中在這個service
 */
@Service
@Transactional
public class CacheService {
    private ClientCache clientCache;
    private RolePermissionCache rolePermissionCache;

    @Autowired
    public void setRolePermissionCache(RolePermissionCache rolePermissionCache) {
        this.rolePermissionCache = rolePermissionCache;
    }

    @Autowired
    public void setClientCache(ClientCache clientCache) {
        this.clientCache = clientCache;
    }

    /**
     * 全刷
     */
    public void refreshAllCache() {
        clientCache.refreshAll();
        rolePermissionCache.refreshAll();
    }

    public ClientModel getClient(String username) {
        return clientCache.getClient(username);
    }

    public void refreshClient(String username) {
        clientCache.refreshClient(username);
    }

    public List<ClientNameObject> getClientNameList() {
        return clientCache.getClientNameList();
    }

    public Set<RolePermissionDto> getRolePermission(long id) {
        return rolePermissionCache.getRolePermission(id);
    }

    public Map<Long, RoleModel> getRole() {
        return rolePermissionCache.getRole();
    }

    public void refreshRole() {
        rolePermissionCache.refreshRole();
    }

    public Map<String, List<PermissionModel>> getPermissionMap() {
        return rolePermissionCache.getPermissionMap();
    }

    public void refreshPermission() {
        rolePermissionCache.refreshPermission();
    }

    public void refreshPermissionMap() {
        rolePermissionCache.refreshPermissionMap();
    }

    public List<RouterModel> getRouters() {
        return rolePermissionCache.getRouters();
    }

    public Set<RouterModel> getRoleRouter(long roleId) {
        return rolePermissionCache.getRoleRouter(roleId);
    }

    public Boolean permissionStatus(String requestedUrl) {
        return rolePermissionCache.permissionStatus(requestedUrl);
    }
}
