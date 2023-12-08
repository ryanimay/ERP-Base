package com.erp.base.controller;

import com.erp.base.dto.request.permission.BanRequest;
import com.erp.base.dto.request.permission.SecurityConfirmRequest;
import com.erp.base.dto.response.ApiResponse;
import com.erp.base.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/permission")
public class PermissionController {
    private PermissionService permissionService;
    @Autowired
    public void setPermissionService(PermissionService permissionService){
        this.permissionService = permissionService;
    }
    
    @GetMapping("/role")
    public ResponseEntity<ApiResponse> rolePermission(long roleId){
        return permissionService.getRolePermission(roleId);
    }

    @GetMapping("/tree")
    public ResponseEntity<ApiResponse> tree(){
        return permissionService.getPermissionTreeCache();
    }

    @PutMapping("/ban")
    public ResponseEntity<ApiResponse> ban(BanRequest request){
        return permissionService.ban(request);
    }


    @PostMapping("/securityConfirm")
    public ResponseEntity<ApiResponse> securityConfirm(@RequestBody SecurityConfirmRequest request){
        return permissionService.securityConfirm(request);
    }
}
