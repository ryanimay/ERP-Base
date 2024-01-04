package com.erp.base.service;

import com.erp.base.dto.request.permission.BanRequest;
import com.erp.base.dto.request.permission.PermissionTreeResponse;
import com.erp.base.dto.request.permission.SecurityConfirmRequest;
import com.erp.base.dto.response.ApiResponse;
import com.erp.base.dto.security.RolePermissionDto;
import com.erp.base.enums.response.ApiResponseCode;
import com.erp.base.model.PermissionModel;
import com.erp.base.repository.PermissionRepository;
import com.erp.base.service.cache.ClientCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Transactional
public class PermissionService {
    @Value("${security.password}")
    private String securityPassword;
    private PermissionRepository permissionRepository;
    private ClientCache clientCache;
    private RoleService roleService;
    @Autowired
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }
    @Autowired
    public void setClientCache(ClientCache clientCache) {
        this.clientCache = clientCache;
    }
    @Autowired
    public void setPermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }


    public List<PermissionModel> findAll() {
        return permissionRepository.findAll();
    }

    public ResponseEntity<ApiResponse> getRolePermission(long roleId) {
        PermissionTreeResponse permissionTree = clientCache.getPermissionTree();
        Map<String, Object> map = new HashMap<>();
        map.put("tree", permissionTree);

        Set<RolePermissionDto> rolePermission = clientCache.getRolePermission(roleService.findById(roleId));
        List<Long> rolePermissionList = rolePermission.stream().map(RolePermissionDto::getId).toList();
        map.put("rolePermissions", rolePermissionList);
        return ApiResponse.success(ApiResponseCode.SUCCESS, map);
    }

    //從緩存拿
    public ResponseEntity<ApiResponse> getPermissionTreeCache() {
        PermissionTreeResponse permissionTree = clientCache.getPermissionTree();
        return ApiResponse.success(ApiResponseCode.SUCCESS, permissionTree);
    }

    public ResponseEntity<ApiResponse> ban(BanRequest request) {
        permissionRepository.updateStatusById(request.getId(), request.isStatus());
        clientCache.refreshPermission();
        return ApiResponse.success(ApiResponseCode.SUCCESS);
    }

    public ResponseEntity<ApiResponse> securityConfirm(SecurityConfirmRequest request) {
        if(securityPassword.equals(request.getSecurityPassword())) return ApiResponse.success(ApiResponseCode.SUCCESS, true);
        return ApiResponse.error(ApiResponseCode.SECURITY_ERROR, false);
    }

    public ApiResponseCode checkPermissionIfDeny(String requestedUrl) {
        Boolean result = permissionRepository.checkPermissionIfDeny(requestedUrl);
        if(!result) return ApiResponseCode.ACCESS_DENIED;
        return null;
    }
}
