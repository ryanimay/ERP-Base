package com.erp.base.controller;

import com.erp.base.model.dto.request.department.DepartmentRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class DepartmentController {
    private DepartmentService departmentService;
    @Autowired
    public void setDepartmentService(DepartmentService departmentService){
        this.departmentService = departmentService;
    }

    @GetMapping(Router.DEPARTMENT.LIST)
    public ResponseEntity<ApiResponse> list(DepartmentRequest request){
        return departmentService.list(request);
    }

    @GetMapping(Router.DEPARTMENT.STAFF)
    public ResponseEntity<ApiResponse> staff(Long id){
        return departmentService.findStaffById(id);
    }

    @PostMapping(Router.DEPARTMENT.EDIT)
    public ResponseEntity<ApiResponse> edit(@RequestBody DepartmentRequest request){
        return departmentService.edit(request);
    }

    @DeleteMapping(Router.DEPARTMENT.REMOVE)
    public ResponseEntity<ApiResponse> remove(Long id){
        return departmentService.remove(id);
    }
}
