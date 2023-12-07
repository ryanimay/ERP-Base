package com.ex.erp.controller;

import com.ex.erp.dto.response.ApiResponse;
import com.ex.erp.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
