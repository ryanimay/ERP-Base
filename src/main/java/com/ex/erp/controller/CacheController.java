package com.ex.erp.controller;

import com.ex.erp.dto.response.ApiResponse;
import com.ex.erp.service.CacheService;
import com.ex.erp.tool.LogFactory;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cache")
public class CacheController {
    LogFactory LOG = new LogFactory(CacheController.class);
    private CacheService cacheService;
    @Autowired
    public void setCacheService(CacheService cacheService){
        this.cacheService = cacheService;
    }

    @GetMapping("/refresh")
    public ResponseEntity<ApiResponse<Object>> refresh(String cacheKey){
        if(StringUtils.isNotEmpty(cacheKey)){

            LOG.info("refresh cacheName:" + cacheKey);
        }else {
            cacheService.refreshAllCache();
        }
        return ApiResponse.success("refresh cache success");
    }
}
