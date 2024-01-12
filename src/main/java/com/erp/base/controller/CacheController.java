package com.erp.base.controller;

import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.enums.response.ApiResponseCode;
import com.erp.base.service.CacheService;
import com.erp.base.tool.LogFactory;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CacheController {
    LogFactory LOG = new LogFactory(CacheController.class);
    private CacheService cacheService;
    @Autowired
    public void setCacheService(CacheService cacheService){
        this.cacheService = cacheService;
    }

    @GetMapping(Router.CACHE.REFRESH)
    public ResponseEntity<ApiResponse> refresh(String cacheKey){
        if(StringUtils.isNotEmpty(cacheKey)){

            LOG.info("refresh cacheName:" + cacheKey);
        }else {
            cacheService.refreshAllCache();
        }
        return ApiResponse.success(ApiResponseCode.REFRESH_CACHE_SUCCESS);
    }
}
