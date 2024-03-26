package com.erp.base.service.cache;

import com.erp.base.model.constant.cache.CacheConstant;
import com.erp.base.model.dto.response.RouterResponse;
import com.erp.base.model.dto.security.RolePermissionDto;
import com.erp.base.model.entity.DepartmentModel;
import com.erp.base.model.entity.PermissionModel;
import com.erp.base.model.entity.RoleModel;
import com.erp.base.model.entity.RouterModel;
import com.erp.base.service.DepartmentService;
import com.erp.base.service.PermissionService;
import com.erp.base.service.RoleService;
import com.erp.base.service.RouterService;
import com.erp.base.tool.LogFactory;
import com.erp.base.tool.ObjectTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 角色權限相關緩存
 */
@Service
@CacheConfig(cacheNames = CacheConstant.ROLE_PERMISSION.NAME_ROLE_PERMISSION)
public class RolePermissionCache implements ICache{
    LogFactory LOG = new LogFactory(RolePermissionCache.class);
    private RoleService roleService;
    private PermissionService permissionService;
    private RouterService routerService;
    private DepartmentService departmentService;

    @Autowired
    public void setDepartmentService(@Lazy DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @Autowired
    public void setRouterService(@Lazy RouterService routerService) {
        this.routerService = routerService;
    }

    @Autowired
    public void setPermissionService(@Lazy PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @Autowired
    public void setRoleService(@Lazy RoleService roleService) {
        this.roleService = roleService;
    }

    @Cacheable(key = CacheConstant.ROLE_PERMISSION.ROLES)
    public Map<Long, RoleModel> getRole() {
        List<RoleModel> allRoles = roleService.findAll();
        return allRoles.stream().collect(Collectors.toMap(RoleModel::getId, role -> role));
    }

    @CacheEvict(key = CacheConstant.ROLE_PERMISSION.ROLES)
    public void refreshRole() {
    }

    @Cacheable(key = CacheConstant.ROLE_PERMISSION.PERMISSIONS)
    public List<PermissionModel> getPermission() {
        List<PermissionModel> allPermission = permissionService.findAll();
        LOG.info("all permission: {0}", ObjectTool.toJson(allPermission));
        return allPermission;
    }

    @Cacheable(key = CacheConstant.ROLE_PERMISSION.PERMISSIONS_MAP)
    public Map<String, List<PermissionModel>> getPermissionMap() {
        List<PermissionModel> allPermission = getPermission();
        Map<String, List<PermissionModel>> map = new HashMap<>();
        for (PermissionModel permission : allPermission) {
            String key = permission.getAuthority().split("_")[0];
            List<PermissionModel> list = map.computeIfAbsent(key, k -> new ArrayList<>());
            list.add(permission);
        }
        return map;
    }

    //角色擁有權限
    @Cacheable(key = CacheConstant.ROLE_PERMISSION.ROLE_PERMISSION + " + #id")
    public Set<RolePermissionDto> getRolePermission(long id) {
        RoleModel role = roleService.findById(id);
        return role.getRolePermissionsDto();
    }

    //角色可訪問路由
    @Cacheable(key = CacheConstant.ROLE_PERMISSION.ROLE_ROUTER + " + #id")
    public Set<RouterResponse> getRoleRouter(long id) {
        RoleModel role = roleService.findById(id);
        Set<RouterModel> routers = role.getRouters();
        return routers.stream().map(RouterResponse::new).collect(Collectors.toSet());
    }

    @Cacheable(key = CacheConstant.ROLE_PERMISSION.PERMISSION_STATUS + " + #path")
    public Boolean permissionStatus(String path) {
        return permissionService.checkPermissionIfDeny(path);
    }

    @CacheEvict(allEntries = true)
    public void refreshAll() {
        LOG.info("refresh all rolePermission cache");
    }

    @Cacheable(key = CacheConstant.ROLE_PERMISSION.ROUTERS)
    public List<RouterModel> getRouters() {
        return routerService.findAll();
    }

    @Cacheable(key = CacheConstant.ROLE_PERMISSION.DEPARTMENT + " + #id")
    public DepartmentModel getDepartment(Long id) {
        return departmentService.findById(id);
    }
}
