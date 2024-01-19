package com.erp.base.controller;

import com.erp.base.model.dto.request.salary.EditSalaryRootRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.service.SalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SalaryController {
    private SalaryService salaryService;
    @Autowired
    public void setSalaryService(SalaryService salaryService){
        this.salaryService = salaryService;
    }
    //root清單
    @GetMapping(Router.SALARY.ROOTS)
    public ResponseEntity<ApiResponse> roots(){
        return salaryService.getRoots();
    }
    //找rootByUserId
    @GetMapping(Router.SALARY.ROOT_BY)
    public ResponseEntity<ApiResponse> rootBy(long id){
        return salaryService.getRootById(id);
    }
    //編輯或新增Root
    @PostMapping(Router.SALARY.EDIT_ROOT)
    public ResponseEntity<ApiResponse> editRoot(@RequestBody EditSalaryRootRequest editSalaryRootRequest){
        return salaryService.editRoot(editSalaryRootRequest);
    }

    //----------------------------------以下展示用
    //個人薪資單列表
    @GetMapping(Router.SALARY.GET)
    public ResponseEntity<ApiResponse> get(){
        return salaryService.get();
    }
    //個人薪資單詳細
    @GetMapping(Router.SALARY.INFO)
    public ResponseEntity<ApiResponse> info(long id){
        return salaryService.info(id);
    }
}