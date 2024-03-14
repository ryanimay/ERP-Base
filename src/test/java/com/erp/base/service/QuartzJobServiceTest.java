package com.erp.base.service;


import com.erp.base.model.constant.response.ApiResponseCode;
import com.erp.base.model.dto.request.IdRequest;
import com.erp.base.model.dto.request.quartzJob.QuartzJobRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.dto.response.QuartzJobResponse;
import com.erp.base.model.entity.QuartzJobModel;
import com.erp.base.repository.QuartzJobRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class QuartzJobServiceTest {
    @Mock
    private QuartzJobRepository quartzJobRepository;
    @Mock
    private Scheduler scheduler;
    @InjectMocks
    private QuartzJobService quartzJobService;

    @Test
    @DisplayName("排程清單_成功")
    void list_ok() {
        List<QuartzJobModel> list = new ArrayList<>();
        QuartzJobModel q1 = new QuartzJobModel();
        q1.setId(1L);
        list.add(q1);
        Mockito.when(quartzJobRepository.findAll()).thenReturn(list);
        ResponseEntity<ApiResponse> response = quartzJobService.list();
        List<QuartzJobResponse> expectList = new ArrayList<>();
        expectList.add(new QuartzJobResponse(q1));
        Assertions.assertEquals(ApiResponse.success(ApiResponseCode.SUCCESS, expectList), response);
    }

    @Test
    @DisplayName("新增排程_找不到class_錯誤")
    void add_classNotFound_error() {
        QuartzJobRequest request = new QuartzJobRequest();
        request.setClassPath("testErrorPath");
        Assertions.assertThrows(ClassNotFoundException.class, () -> quartzJobService.add(request));
    }

    @Test
    @DisplayName("新增排程_cron轉換錯誤_錯誤")
    void add_parseError() {
        QuartzJobRequest request = new QuartzJobRequest();
        request.setCron("*/60 * * * * ?");
        request.setClassPath("com.erp.base.config.quartz.job.TestJob");
        Assertions.assertThrows(ParseException.class, () -> quartzJobService.add(request));
    }

    @Test
    @DisplayName("新增排程_排程錯誤_錯誤")
    void add_schedulerException_error() throws SchedulerException {
        Mockito.doThrow(SchedulerException.class).when(scheduler).scheduleJob(Mockito.any(), Mockito.any());
        QuartzJobRequest request = new QuartzJobRequest();
        request.setCron("*/30 * * * * ?");
        request.setClassPath("com.erp.base.config.quartz.job.TestJob");
        Assertions.assertThrows(SchedulerException.class, () -> quartzJobService.add(request));
    }

    @Test
    @DisplayName("刪除排程_成功")
    void delete_ok() throws SchedulerException {
        QuartzJobModel job = new QuartzJobModel();
        job.setName("test");
        Mockito.when(quartzJobRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(job));
        quartzJobService.delete(1L);
    }

    @Test
    @DisplayName("切換排程_未知ID_錯誤")
    void toggle_unknownId_error() throws SchedulerException {
        Mockito.when(quartzJobRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        IdRequest request = new IdRequest();
        request.setId(1L);
        ResponseEntity<ApiResponse> toggle = quartzJobService.toggle(request);
        Assertions.assertEquals(ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "JobId Not Found."), toggle);
    }

    @Test
    @DisplayName("切換排程_開啟_成功")
    void toggle_true_ok() throws SchedulerException {
        QuartzJobModel job = new QuartzJobModel();
        job.setName("test");
        job.setGroupName("test");
        job.setStatus(true);
        Mockito.when(quartzJobRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(job));
        IdRequest request = new IdRequest();
        request.setId(1L);
        ResponseEntity<ApiResponse> toggle = quartzJobService.toggle(request);
        Assertions.assertEquals(ApiResponse.success(ApiResponseCode.SUCCESS), toggle);
        Mockito.verify(scheduler, Mockito.times(1)).resumeJob(Mockito.any());
    }

    @Test
    @DisplayName("切換排程_關閉_成功")
    void toggle_false_ok() throws SchedulerException {
        QuartzJobModel job = new QuartzJobModel();
        job.setName("test");
        job.setGroupName("test");
        job.setStatus(false);
        Mockito.when(quartzJobRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(job));
        IdRequest request = new IdRequest();
        request.setId(1L);
        ResponseEntity<ApiResponse> toggle = quartzJobService.toggle(request);
        Assertions.assertEquals(ApiResponse.success(ApiResponseCode.SUCCESS), toggle);
        Mockito.verify(scheduler, Mockito.times(1)).pauseJob(Mockito.any());
    }

    @Test
    @DisplayName("執行排程_成功")
    void exec_ok() throws SchedulerException {
        QuartzJobModel job = new QuartzJobModel();
        job.setName("test");
        job.setGroupName("test");
        Mockito.when(quartzJobRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(job));
        IdRequest request = new IdRequest();
        request.setId(1L);
        quartzJobService.exec(request);
        Mockito.verify(scheduler, Mockito.times(1)).triggerJob(Mockito.any());
    }
}