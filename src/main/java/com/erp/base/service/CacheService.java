package com.erp.base.service;

import com.erp.base.model.dto.response.ClientNameObject;
import com.erp.base.model.dto.security.RolePermissionDto;
import com.erp.base.model.entity.*;
import com.erp.base.service.cache.ClientCache;
import com.erp.base.service.cache.ICache;
import com.erp.base.service.cache.RolePermissionCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 所有緩存相關的調用都集中在這個service
 */
@Service
@Transactional
public class CacheService {
    private final ClientCache clientCache;
    private final RolePermissionCache rolePermissionCache;
    private final Map<String, ICache> cacheMap = new HashMap<>();

    @Autowired
    public CacheService(ClientCache clientCache, RolePermissionCache rolePermissionCache) {
        this.clientCache = clientCache;
        this.rolePermissionCache = rolePermissionCache;
        cacheMap.put("CLIENT", clientCache);
        cacheMap.put("ROLE_PERMISSION", rolePermissionCache);
    }

    /**
     * 全刷
     */
    public void refreshAllCache() {
        refreshClient();
        refreshRolePermission();
    }

    public void refreshClient(){
        clientCache.refreshAll();
    }

    public void refreshRolePermission(){
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

    public List<RouterModel> getRouters() {
        return rolePermissionCache.getRouters();
    }

    public Set<RouterModel> getRoleRouter(long roleId) {
        return rolePermissionCache.getRoleRouter(roleId);
    }

    public Boolean permissionStatus(String requestedUrl) {
        return rolePermissionCache.permissionStatus(requestedUrl);
    }

    public DepartmentModel getDepartment(Long departmentId) {
        return rolePermissionCache.getDepartment(departmentId);
    }

    public void refreshCache(String cacheKey) {
        ICache cache = cacheMap.get(cacheKey);
        if(cache == null) throw new IllegalArgumentException("No cache found for key: " + cacheKey);
        cache.refreshAll();
    }
}
