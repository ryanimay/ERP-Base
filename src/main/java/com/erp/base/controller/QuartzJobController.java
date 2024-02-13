package com.erp.base.controller;

import com.erp.base.model.dto.request.IdRequest;
import com.erp.base.model.dto.request.quartzJob.QuartzJobRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.service.QuartzJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class QuartzJobController {
    private QuartzJobService quartzJobService;
    @Autowired
    public void setQuartzJobService(QuartzJobService quartzJobService){
        this.quartzJobService = quartzJobService;
    }

    @GetMapping(Router.QUARTZ_JOB.LIST)
    public ResponseEntity<ApiResponse> list(){
        return quartzJobService.list();
    }
    @PostMapping(Router.QUARTZ_JOB.ADD)
    public ResponseEntity<ApiResponse> add(@RequestBody QuartzJobRequest request){
        return quartzJobService.add(request);
    }
    @PutMapping(Router.QUARTZ_JOB.UPDATE)
    public ResponseEntity<ApiResponse> update(@RequestBody QuartzJobRequest request){
        return quartzJobService.update(request);
    }
    @PutMapping(Router.QUARTZ_JOB.TOGGLE)
    public ResponseEntity<ApiResponse> toggle(@RequestBody IdRequest request){
        return quartzJobService.toggle(request);
    }
    @DeleteMapping(Router.QUARTZ_JOB.DELETE)
    public ResponseEntity<ApiResponse> delete(Long id){
        return quartzJobService.delete(id);
    }
    @PostMapping(Router.QUARTZ_JOB.EXEC)
    public ResponseEntity<ApiResponse> exec(@RequestBody IdRequest request){
        return quartzJobService.exec(request);
    }
}
