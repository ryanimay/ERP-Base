package com.erp.base.controller;

import com.erp.base.aspect.Loggable;
import com.erp.base.enums.response.ApiResponseCode;
import com.erp.base.model.dto.request.IdRequest;
import com.erp.base.model.dto.request.quartzJob.QuartzJobRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.service.QuartzJobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@Tag(name = "QuartzJobController", description = "排程相關API")
public class QuartzJobController {
    private QuartzJobService quartzJobService;

    @Autowired
    public void setQuartzJobService(QuartzJobService quartzJobService) {
        this.quartzJobService = quartzJobService;
    }

    @GetMapping(Router.QUARTZ_JOB.LIST)
    @Operation(summary = "排程清單")
    public ResponseEntity<ApiResponse> list() {
        return quartzJobService.list();
    }

    @Loggable
    @PostMapping(Router.QUARTZ_JOB.ADD)
    @Operation(summary = "新增排程")
    public ResponseEntity<ApiResponse> add(@RequestBody QuartzJobRequest request) {
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        try {
            quartzJobService.add(request);
        } catch (ClassNotFoundException e) {
            response = ApiResponse.errorMsgFormat(ApiResponseCode.CLASS_NOT_FOUND, request.getClassPath());
        } catch (SchedulerException | ParseException e) {
            response = ApiResponse.errorMsgFormat(ApiResponseCode.SCHEDULER_ERROR, e.getMessage());
        }
        return response;
    }

    @Loggable
    @PutMapping(Router.QUARTZ_JOB.UPDATE)
    @Operation(summary = "編輯排程")
    public ResponseEntity<ApiResponse> update(@RequestBody QuartzJobRequest request) {
        ResponseEntity<ApiResponse> response;
        try {
            response = quartzJobService.update(request);
        } catch (ClassNotFoundException e) {
            response = ApiResponse.errorMsgFormat(ApiResponseCode.CLASS_NOT_FOUND, request.getClassPath());
        } catch (SchedulerException | ParseException e) {
            response = ApiResponse.errorMsgFormat(ApiResponseCode.SCHEDULER_ERROR, e.getMessage());
        }
        return response;
    }

    @Loggable
    @PutMapping(Router.QUARTZ_JOB.TOGGLE)
    @Operation(summary = "排程狀態切換")
    public ResponseEntity<ApiResponse> toggle(@Parameter(description = "排程ID") @RequestBody IdRequest request) {
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        try {
            quartzJobService.toggle(request);
        } catch (SchedulerException e) {
            response = ApiResponse.errorMsgFormat(ApiResponseCode.SCHEDULER_ERROR, e.getMessage());
        }
        return response;
    }

    @Loggable
    @DeleteMapping(Router.QUARTZ_JOB.DELETE)
    @Operation(summary = "刪除排程")
    public ResponseEntity<ApiResponse> delete(@Parameter(description = "排程ID") Long id) {
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        try {
            quartzJobService.delete(id);
        } catch (SchedulerException e) {
            response = ApiResponse.errorMsgFormat(ApiResponseCode.SCHEDULER_ERROR, e.getMessage());
        }
        return response;
    }

    @Loggable
    @PostMapping(Router.QUARTZ_JOB.EXEC)
    @Operation(summary = "單次執行任務")
    public ResponseEntity<ApiResponse> exec(@Parameter(description = "排程ID") @RequestBody IdRequest request) {
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        try {
            quartzJobService.exec(request);
        } catch (SchedulerException e) {
            response = ApiResponse.errorMsgFormat(ApiResponseCode.SCHEDULER_ERROR, e.getMessage());
        }
        return response;
    }
}
