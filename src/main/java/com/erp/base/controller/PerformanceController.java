package com.erp.base.controller;

import com.erp.base.aspect.Loggable;
import com.erp.base.model.dto.request.PageRequestParam;
import com.erp.base.model.dto.request.performance.PerformanceAcceptRequest;
import com.erp.base.model.dto.request.performance.PerformanceRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.service.PerformanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PerformanceController {
    private PerformanceService performanceService;
    @Autowired
    public void setPerformanceService(PerformanceService performanceService){
        this.performanceService = performanceService;
    }
    //待審績效清單
    @GetMapping(Router.PERFORMANCE.PENDING_LIST)
    public ResponseEntity<ApiResponse> pendingList(PageRequestParam request){
        return performanceService.pendingList(request);
    }

    //依照輸入值搜尋特定條件績效清單
    @GetMapping(Router.PERFORMANCE.LIST)
    public ResponseEntity<ApiResponse> list(PerformanceRequest request){
        return performanceService.getList(request);
    }
    @Loggable
    @PostMapping(Router.PERFORMANCE.ADD)
    public ResponseEntity<ApiResponse> add(@RequestBody PerformanceRequest request){
        return performanceService.add(request);
    }
    @Loggable
    @PutMapping(Router.PERFORMANCE.UPDATE)
    public ResponseEntity<ApiResponse> update(@RequestBody PerformanceRequest request){
        return performanceService.save(request);
    }
    @Loggable
    @DeleteMapping(Router.PERFORMANCE.REMOVE)
    public ResponseEntity<ApiResponse> remove(Long eventId){
        return performanceService.remove(eventId);
    }
    @Loggable
    @PutMapping(Router.PERFORMANCE.ACCEPT)
    public ResponseEntity<ApiResponse> accept(@RequestBody PerformanceAcceptRequest request){
        return performanceService.accept(request);
    }
    @Loggable
    @GetMapping(Router.PERFORMANCE.CALCULATE)
    public ResponseEntity<ApiResponse> calculate(Long id) {
        return performanceService.calculate(id);
    }
}
