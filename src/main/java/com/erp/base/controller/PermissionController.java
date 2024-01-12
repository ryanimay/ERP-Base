package com.erp.base.controller;

import com.erp.base.model.dto.request.permission.BanRequest;
import com.erp.base.model.dto.request.permission.SecurityConfirmRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PermissionController {
    private PermissionService permissionService;
    @Autowired
    public void setPermissionService(PermissionService permissionService){
        this.permissionService = permissionService;
    }

    @GetMapping(Router.PERMISSION.LIST)
    public ResponseEntity<ApiResponse> list(){
        return permissionService.getPermissionList();
    }

    @GetMapping(Router.PERMISSION.ROLE)
    public ResponseEntity<ApiResponse> rolePermission(long roleId){
        return permissionService.getRolePermission(roleId);
    }

    @PutMapping(Router.PERMISSION.BAN)
    public ResponseEntity<ApiResponse> ban(@RequestBody BanRequest request){
        return permissionService.ban(request);
    }


    @PostMapping(Router.PERMISSION.SECURITY_CONFIRM)
    public ResponseEntity<ApiResponse> securityConfirm(@RequestBody SecurityConfirmRequest request){
        return permissionService.securityConfirm(request);
    }
}
