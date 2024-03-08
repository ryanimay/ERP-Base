package com.erp.base.service;

import com.erp.base.model.constant.response.ApiResponseCode;
import com.erp.base.model.dto.request.permission.BanRequest;
import com.erp.base.model.dto.request.permission.SecurityConfirmRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.dto.security.RolePermissionDto;
import com.erp.base.model.entity.PermissionModel;
import com.erp.base.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
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
        ResponseEntity<ApiResponse> response;
        try {
            Set<RolePermissionDto> rolePermission = cacheService.getRolePermission(roleId);
            List<Long> rolePermissionList = rolePermission.stream().map(RolePermissionDto::getId).toList();
            response = ApiResponse.success(ApiResponseCode.SUCCESS, rolePermissionList);
        } catch (NullPointerException e) {
            response = ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "Unknown roleId: [" + roleId + "]");
        }
        return response;
    }

    public ResponseEntity<ApiResponse> getPermissionList() {
        Map<String, List<PermissionModel>> map = cacheService.getPermissionMap();
        return ApiResponse.success(ApiResponseCode.SUCCESS, map);
    }

    public ResponseEntity<ApiResponse> ban(BanRequest request) {
        permissionRepository.updateStatusById(request.getId(), request.isStatus());
        cacheService.refreshRolePermission();
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
