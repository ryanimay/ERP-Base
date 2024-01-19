package com.erp.base.service;

import com.erp.base.enums.response.ApiResponseCode;
import com.erp.base.model.ClientIdentity;
import com.erp.base.model.dto.request.PageRequestParam;
import com.erp.base.model.dto.request.project.ProjectRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.dto.response.PageResponse;
import com.erp.base.model.entity.ProjectModel;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class ProjectService {
    private ProjectRepository projectRepository;
    @Autowired
    public void setProjectRepository(ProjectRepository projectRepository){
        this.projectRepository = projectRepository;
    }

    public ResponseEntity<ApiResponse> list(PageRequestParam page) {
        Page<ProjectModel> all = projectRepository.findAll(page.getPage());
        return ApiResponse.success(new PageResponse<>(all, ProjectModel.class));
    }

    public ResponseEntity<ApiResponse> add(ProjectRequest request) {
        ClientModel user = ClientIdentity.getUser();
        ProjectModel projectModel = request.toModel();
        projectModel.setCreateBy(user.getId());
        projectRepository.save(projectModel);
        return ApiResponse.success(ApiResponseCode.SUCCESS);
    }

    public ResponseEntity<ApiResponse> update(ProjectRequest request) {
        Optional<ProjectModel> byId = projectRepository.findById(request.getId());
        if(byId.isPresent()){
            ProjectModel projectModel = byId.get();
            projectModel.setId(projectModel.getId());
            projectModel.setName(request.getName());
            projectModel.setType(request.getType());
            projectModel.setScheduledStartTime(request.getScheduledStartTime());
            projectModel.setScheduledEndTime(request.getScheduledEndTime());
            projectModel.setInfo(request.getInfo());
            projectModel.setManager(new ClientModel(request.getManagerId()));
            projectRepository.save(projectModel);
            return ApiResponse.success(ApiResponseCode.SUCCESS);
        }
        return ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "UserNotFound");
    }

    public ResponseEntity<ApiResponse> start(Long projectId) {
        projectRepository.start(projectId, LocalDateTime.now());
        return ApiResponse.success(ApiResponseCode.SUCCESS);
    }

    public ResponseEntity<ApiResponse> done(Long projectId) {
        projectRepository.done(projectId, LocalDateTime.now());
        return ApiResponse.success(ApiResponseCode.SUCCESS);
    }
}
