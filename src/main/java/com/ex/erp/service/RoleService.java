package com.ex.erp.service;

import com.ex.erp.model.RoleModel;
import com.ex.erp.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }


    public List<RoleModel> findAll() {
        return roleRepository.findAll();
    }
}
