package com.erp.base.controller;

import com.erp.base.aspect.Loggable;
import com.erp.base.model.dto.request.IdRequest;
import com.erp.base.model.dto.request.role.RolePermissionRequest;
import com.erp.base.model.dto.request.role.RoleRequest;
import com.erp.base.model.dto.request.role.RoleRouterRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "RoleController", description = "角色相關API")
public class RoleController {
    private final RoleService roleService;
    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping(Router.ROLE.LIST)
    @Operation(summary = "角色清單")
    public ResponseEntity<ApiResponse> list(){
        return roleService.roleNameList();
    }
    @Loggable
    @PutMapping(Router.ROLE.UPDATE)
    @Operation(summary = "編輯角色")
    public ResponseEntity<ApiResponse> update(@RequestBody RoleRequest request){
        return roleService.updateName(request);
    }
    @Loggable
    @PostMapping(Router.ROLE.ADD)
    @Operation(summary = "新增角色")
    public ResponseEntity<ApiResponse> add(@RequestBody RoleRequest request){
        return roleService.addRole(request);
    }

    @PostMapping(Router.ROLE.ROLE_PERMISSION)
    @Operation(summary = "編輯角色權限")
    public ResponseEntity<ApiResponse> rolePermission(@RequestBody RolePermissionRequest request){
        return roleService.updateRolePermission(request);
    }
    @Loggable
    @DeleteMapping(Router.ROLE.REMOVE)
    @Operation(summary = "移除角色")
    public ResponseEntity<ApiResponse> remove(@Parameter(description = "角色ID") IdRequest request){
        return roleService.deleteById(request);
    }

    @PostMapping(Router.ROLE.ROLE_ROUTER)
    @Operation(summary = "編輯角色頁面權限")
    public ResponseEntity<ApiResponse> roleRouter(@RequestBody RoleRouterRequest request){
        return roleService.updateRoleRouter(request);
    }
}
