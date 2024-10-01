package com.erp.base.service;

import com.erp.base.model.ClientIdentity;
import com.erp.base.model.constant.StatusConstant;
import com.erp.base.model.constant.cache.CacheConstant;
import com.erp.base.model.constant.response.ApiResponseCode;
import com.erp.base.model.dto.request.OrderRequest;
import com.erp.base.model.dto.request.project.ProjectRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.dto.response.ProjectResponse;
import com.erp.base.model.dto.security.ClientIdentityDto;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.entity.ProjectModel;
import com.erp.base.repository.ProjectRepository;
import com.erp.base.tool.DateTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class ProjectService {
    private ProjectRepository projectRepository;
    private CacheService cacheService;

    @Autowired
    public void setCacheService(CacheService cacheService) {
        this.cacheService = cacheService;
    }
    @Autowired
    public void setProjectRepository(ProjectRepository projectRepository){
        this.projectRepository = projectRepository;
    }

    public ResponseEntity<ApiResponse> list(ProjectRequest request) {
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(Sort.Order.asc("orderNum").nullsLast());
        orders.add(Sort.Order.asc("id"));
        List<ProjectModel> all = projectRepository.findAll(request.getSpecification(), Sort.by(orders));
        return ApiResponse.success(all.stream().map(ProjectResponse::new).toList());
    }

    public ResponseEntity<ApiResponse> add(ProjectRequest request) {
        ClientIdentityDto user = ClientIdentity.getUser();
        ProjectModel projectModel = request.toModel();
        projectModel.setCreateBy(new ClientModel(Objects.requireNonNull(user).getId()));
        projectRepository.save(projectModel);
        //更新系統專案參數
        cacheService.refreshCache(CacheConstant.OTHER.OTHER + CacheConstant.SPLIT_CONSTANT + CacheConstant.OTHER.SYSTEM_PROJECT);
        return ApiResponse.success(ApiResponseCode.SUCCESS);
    }

    public ResponseEntity<ApiResponse> update(ProjectRequest request) {
        Optional<ProjectModel> byId = projectRepository.findById(request.getId());
        if(byId.isPresent()){
            ProjectModel projectModel = byId.get();
            if(request.getName() != null) projectModel.setName(request.getName());
            if(request.getType() != null) projectModel.setType(request.getType());
            if(request.getScheduledStartTime() != null) projectModel.setScheduledStartTime(request.getScheduledStartTime());
            if(request.getScheduledEndTime() != null) projectModel.setScheduledEndTime(request.getScheduledEndTime());
            if(request.getInfo() != null) projectModel.setInfo(request.getInfo());
            if(request.getManagerId() != null) projectModel.setManager(new ClientModel(request.getManagerId()));
            if(request.getMarkColor() != null) projectModel.setMarkColor(request.getMarkColor());
            projectRepository.save(projectModel);
            return ApiResponse.success(ApiResponseCode.SUCCESS);
        }
        return ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR);
    }

    public ResponseEntity<ApiResponse> start(Long projectId) {
        int count = projectRepository.start(projectId, DateTool.now(), StatusConstant.PENDING_NO, StatusConstant.APPROVED_NO);
        return count == 1
                ? ApiResponse.success(ApiResponseCode.SUCCESS)
                : ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "UPDATE FAILED, ID[" + projectId + "]");
    }

    public ResponseEntity<ApiResponse> done(Long projectId) {
        int count = projectRepository.done(projectId, DateTool.now(), StatusConstant.APPROVED_NO, StatusConstant.CLOSED_NO);
        return count == 1
                ? ApiResponse.success(ApiResponseCode.SUCCESS)
                : ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "UPDATE FAILED, ID[" + projectId + "]");
    }

    public ResponseEntity<ApiResponse> order(List<OrderRequest> orders) {
        orders.forEach(order -> projectRepository.updateOrder(order.getId(), order.getOrder()));
        return ApiResponse.success(ApiResponseCode.SUCCESS);
    }

    public String getSystemProject() {
        return String.valueOf(projectRepository.count());
    }
}
