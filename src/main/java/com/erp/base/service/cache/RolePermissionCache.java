package com.erp.base.service.cache;

import com.erp.base.model.constant.cache.CacheConstant;
import com.erp.base.model.dto.response.MenuResponse;
import com.erp.base.model.dto.response.role.PermissionListResponse;
import com.erp.base.model.dto.security.RolePermissionDto;
import com.erp.base.model.entity.DepartmentModel;
import com.erp.base.model.entity.PermissionModel;
import com.erp.base.model.entity.RoleModel;
import com.erp.base.service.DepartmentService;
import com.erp.base.service.MenuService;
import com.erp.base.service.PermissionService;
import com.erp.base.service.RoleService;
import com.erp.base.tool.LogFactory;
import com.erp.base.tool.ObjectTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
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
public class RolePermissionCache {
    LogFactory LOG = new LogFactory(RolePermissionCache.class);
    private RoleService roleService;
    private PermissionService permissionService;
    private DepartmentService departmentService;
    private MenuService menuService;

    @Autowired
    public void setMenuService(@Lazy MenuService menuService) {
        this.menuService = menuService;
    }

    @Autowired
    public void setDepartmentService(@Lazy DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @Autowired
    public void setPermissionService(@Lazy PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @Autowired
    public void setRoleService(@Lazy RoleService roleService) {
        this.roleService = roleService;
    }

    @Cacheable(key = "'" +CacheConstant.ROLE_PERMISSION.ROLES + "'")
    public Map<Long, RoleModel> getRole() {
        List<RoleModel> allRoles = roleService.findAll();
        return allRoles.stream().collect(Collectors.toMap(RoleModel::getId, role -> role));
    }

    @Cacheable(key = "'" +CacheConstant.ROLE_PERMISSION.PERMISSIONS + "'")
    public List<PermissionModel> getPermission() {
        List<PermissionModel> allPermission = permissionService.findAll();
        LOG.info("all permission: {0}", ObjectTool.toJson(allPermission));
        return allPermission;
    }

    @Cacheable(key = "'" + CacheConstant.ROLE_PERMISSION.PERMISSIONS_MAP + "'")
    public List<PermissionListResponse> getPermissionList() {
        List<PermissionModel> allPermission = getPermission();
        Map<String, List<PermissionModel>> map = new HashMap<>();
        for (PermissionModel permission : allPermission) {
            String key = permission.getAuthority().split("_")[0];
            List<PermissionModel> list = map.computeIfAbsent(key, k -> new ArrayList<>());
            list.add(permission);
        }
        List<PermissionListResponse> responseList = new ArrayList<>();
        //不需要權限的放最前面顯示
        if(map.containsKey("*")){
            responseList.add(new PermissionListResponse("*", map.get("*")));
            map.remove("*");
        }
        map.keySet().forEach(key -> responseList.add(new PermissionListResponse(key, map.get(key))));
        return responseList;
    }

    //角色擁有權限
    @Cacheable(key = "'" + CacheConstant.ROLE_PERMISSION.ROLE_PERMISSION + "'" + " + #id")
    public Set<RolePermissionDto> getRolePermission(long id) {
        RoleModel role = roleService.findById(id);
        return role == null ? null : role.getRolePermissionsDto();
    }

    @Cacheable(key = "'" + CacheConstant.ROLE_PERMISSION.PERMISSION_STATUS + "'" + " + #path")
    public Boolean permissionStatus(String path) {
        return permissionService.checkPermissionIfDeny(path);
    }

    @Cacheable(key = "'" + CacheConstant.ROLE_PERMISSION.DEPARTMENT + "'" + " + #id")
    public DepartmentModel getDepartment(Long id) {
        return departmentService.findById(id);
    }

    @Cacheable(key = "'" + CacheConstant.ROLE_PERMISSION.MENU_TREE + "'")
    public List<MenuResponse> findMenuTree() {
        return menuService.findAllTree();
    }

    @Cacheable(key = "'" + CacheConstant.ROLE_PERMISSION.ROLE_MENU + "'" + " + #id")
    public List<MenuResponse> getRoleMenu(Long id) {
        return menuService.getRoleMenu(id);
    }
}
