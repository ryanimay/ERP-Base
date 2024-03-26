package com.erp.base.service;


import com.erp.base.model.constant.response.ApiResponseCode;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.dto.response.RouterConfigResponse;
import com.erp.base.model.dto.response.RouterResponse;
import com.erp.base.model.entity.RouterModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.*;

@ExtendWith(MockitoExtension.class)
class RouterServiceTest {
    @Mock
    private CacheService cacheService;
    @InjectMocks
    private RouterService routerService;
    private static final List<RouterModel> routerModels;
    private static final RouterModel r1;
    private static final RouterModel r2;
    private static final RouterModel r3;
    private static final RouterModel r4;
    static {
        routerModels = new ArrayList<>();
        RouterModel parent = new RouterModel(5L);
        parent.setName("testParents");
        r1 = new RouterModel();
        r1.setId(1L);
        r1.setName("r1");
        r1.setPath("/r1");
        r1.setComponents("r1Components");
        r1.setParent(parent);
        routerModels.add(r1);
        r2 = new RouterModel();
        r2.setId(2L);
        r2.setName("r2");
        r2.setPath("/r2");
        r2.setComponents("r2Components");
        r2.setParent(parent);
        routerModels.add(r2);
        r3 = new RouterModel();
        r3.setId(3L);
        r3.setName("r3");
        r3.setPath("/r3");
        r3.setComponents("r3Components");
        r3.setParent(parent);
        routerModels.add(r3);
        r4 = new RouterModel();
        r4.setId(4L);
        r4.setName("r4");
        r4.setPath("/r4");
        r4.setComponents("r4Components");
        r4.setParent(parent);
        routerModels.add(r4);
    }

    @Test
    @DisplayName("路由配置清單_成功")
    void configList_ok() {
        Mockito.when(cacheService.getRouters()).thenReturn(routerModels);
        ResponseEntity<ApiResponse> response = routerService.configList();
        List<RouterConfigResponse> routerConfigResponses = new ArrayList<>();
        routerConfigResponses.add(new RouterConfigResponse(r1));
        routerConfigResponses.add(new RouterConfigResponse(r2));
        routerConfigResponses.add(new RouterConfigResponse(r3));
        routerConfigResponses.add(new RouterConfigResponse(r4));
        Assertions.assertEquals(ApiResponse.success(ApiResponseCode.SUCCESS, routerConfigResponses), response);
    }

    @Test
    @DisplayName("路由展示清單_成功")
    void fullList_ok() {
        Mockito.when(cacheService.getRouters()).thenReturn(routerModels);
        ResponseEntity<ApiResponse> response = routerService.fullList();
        Map<String, List<RouterResponse>> map = new HashMap<>();
        List<RouterResponse> routerResponseList = new ArrayList<>();
        routerResponseList.add(new RouterResponse(r1));
        routerResponseList.add(new RouterResponse(r2));
        routerResponseList.add(new RouterResponse(r3));
        routerResponseList.add(new RouterResponse(r4));
        map.put("testParents", routerResponseList);
        Assertions.assertEquals(ApiResponse.success(ApiResponseCode.SUCCESS, map), response);
    }

    @Test
    @DisplayName("角色路由清單_成功")
    void getRoleRouter_ok() {
        Set<RouterResponse> rolePermission = new HashSet<>();
        rolePermission.add(new RouterResponse(r1));
        rolePermission.add(new RouterResponse(r2));
        rolePermission.add(new RouterResponse(r3));
        rolePermission.add(new RouterResponse(r4));
        Mockito.when(cacheService.getRoleRouter(Mockito.anyLong())).thenReturn(rolePermission);
        List<Long> list = new ArrayList<>();
        list.add(1L);
        list.add(2L);
        list.add(3L);
        ResponseEntity<ApiResponse> response = routerService.getRoleRouter(list);
        Set<Long> rolePermissionList = new HashSet<>();
        rolePermissionList.add(r1.getId());
        rolePermissionList.add(r2.getId());
        rolePermissionList.add(r3.getId());
        rolePermissionList.add(r4.getId());
        Assertions.assertEquals(ApiResponse.success(ApiResponseCode.SUCCESS, rolePermissionList), response);
    }

}