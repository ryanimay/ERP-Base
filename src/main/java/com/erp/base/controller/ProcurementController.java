package com.erp.base.controller;

import com.erp.base.aspect.Loggable;
import com.erp.base.model.dto.request.procurement.ProcurementRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.service.ProcurementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ProcurementController {
    private ProcurementService procurementService;
    @Autowired
    public void setProcurementService(ProcurementService procurementService){
        this.procurementService = procurementService;
    }
    @GetMapping(Router.PROCUREMENT.LIST)
    public ResponseEntity<ApiResponse> list(ProcurementRequest request){
        return procurementService.findAll(request);
    }
    @Loggable
    @PostMapping(Router.PROCUREMENT.ADD)
    public ResponseEntity<ApiResponse> add(@RequestBody ProcurementRequest request){
        return procurementService.add(request);
    }
    @Loggable
    @PutMapping(Router.PROCUREMENT.UPDATE)
    public ResponseEntity<ApiResponse> update(@RequestBody ProcurementRequest request){
        return procurementService.update(request);
    }
    @Loggable
    @DeleteMapping(Router.PROCUREMENT.DELETE)
    public ResponseEntity<ApiResponse> delete(Long id){
        return procurementService.delete(id);
    }
}
