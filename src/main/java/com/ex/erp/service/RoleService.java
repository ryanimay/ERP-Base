package com.ex.erp.service;

import com.ex.erp.dto.request.role.UpdateRoleRequest;
import com.ex.erp.dto.response.ApiResponse;
import com.ex.erp.dto.response.role.RoleListResponse;
import com.ex.erp.enums.response.ApiResponseCode;
import com.ex.erp.model.RoleModel;
import com.ex.erp.repository.RoleRepository;
import com.ex.erp.service.cache.ClientCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RoleService {
    private RoleRepository roleRepository;
    private ClientCache clientCache;

    @Autowired
    public void setRoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }
    @Autowired
    public void setClientCache(ClientCache clientCache) {
        this.clientCache = clientCache;
    }


    public List<RoleModel> findAll() {
        return roleRepository.findAll();
    }

    public ResponseEntity<ApiResponse> roleNameList() {
        List<RoleListResponse> roleListResponses = clientCache.getRole().values().stream().map(RoleListResponse::new).toList();
        return ApiResponse.success(roleListResponses);
    }

    public ResponseEntity<ApiResponse> updateName(UpdateRoleRequest request) {
        Long id = request.getId();
        String name = request.getName();
        Optional<RoleModel> roleModel = roleRepository.findByRoleName(name);
        if(roleModel.isPresent() && roleModel.get().getId() != id) return ApiResponse.error(ApiResponseCode.NAME_ALREADY_EXIST);

        RoleModel model = clientCache.getRole().get(id);
        if(model != null){
            model.setRoleName(name);
            roleRepository.save(model);
            clientCache.refreshRole();
            return ApiResponse.success(ApiResponseCode.SUCCESS);
        }

        return ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR);
    }
}
