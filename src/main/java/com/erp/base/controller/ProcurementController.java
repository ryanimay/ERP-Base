package com.erp.base.controller;

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

    @PostMapping(Router.PROCUREMENT.ADD)
    public ResponseEntity<ApiResponse> add(@RequestBody ProcurementRequest request){
        return procurementService.add(request);
    }

    @PutMapping(Router.PROCUREMENT.UPDATE)
    public ResponseEntity<ApiResponse> update(@RequestBody ProcurementRequest request){
        return procurementService.update(request);
    }
}
