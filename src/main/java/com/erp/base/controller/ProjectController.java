package com.erp.base.controller;

import com.erp.base.model.dto.request.IdRequest;
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
    public ResponseEntity<ApiResponse> list(ProjectRequest request){
        return projectService.list(request);
    }

    @PostMapping(Router.PROJECT.ADD)
    public ResponseEntity<ApiResponse> add(@RequestBody ProjectRequest request){
        return projectService.add(request);
    }

    @PutMapping(Router.PROJECT.UPDATE)
    public ResponseEntity<ApiResponse> update(@RequestBody ProjectRequest request){
        return projectService.update(request);
    }

    @PutMapping(Router.PROJECT.START)
    public ResponseEntity<ApiResponse> start(@RequestBody IdRequest request){
        return projectService.start(request.getId());
    }

    @PutMapping(Router.PROJECT.DONE)
    public ResponseEntity<ApiResponse> done(@RequestBody IdRequest request){
        return projectService.done(request.getId());
    }
}
