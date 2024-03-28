package com.erp.base.controller;

import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.service.MenuService;
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
@Tag(name = "MenuController", description = "頁面權限相關API")
public class MenuController {
    private MenuService menuService;

    @Autowired
    public void setMenuService(MenuService menuService) {
        this.menuService = menuService;
    }

    //設置菜單權限展示用
    @GetMapping(Router.MENU.ALL)
    @Operation(summary = "全部菜單")
    public ResponseEntity<ApiResponse> findAll() {
        return menuService.findAll();
    }

    @GetMapping(Router.MENU.P_MENU)
    @Operation(summary = "用戶菜單")
    public ResponseEntity<ApiResponse> pMenu(@Parameter(description = "角色Ids") @RequestParam List<Long> roleIds){
        return menuService.pMenu(roleIds);
    }
}
