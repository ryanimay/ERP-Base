package com.erp.base.service;

import com.erp.base.model.constant.cache.CacheConstant;
import com.erp.base.model.constant.response.ApiResponseCode;
import com.erp.base.model.dto.request.permission.BanRequest;
import com.erp.base.model.dto.request.permission.SecurityConfirmRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.dto.response.role.PermissionListResponse;
import com.erp.base.model.dto.security.RolePermissionDto;
import com.erp.base.model.entity.PermissionModel;
import com.erp.base.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional
public class PermissionService {
    @Value("${security.password}")
    private String securityPassword;
    private PermissionRepository permissionRepository;
    private CacheService cacheService;

    @Autowired
    public void setCacheService(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @Autowired
    public void setPermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }


    public List<PermissionModel> findAll() {
        return permissionRepository.findAll();
    }

    public ResponseEntity<ApiResponse> getRolePermission(long roleId) {
        Set<RolePermissionDto> rolePermission = cacheService.getRolePermission(roleId);
        if(rolePermission == null) return ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR);
        List<Long> rolePermissionList = rolePermission.stream().map(RolePermissionDto::getId).toList();
        return ApiResponse.success(ApiResponseCode.SUCCESS, rolePermissionList);
    }

    public ResponseEntity<ApiResponse> getPermissionList() {

        List<PermissionListResponse> map = cacheService.getPermissionMap();
        return ApiResponse.success(ApiResponseCode.SUCCESS, map);
    }

    public ResponseEntity<ApiResponse> ban(BanRequest request) {
        permissionRepository.updateStatusById(request.getId(), request.isStatus());
        cacheService.refreshCache(CacheConstant.ROLE_PERMISSION.NAME_ROLE_PERMISSION);
        return ApiResponse.success(ApiResponseCode.SUCCESS);
    }

    public ResponseEntity<ApiResponse> securityConfirm(SecurityConfirmRequest request) {
        if (securityPassword.equals(request.getSecurityPassword()))
            return ApiResponse.success(ApiResponseCode.SUCCESS, true);
        return ApiResponse.error(ApiResponseCode.SECURITY_ERROR, false);
    }

    public Boolean checkPermissionIfDeny(String requestedUrl) {
        return permissionRepository.checkPermissionIfDeny(requestedUrl);
    }
}
