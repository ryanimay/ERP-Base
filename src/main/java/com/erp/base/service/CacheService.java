package com.erp.base.service;

import com.erp.base.model.constant.cache.CacheConstant;
import com.erp.base.model.dto.response.ClientNameObject;
import com.erp.base.model.dto.response.MenuResponse;
import com.erp.base.model.dto.response.role.PermissionListResponse;
import com.erp.base.model.dto.security.ClientIdentityDto;
import com.erp.base.model.dto.security.RolePermissionDto;
import com.erp.base.model.entity.DepartmentModel;
import com.erp.base.model.entity.RoleModel;
import com.erp.base.service.cache.ClientCache;
import com.erp.base.service.cache.ICache;
import com.erp.base.service.cache.RolePermissionCache;
import com.erp.base.service.cache.TokenBlackList;
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
    private final TokenBlackList tokenBlackList;
    private final Map<String, ICache> cacheMap = new HashMap<>();

    @Autowired
    public CacheService(ClientCache clientCache, RolePermissionCache rolePermissionCache, TokenBlackList tokenBlackList) {
        this.clientCache = clientCache;
        this.rolePermissionCache = rolePermissionCache;
        this.tokenBlackList = tokenBlackList;
        cacheMap.put(CacheConstant.CLIENT.NAME_CLIENT, clientCache);
        cacheMap.put(CacheConstant.ROLE_PERMISSION.NAME_ROLE_PERMISSION, rolePermissionCache);
        cacheMap.put(CacheConstant.TOKEN_BLACK_LIST.TOKEN_BLACK_LIST, tokenBlackList);
    }

    /**
     * 全刷
     */
    public void refreshAllCache() {
        cacheMap.values().forEach(ICache::refreshAll);
    }

    public void refreshClient() {
        clientCache.refreshAll();
    }

    public void refreshRolePermission() {
        rolePermissionCache.refreshAll();
    }

    public ClientIdentityDto getClient(Long id) {
        return clientCache.getClient(id);
    }

    public void refreshClient(Long id) {
        clientCache.refreshClient(id);
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

    public List<PermissionListResponse> getPermissionMap() {
        return rolePermissionCache.getPermissionList();
    }

    public Boolean permissionStatus(String requestedUrl) {
        return rolePermissionCache.permissionStatus(requestedUrl);
    }

    public DepartmentModel getDepartment(Long departmentId) {
        return rolePermissionCache.getDepartment(departmentId);
    }

    public void refreshCache(String cacheKey) {
        ICache cache = cacheMap.get(cacheKey);
        if (cache == null) throw new IllegalArgumentException("No cache found for key: " + cacheKey);
        cache.refreshAll();
    }

    public void addTokenBlackList(String token) {
        tokenBlackList.add(token);
    }

    public boolean existsTokenBlackList(String token) {
        return tokenBlackList.exists(token);
    }

    public List<MenuResponse> findMenuTree() {
        return rolePermissionCache.findMenuTree();
    }

    public List<MenuResponse> getRoleMenu(Long id) {
        return rolePermissionCache.getRoleMenu(id);
    }
}
