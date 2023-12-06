package com.ex.erp.service;

import com.ex.erp.dto.response.ApiResponse;
import com.ex.erp.dto.response.role.RoleListResponse;
import com.ex.erp.model.RoleModel;
import com.ex.erp.repository.RoleRepository;
import com.ex.erp.service.cache.ClientCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        List<RoleListResponse> roleListResponses = clientCache.getRole().stream().map(RoleListResponse::new).toList();
        return ApiResponse.success(roleListResponses);
    }
}
