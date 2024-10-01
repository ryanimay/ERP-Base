package com.erp.base.service.cache;

import com.erp.base.model.constant.cache.CacheConstant;
import com.erp.base.service.DepartmentService;
import com.erp.base.service.ProcurementService;
import com.erp.base.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * 其他緩存
 */
@Service
@CacheConfig(cacheNames = CacheConstant.OTHER.OTHER)
public class OtherCache {
    private DepartmentService departmentService;
    private ProjectService projectService;
    private ProcurementService procureService;
    @Autowired
    public void setDepartmentService(@Lazy DepartmentService departmentService) {
        this.departmentService = departmentService;
    }
    @Autowired
    public void setProjectService(@Lazy ProjectService projectService) {
        this.projectService = projectService;
    }
    @Autowired
    public void setProcureService(@Lazy ProcurementService procureService) {
        this.procureService = procureService;
    }

    @Cacheable(key = "'" +CacheConstant.OTHER.SYSTEM_DEPARTMENT + "'")
    public String getSystemDepartment() {
        return departmentService.getSystemDepartment();
    }

    @Cacheable(key = "'" +CacheConstant.OTHER.SYSTEM_PROJECT + "'")
    public String getSystemProject() {
        return projectService.getSystemProject();
    }

    @Cacheable(key = "'" +CacheConstant.OTHER.SYSTEM_PROCURE + "'")
    public Object[] getSystemProcure() {
        return procureService.getSystemProcure();
    }
}
