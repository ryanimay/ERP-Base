package com.ex.erp.controller;

import com.ex.erp.dto.request.permission.BanRequest;
import com.ex.erp.dto.response.ApiResponse;
import com.ex.erp.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @GetMapping("/tree")
    public ResponseEntity<ApiResponse> tree(){
        return permissionService.getPermissionTreeCache();
    }

    @PutMapping("/ban")
    public ResponseEntity<ApiResponse> ban(BanRequest request){
        return permissionService.ban(request);
    }
}
