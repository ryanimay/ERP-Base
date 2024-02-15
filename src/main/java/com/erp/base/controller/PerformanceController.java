package com.erp.base.controller;

import com.erp.base.aspect.Loggable;
import com.erp.base.model.dto.request.PageRequestParam;
import com.erp.base.model.dto.request.performance.PerformanceAcceptRequest;
import com.erp.base.model.dto.request.performance.PerformanceRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.service.PerformanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "PerformanceController", description = "績效相關API")
public class PerformanceController {
    private PerformanceService performanceService;
    @Autowired
    public void setPerformanceService(PerformanceService performanceService){
        this.performanceService = performanceService;
    }
    @GetMapping(Router.PERFORMANCE.PENDING_LIST)
    @Operation(summary = "待審績效清單")
    public ResponseEntity<ApiResponse> pendingList(PageRequestParam request){
        return performanceService.pendingList(request);
    }

    @GetMapping(Router.PERFORMANCE.LIST)
    @Operation(summary = "績效清單")
    public ResponseEntity<ApiResponse> list(PerformanceRequest request){
        return performanceService.getList(request);
    }
    @Loggable
    @PostMapping(Router.PERFORMANCE.ADD)
    @Operation(summary = "新增績效")
    public ResponseEntity<ApiResponse> add(@RequestBody PerformanceRequest request){
        return performanceService.add(request);
    }
    @Loggable
    @PutMapping(Router.PERFORMANCE.UPDATE)
    @Operation(summary = "更新績效")
    public ResponseEntity<ApiResponse> update(@RequestBody PerformanceRequest request){
        return performanceService.save(request);
    }
    @Loggable
    @DeleteMapping(Router.PERFORMANCE.REMOVE)
    @Operation(summary = "移除績效")
    public ResponseEntity<ApiResponse> remove(@Parameter(description = "績效ID") Long eventId){
        return performanceService.remove(eventId);
    }
    @Loggable
    @PutMapping(Router.PERFORMANCE.ACCEPT)
    @Operation(summary = "審核績效")
    public ResponseEntity<ApiResponse> accept(@RequestBody PerformanceAcceptRequest request){
        return performanceService.accept(request);
    }
    @Loggable
    @GetMapping(Router.PERFORMANCE.CALCULATE)
    @Operation(summary = "計算績效")
    public ResponseEntity<ApiResponse> calculate(@Parameter(description = "績效ID") Long id) {
        return performanceService.calculate(id);
    }
}
