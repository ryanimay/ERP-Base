package com.erp.base.controller;

import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.service.RouterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "RouterController", description = "頁面權限相關API")
public class RouterController {
    private RouterService routerService;

    @Autowired
    public void setRouterService(RouterService routerService) {
        this.routerService = routerService;
    }

    @GetMapping(Router.ROUTER.CONFIG_LIST)
    @Operation(summary = "設置用頁面權限清單")
    public ResponseEntity<ApiResponse> configList() {
        return routerService.configList();
    }

    @GetMapping(Router.ROUTER.LIST)
    @Operation(summary = "頁面權限清單")
    public ResponseEntity<ApiResponse> list() {
        return routerService.fullList();
    }

    @GetMapping(Router.ROUTER.ROLE)
    @Operation(summary = "角色頁面權限")
    public ResponseEntity<ApiResponse> roleRouter(@Parameter(description = "角色ID") @RequestParam List<Long> roleIds){
        return routerService.getRoleRouter(roleIds);
    }
}
