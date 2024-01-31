package com.erp.base.service;

import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.entity.DepartmentModel;
import com.erp.base.model.entity.RoleModel;
import com.erp.base.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class DepartmentService {
    private DepartmentRepository departmentRepository;
    private CacheService cacheService;
    @Autowired
    public void setCacheService(CacheService cacheService){
        this.cacheService = cacheService;
    }
    @Autowired
    public void setDepartmentRepository(DepartmentRepository departmentRepository){
        this.departmentRepository = departmentRepository;
    }
    /**
     * 依照部門設置部門默認權限
     * */
    public ClientModel setDefaultRole(ClientModel model, Long departmentId){
        Set<RoleModel> role = model.getRoles();
        //default 1
        if(departmentId == null) {
            role.add(new RoleModel(1L));//默認值visitor
        }else {
            DepartmentModel department = cacheService.getDepartment(departmentId);
            model.setDepartment(department);
            role.add(new RoleModel(department.getDefaultRoleId()));
        }
        return model;
    }

    public DepartmentModel findById(Long id){
        Optional<DepartmentModel> byId = departmentRepository.findById(id);
        return byId.orElse(null);
    }
}
