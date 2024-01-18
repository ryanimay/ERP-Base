package com.erp.base.service;

import com.erp.base.model.dto.request.PageRequestParam;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.dto.response.PageResponse;
import com.erp.base.model.entity.ProjectModel;
import com.erp.base.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
