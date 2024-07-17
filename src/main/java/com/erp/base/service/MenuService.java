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

import java.util.*;

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
        Set<MenuResponse> set = new HashSet<>();
        roleIds.forEach(id -> set.addAll(cacheService.getRoleMenu(id)));//set去重
        List<MenuResponse> menuList = new ArrayList<>(set);
        menuList.sort(new MenuComparator());//轉list排序
        Map<Long, List<MenuResponse>> menuMap = new HashMap<>();
        for (MenuResponse model : menuList) {
            handleMenu(model, menuMap);
        }//整理成樹狀返回根節點
        return ApiResponse.success(ApiResponseCode.SUCCESS, menuMap.get(0L) == null ? new ArrayList<>() : menuMap.get(0L));
    }

    public List<MenuResponse> getRoleMenu(long roleId){
        List<MenuModel> byRoleId = menuRepository.findByRoleId(roleId);
        return byRoleId.stream().map(MenuResponse::new).toList();
    }

    private static class MenuComparator implements Comparator<MenuResponse> {
        @Override
        public int compare(MenuResponse m1, MenuResponse m2) {
            // level 降序排列
            if (!Objects.equals(m1.getLevel(), m2.getLevel())) {
                return Integer.compare(m2.getLevel(), m1.getLevel()); // m2在前面，以便降序排列
            }

            // parentId 升序排列
            if (m1.getParentsId() != m2.getParentsId()) {
                return Long.compare(m1.getParentsId(), m2.getParentsId());
            }

            // orderNum 升序排列
            return Integer.compare(m1.getOrder(), m2.getOrder());
        }
    }
}
