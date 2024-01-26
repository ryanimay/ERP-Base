package com.erp.base.controller;

import com.erp.base.model.dto.request.job.JobRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class JobController {
    private JobService jobService;
    @Autowired
    public void setJobService(JobService jobService){
        this.jobService = jobService;
    }
    @GetMapping(Router.JOB.LIST)
    public ResponseEntity<ApiResponse> list(){
        return jobService.findAll();
    }

    @PostMapping(Router.JOB.ADD)
    public ResponseEntity<ApiResponse> add(@RequestBody JobRequest request){
        return jobService.add(request);
    }

    @PutMapping(Router.JOB.UPDATE)
    public ResponseEntity<ApiResponse> update(@RequestBody JobRequest request){
        return jobService.update(request);
    }

    @DeleteMapping(Router.JOB.REMOVE)
    public ResponseEntity<ApiResponse> remove(Long id){
        return jobService.deleteById(id);
    }

}
