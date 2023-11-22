package com.ex.erp.service;

import com.ex.erp.model.PermissionModel;
import com.ex.erp.model.RoleModel;
import com.ex.erp.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional
public class RoleService {
    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }


    public List<RoleModel> findAll() {
        return roleRepository.findAll();
    }

    public Set<PermissionModel> getPermissionsByRoleId(Long roleId){
        return roleRepository.getPermissionsByRoleId(roleId);
    }
}
