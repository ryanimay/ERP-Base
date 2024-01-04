package com.erp.base.controller;

import com.erp.base.dto.response.ApiResponse;
import com.erp.base.service.RouterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RouterController {
    private RouterService routerService;

    @Autowired
    public void setRouterService(RouterService routerService) {
        this.routerService = routerService;
    }

    @GetMapping(Router.ROUTER.CONFIG_LIST)
    public ResponseEntity<ApiResponse> configList() {
        return routerService.configList();
    }

    @GetMapping(Router.ROUTER.LIST)
    public ResponseEntity<ApiResponse> list() {
        return routerService.fullList();
    }

}
