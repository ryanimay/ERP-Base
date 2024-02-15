package com.erp.base.controller;

import com.erp.base.aspect.Loggable;
import com.erp.base.model.dto.request.procurement.ProcurementRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.service.ProcurementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "ProcurementController", description = "採購相關API")
public class ProcurementController {
    private ProcurementService procurementService;
    @Autowired
    public void setProcurementService(ProcurementService procurementService){
        this.procurementService = procurementService;
    }
    @GetMapping(Router.PROCUREMENT.LIST)
    @Operation(summary = "採購清單")
    public ResponseEntity<ApiResponse> list(ProcurementRequest request){
        return procurementService.findAll(request);
    }
    @Loggable
    @PostMapping(Router.PROCUREMENT.ADD)
    @Operation(summary = "新增採購")
    public ResponseEntity<ApiResponse> add(@RequestBody ProcurementRequest request){
        return procurementService.add(request);
    }
    @Loggable
    @PutMapping(Router.PROCUREMENT.UPDATE)
    @Operation(summary = "更新採購")
    public ResponseEntity<ApiResponse> update(@RequestBody ProcurementRequest request){
        return procurementService.update(request);
    }
    @Loggable
    @DeleteMapping(Router.PROCUREMENT.DELETE)
    @Operation(summary = "移除採購")
    public ResponseEntity<ApiResponse> delete(@Parameter(description = "採購單ID") Long id){
        return procurementService.delete(id);
    }
}
