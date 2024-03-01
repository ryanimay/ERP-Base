package com.erp.base.controller;

import com.erp.base.aspect.Loggable;
import com.erp.base.model.dto.request.salary.SalaryRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.service.SalaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "SalaryController", description = "薪資相關API")
public class SalaryController {
    private SalaryService salaryService;
    @Autowired
    public void setSalaryService(SalaryService salaryService){
        this.salaryService = salaryService;
    }
    //root清單
    @GetMapping(Router.SALARY.ROOTS)
    @Operation(summary = "薪資設定清單")
    public ResponseEntity<ApiResponse> roots(SalaryRequest request){
        return salaryService.getRoots(request);
    }
    //編輯或新增Root
    @Loggable
    @PostMapping(Router.SALARY.EDIT_ROOT)
    @Operation(summary = "新增/編輯薪資設定")
    public ResponseEntity<ApiResponse> editRoot(@RequestBody SalaryRequest request){
        return salaryService.editRoot(request);
    }

    //----------------------------------以下展示用
    //個人薪資單列表
    @GetMapping(Router.SALARY.GET)
    @Operation(summary = "個人薪資單")
    public ResponseEntity<ApiResponse> get(){
        return salaryService.get();
    }
    //個人薪資單詳細
    @GetMapping(Router.SALARY.INFO)
    @Operation(summary = "個人薪資單詳細")
    public ResponseEntity<ApiResponse> info(@Parameter(description = "用戶ID") long id){
        return salaryService.info(id);
    }
}
