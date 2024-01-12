package com.erp.base.service.cache;

import com.erp.base.model.dto.security.RolePermissionDto;
import com.erp.base.model.entity.PermissionModel;
import com.erp.base.model.entity.RoleModel;
import com.erp.base.model.entity.RouterModel;
import com.erp.base.model.entity.UserModel;
import com.erp.base.service.PermissionService;
import com.erp.base.service.RoleService;
import com.erp.base.service.RouterService;
import com.erp.base.tool.JsonTool;
import com.erp.base.tool.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheNames = "client")
@Transactional
public class ClientCache {
    LogFactory LOG = new LogFactory(ClientCache.class);
    private RoleService roleService;
    private PermissionService permissionService;
    private RouterService routerService;
    @Autowired
    public void setRouterService(@Lazy RouterService routerService){
        this.routerService = routerService;
    }
    @Autowired
    public void setPermissionService(@Lazy PermissionService permissionService){
        this.permissionService = permissionService;
    }
    @Autowired
    public void setRoleService(@Lazy RoleService roleService){
        this.roleService = roleService;
    }

    //有關使用者資訊，不存密碼
    @Cacheable(key = "'clientCache_' + #username")
    public UserModel getClient(String username) {
//        ClientModel model = clientService.findByUsername(username);
//        Hibernate.initialize(model.getRole().getPermissions());//確保保存在redis的實體完整加載
//        return model;
        return null;
    }

    @CacheEvict(key = "'clientCache_' + #username")
    public void refreshClient(String username) {
    }

    @CacheEvict(allEntries = true)
    public void refreshClientAll() {
        LOG.info("refresh all cache");
    }

    @Cacheable(key = "'roleCache'")
    public Map<Long, RoleModel> getRole() {
        List<RoleModel> allRoles = roleService.findAll();
        return allRoles.stream().collect(Collectors.toMap(RoleModel::getId, role -> role));
    }

    @CacheEvict(key = "'roleCache'")
    public void refreshRole() {
    }

    @Cacheable(key = "'permissionCache'")
    public List<PermissionModel> getPermission() {
        List<PermissionModel> allPermission = permissionService.findAll();
        LOG.info("all permission: {0}", JsonTool.toJson(allPermission));
        return allPermission;
    }
    @CacheEvict(key = "'permissionCache'")
    public void refreshPermission() {
    }
    @Cacheable(key = "'permissionMap'")
    public Map<String, List<PermissionModel>> getPermissionMap() {
        List<PermissionModel> allPermission = permissionService.findAll();
        Map<String, List<PermissionModel>> map = new HashMap<>();
        for(PermissionModel permission : allPermission){
            String key = permission.getAuthority().split(":")[0];
            List<PermissionModel> list = map.computeIfAbsent(key, k -> new ArrayList<>());
            list.add(permission);
        }
        return map;
    }
    @CacheEvict(key = "'permissionMap'")
    public void refreshPermissionMap() {
    }

    //角色擁有權限
    @Cacheable(key = "'rolePermission_' + #id")
    public Set<RolePermissionDto> getRolePermission(long id) {
        RoleModel role = roleService.findById(id);
        return role.getRolePermissionsDto();
    }
    //角色可訪問路由
    @Cacheable(key = "'roleRouter_' + #id")
    public Set<RouterModel> getRoleRouter(long id) {
        RoleModel role = roleService.findById(id);
        return role.getRouters();
    }

    @Cacheable(key = "'routers'")
    public List<RouterModel> getRouters() {
        return routerService.findAll();
    }

    @CacheEvict(key = "'routers'")
    public void refreshRouters() {
    }
}
