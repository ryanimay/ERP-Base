package com.erp.base.controller;

import com.erp.base.dto.request.role.RolePermissionRequest;
import com.erp.base.dto.request.role.RoleRequest;
import com.erp.base.dto.response.ApiResponse;
import com.erp.base.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class RoleController {
    private final RoleService roleService;
    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping(Router.ROLE.LIST)
    public ResponseEntity<ApiResponse> list(){
        return roleService.roleNameList();
    }
    @PutMapping(Router.ROLE.UPDATE)
    public ResponseEntity<ApiResponse> update(@RequestBody RoleRequest request){
        return roleService.updateName(request);
    }

    @PostMapping(Router.ROLE.ADD)
    public ResponseEntity<ApiResponse> add(@RequestBody RoleRequest request){
        return roleService.addRole(request);
    }

    @PostMapping(Router.ROLE.ROLE_PERMISSION)
    public ResponseEntity<ApiResponse> rolePermission(@RequestBody RolePermissionRequest request){
        return roleService.updateRolePermission(request);
    }

    @DeleteMapping(Router.ROLE.REMOVE)
    public ResponseEntity<ApiResponse> remove(@RequestBody RoleRequest request){
        return roleService.deleteById(request);
    }
}
