package com.erp.base.controller;

import com.erp.base.model.dto.request.PageRequestParam;
import com.erp.base.model.dto.request.project.ProjectRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ProjectController {
    private ProjectService projectService;
    @Autowired
    public void setProjectService(ProjectService projectService){
        this.projectService = projectService;
    }

    @GetMapping(Router.PROJECT.LIST)
    public ResponseEntity<ApiResponse> list(PageRequestParam page){
        return projectService.list(page);
    }

    @PostMapping(Router.PROJECT.ADD)
    public ResponseEntity<ApiResponse> add(@RequestBody ProjectRequest request){
        return projectService.add(request);
    }

    @PutMapping(Router.PROJECT.UPDATE)
    public ResponseEntity<ApiResponse> update(@RequestBody ProjectRequest request){
        return projectService.update(request);
    }
}
