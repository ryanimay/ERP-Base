package com.erp.base.service;

import com.erp.base.model.constant.response.ApiResponseCode;
import com.erp.base.model.dto.request.IdRequest;
import com.erp.base.model.dto.request.role.RoleMenuRequest;
import com.erp.base.model.dto.request.role.RolePermissionRequest;
import com.erp.base.model.dto.request.role.RoleRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.dto.response.role.RoleNameResponse;
import com.erp.base.model.entity.MenuModel;
import com.erp.base.model.entity.PermissionModel;
import com.erp.base.model.entity.RoleModel;
import com.erp.base.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class RoleService {
    private RoleRepository roleRepository;
    private CacheService cacheService;
    private ClientService clientService;
    private DepartmentService departmentService;
    @Autowired
    public void setDepartmentService(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }
    @Autowired
    public void setClientService(ClientService clientService) {
        this.clientService = clientService;
    }

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
        List<RoleNameResponse> roleNameResponses = cacheService.getRole()
                .values()
                .stream()
                .sorted(Comparator.comparing(RoleModel::getId))
                .map(RoleNameResponse::new)
                .toList();
        return ApiResponse.success(roleNameResponses);
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
        RoleModel model = roleRepository.save(new RoleModel(name));
        cacheService.refreshRole();
        return ApiResponse.success(ApiResponseCode.SUCCESS, new RoleNameResponse(model));
    }

    private ResponseEntity<ApiResponse> checkRoleName(String name, Long id) {
        Optional<RoleModel> roleModel = roleRepository.findByRoleName(name);
        if (roleModel.isPresent() && (id == null || roleModel.get().getId() != id))
            return ApiResponse.error(ApiResponseCode.NAME_ALREADY_EXIST);
        return null;
    }

    public ResponseEntity<ApiResponse> deleteById(IdRequest request) {
        Long id = request.getId();
        boolean exists = clientService.checkExistsRoleId(id);
        if(exists) return ApiResponse.error(ApiResponseCode.ROLE_IN_USE);
        //確定角色沒人使用，先把部門角色關聯解除
        departmentService.removeRole(id);
        roleRepository.deleteById(id);
        cacheService.refreshRole();
        return ApiResponse.success(ApiResponseCode.SUCCESS);
    }

    public RoleModel findById(long roleId) {
        Optional<RoleModel> role = roleRepository.findById(roleId);
        return role.orElse(null);
    }

    public ResponseEntity<ApiResponse> updateRolePermission(RolePermissionRequest request) {
        Long id = request.getId();
        RoleModel roleModel = cacheService.getRole().get(id);
        if(roleModel == null) return ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR);
        Set<PermissionModel> permissionSet = request.getPermissionSet();
        roleModel.setPermissions(permissionSet);
        roleRepository.save(roleModel);
        cacheService.refreshRolePermission();
        return ApiResponse.success(ApiResponseCode.SUCCESS);
    }

    public ResponseEntity<ApiResponse> updateRoleMenu(RoleMenuRequest request) {
        Long id = request.getId();
        RoleModel roleModel = cacheService.getRole().get(id);
        if(roleModel == null) return ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR);
        Set<MenuModel> set = request.getMenuSet();
        roleModel.setMenus(set);
        roleRepository.save(roleModel);
        return ApiResponse.success(ApiResponseCode.SUCCESS);
    }
}
