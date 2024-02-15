package com.erp.base.controller;

import com.erp.base.model.dto.request.log.LogRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.service.LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "LogController", description = "日誌相關API")
public class LogController {
    private LogService logService;
    @Autowired
    public void setLogService(LogService logService){
        this.logService = logService;
    }

    @GetMapping(Router.LOG.LIST)
    @Operation(summary = "日誌清單")
    public ResponseEntity<ApiResponse> list(@Parameter(description = "日誌清單請求") LogRequest request){
        return logService.findAll(request);
    }
}
