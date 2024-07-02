package com.erp.base.controller;

import com.erp.base.aspect.Loggable;
import com.erp.base.model.dto.request.department.DepartmentEditRequest;
import com.erp.base.model.dto.request.department.DepartmentRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "DepartmentController", description = "部門相關API")
public class DepartmentController {
    private DepartmentService departmentService;
    @Autowired
    public void setDepartmentService(DepartmentService departmentService){
        this.departmentService = departmentService;
    }

    @GetMapping(Router.DEPARTMENT.LIST)
    @Operation(summary = "部門清單")
    public ResponseEntity<ApiResponse> list(@Parameter(description = "部門清單請求") DepartmentRequest request){
        return departmentService.list(request);
    }

    @GetMapping(Router.DEPARTMENT.STAFF)
    @Operation(summary = "部門員工清單")
    public ResponseEntity<ApiResponse> staff(@Parameter(description = "部門ID") Long id){
        return departmentService.findStaffById(id);
    }
    @Loggable
    @PostMapping(Router.DEPARTMENT.EDIT)
    @Operation(summary = "編輯部門")
    public ResponseEntity<ApiResponse> edit(@Parameter(description = "編輯部門請求") @RequestBody DepartmentEditRequest request){
        return departmentService.edit(request);
    }
    @Loggable
    @DeleteMapping(Router.DEPARTMENT.REMOVE)
    @Operation(summary = "移除部門")
    public ResponseEntity<ApiResponse> remove(@Parameter(description = "部門ID") Long id){
        return departmentService.remove(id);
    }
}
