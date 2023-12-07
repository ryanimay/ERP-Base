package com.ex.erp.controller;

import com.ex.erp.dto.request.role.RoleRequest;
import com.ex.erp.dto.response.ApiResponse;
import com.ex.erp.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/role")
public class RoleController {
    private final RoleService roleService;
    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse> list(){
        return roleService.roleNameList();
    }
    @PutMapping("/update")
    public ResponseEntity<ApiResponse> update(@RequestBody RoleRequest request){
        return roleService.updateName(request);
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> add(@RequestBody RoleRequest request){
        return roleService.addRole(request);
    }
}
