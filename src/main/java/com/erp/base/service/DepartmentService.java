package com.erp.base.service;

import com.erp.base.model.constant.response.ApiResponseCode;
import com.erp.base.model.dto.request.department.DepartmentEditRequest;
import com.erp.base.model.dto.request.department.DepartmentRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.dto.response.ClientNameRoleObject;
import com.erp.base.model.dto.response.DepartmentResponse;
import com.erp.base.model.dto.response.PageResponse;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.entity.DepartmentModel;
import com.erp.base.model.entity.RoleModel;
import com.erp.base.repository.DepartmentRepository;
import com.erp.base.tool.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class DepartmentService {
    LogFactory LOG = new LogFactory(DepartmentService.class);
    private DepartmentRepository departmentRepository;
    private CacheService cacheService;

    @Autowired
    public void setCacheService(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @Autowired
    public void setDepartmentRepository(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    /**
     * 依照部門設置部門默認權限
     */
    public ClientModel setDepartmentDefaultRole(ClientModel model, Long departmentId) {
        Set<RoleModel> role = new HashSet<>(model.getRoles());
        //default 1
        if (departmentId == null) {
            role.add(new RoleModel(1L));//默認值visitor
        } else {
            DepartmentModel department = cacheService.getDepartment(departmentId);
            model.setDepartment(department);
            long roleId = department.getDefaultRole().getId();
            //要新建構role不然相關屬性會在.add時懶加載出錯
            role.add(new RoleModel(roleId));
        }
        model.setRoles(role);
        return model;
    }

    public DepartmentModel findById(Long id) {
        Optional<DepartmentModel> byId = departmentRepository.findById(id);
        return byId.orElse(null);
    }

    public ResponseEntity<ApiResponse> list(DepartmentRequest request) {
        Page<DepartmentModel> all = departmentRepository.findAll(request.getSpecification(), request.getPage());
        return ApiResponse.success(new PageResponse<>(all, DepartmentResponse.class));
    }

    public ResponseEntity<ApiResponse> findStaffById(Long id) {
        DepartmentModel model = findById(id);
        if (model == null) {
            LOG.warn("DepartmentId Not Found.");
            return ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "DepartmentId Not Found.");
        }
        List<ClientModel> modelList = model.getClientModelList();
        return ApiResponse.success(transToDto(modelList));
    }

    private List<ClientNameRoleObject> transToDto(List<ClientModel> clientList) {
        return clientList.stream().map(ClientNameRoleObject::new).toList();
    }

    public ResponseEntity<ApiResponse> edit(DepartmentEditRequest request) {
        departmentRepository.save(request.toModel());
        cacheService.refreshRolePermission();
        return ApiResponse.success(ApiResponseCode.SUCCESS);
    }

    public ResponseEntity<ApiResponse> remove(long id) {
        DepartmentModel model = findById(id);
        if (model != null && (model.getClientModelList() == null || model.getClientModelList().isEmpty())) {
            departmentRepository.deleteById(id);
            cacheService.refreshRolePermission();
            return ApiResponse.success(ApiResponseCode.SUCCESS);
        }
        return ApiResponse.error(ApiResponseCode.DEPARTMENT_IN_USE);
    }
}
