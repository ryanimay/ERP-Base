package com.erp.base.service;


import com.erp.base.model.constant.response.ApiResponseCode;
import com.erp.base.model.dto.request.permission.SecurityConfirmRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.dto.security.RolePermissionDto;
import com.erp.base.model.entity.PermissionModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class PermissionServiceTest {
    @Mock
    private CacheService cacheService;
    @InjectMocks
    private PermissionService permissionService;

    @Test
    @DisplayName("角色權限ID清單_成功")
    void getRolePermission_ok() {
        Set<RolePermissionDto> set = new HashSet<>();
        PermissionModel model = new PermissionModel();
        model.setId(1L);
        model.setInfo("test");
        set.add(new RolePermissionDto(model));
        Mockito.when(cacheService.getRolePermission(Mockito.anyLong())).thenReturn(set);
        ResponseEntity<ApiResponse> rolePermission = permissionService.getRolePermission(1L);
        List<Long> list = new ArrayList<>();
        list.add(model.getId());
        Assertions.assertEquals(ApiResponse.success(ApiResponseCode.SUCCESS, list), rolePermission);
    }

    @Test
    @DisplayName("安全認證_成功")
    void securityConfirm_ok() {
        ReflectionTestUtils.setField(permissionService, "securityPassword", "12345");
        SecurityConfirmRequest request = new SecurityConfirmRequest();
        request.setSecurityPassword("12345");
        ResponseEntity<ApiResponse> response = permissionService.securityConfirm(request);
        Assertions.assertEquals(ApiResponse.success(ApiResponseCode.SUCCESS, true), response);
    }

    @Test
    @DisplayName("安全認證_失敗")
    void securityConfirm_error() {
        ReflectionTestUtils.setField(permissionService, "securityPassword", "123123");
        SecurityConfirmRequest request = new SecurityConfirmRequest();
        request.setSecurityPassword("12345");
        ResponseEntity<ApiResponse> response = permissionService.securityConfirm(request);
        Assertions.assertEquals(ApiResponse.error(ApiResponseCode.SECURITY_ERROR, false), response);
    }
}