package com.erp.base.controller;

import com.erp.base.aspect.Loggable;
import com.erp.base.model.dto.request.permission.BanRequest;
import com.erp.base.model.dto.request.permission.SecurityConfirmRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "PermissionController", description = "權限相關API")
public class PermissionController {
    private PermissionService permissionService;
    @Autowired
    public void setPermissionService(PermissionService permissionService){
        this.permissionService = permissionService;
    }

    @GetMapping(Router.PERMISSION.LIST)
    @Operation(summary = "權限清單")
    public ResponseEntity<ApiResponse> list(){
        return permissionService.getPermissionList();
    }

    @GetMapping(Router.PERMISSION.ROLE)
    @Operation(summary = "角色權限")
    public ResponseEntity<ApiResponse> rolePermission(@Parameter(description = "角色ID") long roleId){
        return permissionService.getRolePermission(roleId);
    }
    @Loggable
    @PutMapping(Router.PERMISSION.BAN)
    @Operation(summary = "權限停用/啟用")
    public ResponseEntity<ApiResponse> ban(@RequestBody BanRequest request){
        return permissionService.ban(request);
    }


    @PostMapping(Router.PERMISSION.SECURITY_CONFIRM)
    @Operation(summary = "安全認證")
    public ResponseEntity<ApiResponse> securityConfirm(@RequestBody SecurityConfirmRequest request){
        return permissionService.securityConfirm(request);
    }
}
