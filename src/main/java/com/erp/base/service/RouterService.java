package com.erp.base.service;

import com.erp.base.model.constant.response.ApiResponseCode;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.dto.response.RouterConfigResponse;
import com.erp.base.model.dto.response.RouterResponse;
import com.erp.base.model.entity.RouterModel;
import com.erp.base.repository.RouterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class RouterService {
    private RouterRepository routerRepository;
    private CacheService cacheService;

    @Autowired
    public void setClientCache(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @Autowired
    public void setRouterRepository(RouterRepository routerRepository) {
        this.routerRepository = routerRepository;
    }

    public List<RouterModel> findAll() {
        return routerRepository.findAll();
    }

    public ResponseEntity<ApiResponse> configList() {
        List<RouterModel> routers = cacheService.getRouters();
        List<RouterConfigResponse> routerConfigResponses =
                routers
                        .stream()
                        .filter(routerModel -> routerModel.getParent() != null)//不管分層
                        .map(RouterConfigResponse::new)
                        .toList();
        return ApiResponse.success(ApiResponseCode.SUCCESS, routerConfigResponses);
    }

    public ResponseEntity<ApiResponse> fullList() {
        List<RouterModel> routers = cacheService.getRouters();
        Map<String, List<RouterResponse>> map = new HashMap<>();
        for (RouterModel router : routers) {
            RouterModel parent = router.getParent();
            if (parent != null) {
                List<RouterResponse> list = map.computeIfAbsent(parent.getName(), k -> new ArrayList<>());
                list.add(new RouterResponse(router));
            }
        }
        return ApiResponse.success(ApiResponseCode.SUCCESS, map);
    }

    //返回routerIds
    public ResponseEntity<ApiResponse> getRoleRouter(long roleId) {
        Set<RouterModel> rolePermission = cacheService.getRoleRouter(roleId);
        List<Long> rolePermissionList = rolePermission.stream().map(RouterModel::getId).toList();
        return ApiResponse.success(ApiResponseCode.SUCCESS, rolePermissionList);
    }
}
