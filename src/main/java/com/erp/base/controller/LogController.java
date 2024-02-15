package com.erp.base.controller;

import com.erp.base.model.dto.request.log.LogRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LogController {
    private LogService logService;
    @Autowired
    public void setLogService(LogService logService){
        this.logService = logService;
    }

    @GetMapping(Router.LOG.LIST)
    public ResponseEntity<ApiResponse> list(LogRequest request){
        return logService.findAll(request);
    }
}
