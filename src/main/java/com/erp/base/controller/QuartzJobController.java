package com.erp.base.controller;

import com.erp.base.enums.response.ApiResponseCode;
import com.erp.base.model.dto.request.IdRequest;
import com.erp.base.model.dto.request.quartzJob.QuartzJobRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.service.QuartzJobService;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class QuartzJobController {
    private QuartzJobService quartzJobService;
    @Autowired
    public void setQuartzJobService(QuartzJobService quartzJobService) {
        this.quartzJobService = quartzJobService;
    }

    @GetMapping(Router.QUARTZ_JOB.LIST)
    public ResponseEntity<ApiResponse> list() {
        return quartzJobService.list();
    }
    @PostMapping(Router.QUARTZ_JOB.ADD)
    public ResponseEntity<ApiResponse> add(@RequestBody QuartzJobRequest request) {
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        try {
            quartzJobService.add(request);
        } catch (ClassNotFoundException e) {
            response = ApiResponse.error(ApiResponseCode.CLASS_NOT_FOUND);
        } catch (SchedulerException e) {
            response = ApiResponse.error(ApiResponseCode.SCHEDULER_ERROR);
        }
        return response;
    }
    @PutMapping(Router.QUARTZ_JOB.UPDATE)
    public ResponseEntity<ApiResponse> update(@RequestBody QuartzJobRequest request) {
        ResponseEntity<ApiResponse> response;
        try {
            response = quartzJobService.update(request);
        } catch (ClassNotFoundException e) {
            response = ApiResponse.error(ApiResponseCode.CLASS_NOT_FOUND);
        } catch (SchedulerException e) {
            response = ApiResponse.error(ApiResponseCode.SCHEDULER_ERROR);
        }
        return response;
    }
    @PutMapping(Router.QUARTZ_JOB.TOGGLE)
    public ResponseEntity<ApiResponse> toggle(@RequestBody IdRequest request) {
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        try {
            quartzJobService.toggle(request);
        } catch (SchedulerException e) {
            response = ApiResponse.error(ApiResponseCode.SCHEDULER_ERROR);
        }
        return response;
    }
    @DeleteMapping(Router.QUARTZ_JOB.DELETE)
    public ResponseEntity<ApiResponse> delete(Long id) {
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        try {
            quartzJobService.delete(id);
        } catch (SchedulerException e) {
            response = ApiResponse.error(ApiResponseCode.SCHEDULER_ERROR);
        }
        return response;
    }
    @PostMapping(Router.QUARTZ_JOB.EXEC)
    public ResponseEntity<ApiResponse> exec(@RequestBody IdRequest request) {
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        try {
            quartzJobService.exec(request);
        } catch (SchedulerException e) {
            response = ApiResponse.error(ApiResponseCode.SCHEDULER_ERROR);
        }
        return response;
    }
}
