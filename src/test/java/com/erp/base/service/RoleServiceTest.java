package com.erp.base.service;


import com.erp.base.model.constant.response.ApiResponseCode;
import com.erp.base.model.dto.request.IdRequest;
import com.erp.base.model.dto.request.role.RolePermissionRequest;
import com.erp.base.model.dto.request.role.RoleRequest;
import com.erp.base.model.dto.request.role.RoleMenuRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.dto.response.role.RoleNameResponse;
import com.erp.base.model.entity.RoleModel;
import com.erp.base.repository.RoleRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private CacheService cacheService;
    @Mock
    private DepartmentService departmentService;
    @Mock
    private ClientService clientService;
    @InjectMocks
    private RoleService roleService;

    @Test
    @DisplayName("更新角色名稱_名稱以存在_錯誤")
    void updateName_nameAlreadyExists_error() {
        Mockito.when(roleRepository.findByRoleName(Mockito.any())).thenReturn(Optional.of(new RoleModel()));
        ResponseEntity<ApiResponse> response = roleService.updateName(new RoleRequest());
        Assertions.assertEquals(ApiResponse.error(ApiResponseCode.NAME_ALREADY_EXIST), response);
    }

    @Test
    @DisplayName("更新角色名稱_未知錯誤_錯誤")
    void updateName_unknownError_error() {
        Mockito.when(roleRepository.findByRoleName(Mockito.any())).thenReturn(Optional.empty());
        Mockito.when(cacheService.getRole()).thenReturn(new HashMap<>());
        ResponseEntity<ApiResponse> response = roleService.updateName(new RoleRequest());
        Assertions.assertEquals(ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR), response);
    }

    @Test
    @DisplayName("更新角色名稱_成功")
    void updateName_ok() {
        Mockito.when(roleRepository.findByRoleName(Mockito.any())).thenReturn(Optional.empty());
        HashMap<Long, RoleModel> map = new HashMap<>();
        map.put(1L, new RoleModel(1L));
        Mockito.when(cacheService.getRole()).thenReturn(map);
        RoleRequest request = new RoleRequest();
        request.setId(1L);
        ResponseEntity<ApiResponse> response = roleService.updateName(request);
        Assertions.assertEquals(ApiResponse.success(ApiResponseCode.SUCCESS), response);
    }

    @Test
    @DisplayName("新增角色_名稱以存在_錯誤")
    void addRole_nameAlreadyExists_error() {
        Mockito.when(roleRepository.findByRoleName(Mockito.any())).thenReturn(Optional.of(new RoleModel()));
        ResponseEntity<ApiResponse> response = roleService.addRole(new RoleRequest());
        Assertions.assertEquals(ApiResponse.error(ApiResponseCode.NAME_ALREADY_EXIST), response);
    }

    @Test
    @DisplayName("新增角色_成功")
    void addRole_ok() {
        Mockito.when(roleRepository.findByRoleName(Mockito.any())).thenReturn(Optional.empty());
        Mockito.when(roleRepository.save(Mockito.any())).thenReturn(new RoleModel());
        ResponseEntity<ApiResponse> response = roleService.addRole(new RoleRequest());
        Assertions.assertEquals(ApiResponse.success(ApiResponseCode.SUCCESS, new RoleNameResponse()), response);
    }

    @Test
    @DisplayName("刪除角色_角色使用中_錯誤")
    void deleteById_inUsed_error() {
        Mockito.when(clientService.checkExistsRoleId(Mockito.anyLong())).thenReturn(true);
        IdRequest request = new IdRequest();
        request.setId(1L);
        ResponseEntity<ApiResponse> response = roleService.deleteById(request);
        Assertions.assertEquals(ApiResponse.error(ApiResponseCode.ROLE_IN_USE), response);
    }

    @Test
    @DisplayName("刪除角色_成功")
    void deleteById_ok() {
        Mockito.when(clientService.checkExistsRoleId(Mockito.anyLong())).thenReturn(false);
        roleService.setDepartmentService(departmentService);
        IdRequest request = new IdRequest();
        request.setId(1L);
        ResponseEntity<ApiResponse> response = roleService.deleteById(request);
        Assertions.assertEquals(ApiResponse.success(ApiResponseCode.SUCCESS), response);
    }

    @Test
    @DisplayName("編輯角色權限_未知ID_錯誤")
    void updateRolePermission_unknownId_error() {
        Mockito.when(cacheService.getRole()).thenReturn(new HashMap<>());
        ResponseEntity<ApiResponse> response = roleService.updateRolePermission(new RolePermissionRequest());
        Assertions.assertEquals(ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR), response);
    }

    @Test
    @DisplayName("編輯角色權限_成功")
    void updateRolePermission_ok() {
        HashMap<Long, RoleModel> map = new HashMap<>();
        map.put(1L, new RoleModel(1));
        Mockito.when(cacheService.getRole()).thenReturn(map);
        RolePermissionRequest request = new RolePermissionRequest();
        ArrayList<Long> permissionIds = new ArrayList<>();
        permissionIds.add(1L);
        permissionIds.add(2L);
        permissionIds.add(3L);
        request.setPermissionIds(permissionIds);
        request.setId(1L);
        ResponseEntity<ApiResponse> response = roleService.updateRolePermission(request);
        Assertions.assertEquals(ApiResponse.success(ApiResponseCode.SUCCESS), response);
    }

    @Test
    @DisplayName("編輯角色前端路由權限_未知ID_錯誤")
    void updateRoleRouter_unknownId_error() {
        Mockito.when(cacheService.getRole()).thenReturn(new HashMap<>());
        ResponseEntity<ApiResponse> response = roleService.updateRoleMenu(new RoleMenuRequest());
        Assertions.assertEquals(ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR), response);
    }

    @Test
    @DisplayName("編輯角色前端路由權限_成功")
    void updateRoleRouter_ok() {
        HashMap<Long, RoleModel> map = new HashMap<>();
        map.put(1L, new RoleModel(1));
        Mockito.when(cacheService.getRole()).thenReturn(map);
        RoleMenuRequest request = new RoleMenuRequest();
        ArrayList<Long> menuIds = new ArrayList<>();
        menuIds.add(1L);
        menuIds.add(2L);
        menuIds.add(3L);
        request.setMenuIds(menuIds);
        request.setId(1L);
        ResponseEntity<ApiResponse> response = roleService.updateRoleMenu(request);
        Assertions.assertEquals(ApiResponse.success(ApiResponseCode.SUCCESS), response);
    }
}