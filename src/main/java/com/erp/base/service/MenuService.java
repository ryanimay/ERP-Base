package com.erp.base.service;

import com.erp.base.model.constant.response.ApiResponseCode;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.dto.response.MenuResponse;
import com.erp.base.model.entity.MenuModel;
import com.erp.base.repository.MenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class MenuService {
    private MenuRepository menuRepository;
    private CacheService cacheService;

    @Autowired
    public void setClientCache(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @Autowired
    public void setMenuRepository(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    public ResponseEntity<ApiResponse> findAll() {
        List<MenuResponse> menus = cacheService.findMenuTree();
        return ApiResponse.success(ApiResponseCode.SUCCESS, menus);
    }

    public List<MenuResponse> findAllTree() {
        //找出的順序是由最末子節點開始
        List<MenuModel> all = menuRepository.findAllOrderByParentsAndOrderNum();
        Map<Long, List<MenuResponse>> menuMap = new HashMap<>();
        for (MenuModel model : all) {
            handleMenu(new MenuResponse(model), menuMap);
        }
        return menuMap.get(0L);
    }

    //map中就是待處理的model，最後剩下的就是根
    private void handleMenu(MenuResponse model, Map<Long, List<MenuResponse>> menuMap) {
        long id = model.getId();
        if (menuMap.containsKey(id)) {
            model.setChild(menuMap.get(id));
            menuMap.remove(id);
        }
        menuMap.computeIfAbsent(model.getParentsId(), k -> new ArrayList<>()).add(model);
    }

    public ResponseEntity<ApiResponse> pMenu(List<Long> roleIds) {
        List<MenuModel> byRoleId = menuRepository.findByRoleId(roleIds);
        Map<Long, List<MenuResponse>> menuMap = new HashMap<>();
        for (MenuModel model : byRoleId) {
            handleMenu(new MenuResponse(model), menuMap);
        }
        return ApiResponse.success(ApiResponseCode.SUCCESS, menuMap.get(0L));
    }
}
