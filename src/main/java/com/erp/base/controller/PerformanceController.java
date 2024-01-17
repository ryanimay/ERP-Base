package com.erp.base.controller;

import com.erp.base.model.dto.request.PageRequestParam;
import com.erp.base.model.dto.request.performance.AddPerformanceRequest;
import com.erp.base.model.dto.request.performance.PerformanceListRequest;
import com.erp.base.model.dto.request.performance.UpdatePerformanceRequest;
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

    @GetMapping(Router.PERFORMANCE.PENDING_LIST)
    public ResponseEntity<ApiResponse> pendingList(PageRequestParam request){
        return performanceService.pendingList(request);
    }

    @GetMapping(Router.PERFORMANCE.ALL_LIST)
    public ResponseEntity<ApiResponse> allList(PerformanceListRequest request){
        return performanceService.getAllList(request);
    }

    @GetMapping(Router.PERFORMANCE.LIST)
    public ResponseEntity<ApiResponse> list(PerformanceListRequest request){
        return performanceService.getList(request);
    }

    @PostMapping(Router.PERFORMANCE.ADD)
    public ResponseEntity<ApiResponse> add(@RequestBody AddPerformanceRequest request){
        return performanceService.add(request);
    }

    @PutMapping(Router.PERFORMANCE.UPDATE)
    public ResponseEntity<ApiResponse> update(@RequestBody UpdatePerformanceRequest request){
        return performanceService.save(request);
    }

    @DeleteMapping(Router.PERFORMANCE.REMOVE)
    public ResponseEntity<ApiResponse> remove(@RequestBody Long eventId){
        return performanceService.remove(eventId);
    }

    @PutMapping(Router.PERFORMANCE.ACCEPT)
    public ResponseEntity<ApiResponse> accept(@RequestBody Long eventId){
        return performanceService.accept(eventId);
    }
}
