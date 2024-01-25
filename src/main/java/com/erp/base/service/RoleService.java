package com.erp.base.service;

import com.erp.base.enums.response.ApiResponseCode;
import com.erp.base.model.dto.request.role.RolePermissionRequest;
import com.erp.base.model.dto.request.role.RoleRequest;
import com.erp.base.model.dto.request.role.RoleRouterRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.dto.response.role.RoleListResponse;
import com.erp.base.model.entity.PermissionModel;
import com.erp.base.model.entity.RoleModel;
import com.erp.base.model.entity.RouterModel;
import com.erp.base.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class RoleService {
    private RoleRepository roleRepository;
    private CacheService cacheService;

    @Autowired
    public void setRoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Autowired
    public void setCacheService(CacheService cacheService) {
        this.cacheService = cacheService;
    }


    public List<RoleModel> findAll() {
        return roleRepository.findAll();
    }

    public ResponseEntity<ApiResponse> roleNameList() {
        List<RoleListResponse> roleListResponses = cacheService.getRole().values().stream().map(RoleListResponse::new).toList();
        return ApiResponse.success(roleListResponses);
    }

    public ResponseEntity<ApiResponse> updateName(RoleRequest request) {
        Long id = request.getId();
        String name = request.getName();
        ResponseEntity<ApiResponse> response = checkRoleName(name, id);
        if (response != null) return response;

        RoleModel model = cacheService.getRole().get(id);
        if (model != null) {
            model.setRoleName(name);
            roleRepository.save(model);
            cacheService.refreshRole();
            return ApiResponse.success(ApiResponseCode.SUCCESS);
        }

        return ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR);
    }

    public ResponseEntity<ApiResponse> addRole(RoleRequest request) {
        String name = request.getName();
        ResponseEntity<ApiResponse> response = checkRoleName(name, null);
        if (response != null) return response;
        roleRepository.save(new RoleModel(name));
        cacheService.refreshRole();
        return ApiResponse.success(ApiResponseCode.SUCCESS);
    }

    private ResponseEntity<ApiResponse> checkRoleName(String name, Long id) {
        Optional<RoleModel> roleModel = roleRepository.findByRoleName(name);
        if (roleModel.isPresent() && roleModel.get().getId() != id)
            return ApiResponse.error(ApiResponseCode.NAME_ALREADY_EXIST);
        return null;
    }

    public ResponseEntity<ApiResponse> deleteById(RoleRequest request) {
        roleRepository.deleteById(request.getId());
        return ApiResponse.success(ApiResponseCode.SUCCESS);
    }

    public RoleModel findById(long roleId) {
        Optional<RoleModel> role = roleRepository.findById(roleId);
        return role.orElse(null);
    }

    public ResponseEntity<ApiResponse> updateRolePermission(RolePermissionRequest request) {
        Long id = request.getId();
        RoleModel roleModel = cacheService.getRole().get(id);
        Set<PermissionModel> permissionSet = request.getPermissionSet();
        roleModel.setPermissions(permissionSet);
        roleRepository.save(roleModel);
        return ApiResponse.success(ApiResponseCode.SUCCESS);
    }

    public ResponseEntity<ApiResponse> updateRoleRouter(RoleRouterRequest request) {
        Long id = request.getId();
        RoleModel roleModel = cacheService.getRole().get(id);
        Set<RouterModel> set = request.getRouterSet();
        roleModel.setRouters(set);
        roleRepository.save(roleModel);
        return ApiResponse.success(ApiResponseCode.SUCCESS);
    }
}
