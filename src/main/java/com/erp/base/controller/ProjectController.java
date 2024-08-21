package com.erp.base.controller;

import com.erp.base.aspect.Loggable;
import com.erp.base.model.dto.request.IdRequest;
import com.erp.base.model.dto.request.OrderRequest;
import com.erp.base.model.dto.request.project.ProjectRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "ProjectController", description = "專案管理相關API")
public class ProjectController {
    private ProjectService projectService;
    @Autowired
    public void setProjectService(ProjectService projectService){
        this.projectService = projectService;
    }

    @GetMapping(Router.PROJECT.LIST)
    @Operation(summary = "專案清單")
    public ResponseEntity<ApiResponse> list(ProjectRequest request){
        return projectService.list(request);
    }
    @Loggable
    @PostMapping(Router.PROJECT.ADD)
    @Operation(summary = "新增專案")
    public ResponseEntity<ApiResponse> add(@RequestBody ProjectRequest request){
        return projectService.add(request);
    }
    @Loggable
    @PutMapping(Router.PROJECT.UPDATE)
    @Operation(summary = "編輯專案")
    public ResponseEntity<ApiResponse> update(@RequestBody ProjectRequest request){
        return projectService.update(request);
    }
    @Loggable
    @PutMapping(Router.PROJECT.START)
    @Operation(summary = "專案狀態(啟動)")
    public ResponseEntity<ApiResponse> start(@Parameter(description = "專案ID") @RequestBody IdRequest request){
        return projectService.start(request.getId());
    }
    @Loggable
    @PutMapping(Router.PROJECT.DONE)
    @Operation(summary = "專案狀態(結案)")
    public ResponseEntity<ApiResponse> done(@Parameter(description = "專案ID") @RequestBody IdRequest request){
        return projectService.done(request.getId());
    }
    @Loggable
    @PutMapping(Router.PROJECT.ORDER)
    @Operation(summary = "控制專案排序")
    public ResponseEntity<ApiResponse> order(@Parameter(description = "排序物件清單") @RequestBody List<OrderRequest> orders){
        return projectService.order(orders);
    }
}
