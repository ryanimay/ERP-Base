package com.erp.base.controller;

import com.erp.base.model.dto.request.job.JobRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "JobController", description = "任務卡相關API")
public class JobController {
    private JobService jobService;
    @Autowired
    public void setJobService(JobService jobService){
        this.jobService = jobService;
    }
    @GetMapping(Router.JOB.LIST)
    @Operation(summary = "任務卡清單")
    public ResponseEntity<ApiResponse> list(){
        return jobService.findAll();
    }

    @PostMapping(Router.JOB.ADD)
    @Operation(summary = "新增任務卡")
    public ResponseEntity<ApiResponse> add(@RequestBody JobRequest request){
        return jobService.add(request);
    }

    @PutMapping(Router.JOB.UPDATE)
    @Operation(summary = "編輯任務卡")
    public ResponseEntity<ApiResponse> update(@RequestBody JobRequest request){
        return jobService.update(request);
    }

    @DeleteMapping(Router.JOB.REMOVE)
    @Operation(summary = "移除任務卡")
    public ResponseEntity<ApiResponse> remove(@Parameter(description = "任務卡ID") Long id){
        return jobService.deleteById(id);
    }

}
