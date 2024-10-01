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
import com.erp.base.service.cache.OtherCache;
import com.erp.base.service.cache.RolePermissionCache;
import com.erp.base.service.cache.TokenBlackList;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 所有緩存相關的調用都集中在這個service
 */
@Service
@Transactional
public class CacheService {
    private final ClientCache clientCache;
    private final RolePermissionCache rolePermissionCache;
    private final TokenBlackList tokenBlackList;
    private final OtherCache otherCache;
    private final CacheManager cacheManager;

    @Autowired
    public CacheService(ClientCache clientCache, RolePermissionCache rolePermissionCache, TokenBlackList tokenBlackList, OtherCache otherCache, CacheManager cacheManager) {
        this.cacheManager = cacheManager;
        this.clientCache = clientCache;
        this.rolePermissionCache = rolePermissionCache;
        this.tokenBlackList = tokenBlackList;
        this.otherCache = otherCache;
    }

    /**
     * 全刷
     */
    public void refreshAllCache() {
        cacheManager.getCacheNames().forEach(name -> Objects.requireNonNull(cacheManager.getCache(name)).clear());
    }

    public ClientIdentityDto getClient(Long id) {
        return clientCache.getClient(id);
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

    public List<PermissionListResponse> getPermissionMap() {
        return rolePermissionCache.getPermissionList();
    }

    public Boolean permissionStatus(String requestedUrl) {
        return rolePermissionCache.permissionStatus(requestedUrl);
    }

    public DepartmentModel getDepartment(Long departmentId) {
        return rolePermissionCache.getDepartment(departmentId);
    }

    /**
     * 傳入param格式為'cacheName.cacheKey'
     */
    public void refreshCache(String param) {
        if(param.contains(CacheConstant.SPLIT_CONSTANT)){
            String[] key = param.split(CacheConstant.SPLIT_CONSTANT);
            if(StringUtils.isNotEmpty(key[1])){
                Objects.requireNonNull(cacheManager.getCache(key[0])).evict(key[1]);
                return;
            }
            param = key[0];
        }
        Objects.requireNonNull(cacheManager.getCache(param)).clear();
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

    public Map<String, Object> getSystemInfo() {
        Map<String, Object> map = new HashMap<>();
        map.put("systemUser", clientCache.getSystemUser());
        map.put("systemDepartment", otherCache.getSystemDepartment());
        map.put("systemProject", otherCache.getSystemProject());
        map.put("systemProcure", otherCache.getSystemProcure());
        return map;
    }
}
