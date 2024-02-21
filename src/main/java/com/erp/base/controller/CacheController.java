package com.erp.base.controller;

import com.erp.base.aspect.Loggable;
import com.erp.base.enums.response.ApiResponseCode;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.service.CacheService;
import com.erp.base.tool.LogFactory;
import io.micrometer.common.util.StringUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "CacheController", description = "緩存相關API")
public class CacheController {
    LogFactory LOG = new LogFactory(CacheController.class);
    private CacheService cacheService;

    @Autowired
    public void setCacheService(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @Loggable
    @GetMapping(Router.CACHE.REFRESH)
    @Operation(summary = "刷新緩存")
    public ResponseEntity<ApiResponse> refresh(@Parameter(description = "緩存key") String cacheKey) {
        if (StringUtils.isNotEmpty(cacheKey)) {
            cacheService.refreshCache(cacheKey);
            LOG.info("refresh cacheName:" + cacheKey);
        } else {
            cacheService.refreshAllCache();
        }
        return ApiResponse.success(ApiResponseCode.REFRESH_CACHE_SUCCESS);
    }
}
