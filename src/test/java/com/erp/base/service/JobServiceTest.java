package com.erp.base.service;


import com.erp.base.model.constant.StatusConstant;
import com.erp.base.model.constant.response.ApiResponseCode;
import com.erp.base.model.dto.request.job.JobRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.dto.response.JobResponse;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.entity.JobModel;
import com.erp.base.repository.JobRepository;
import com.erp.base.service.security.UserDetailImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {
    @Mock
    private JobRepository jobRepository;
    @InjectMocks
    private JobService jobService;
    private static final ClientModel clientModel = new ClientModel(1);
    private static final List<JobModel> list;
    private static final JobModel j1;
    private static final JobModel j2;
    private static final JobModel j3;
    private static final JobModel j4;
    static{
        list = new ArrayList<>();
        j1 = new JobModel();
        j1.setCreateBy(clientModel);
        j1.setUser(new ClientModel(5));
        j1.setStatus(StatusConstant.PENDING_NO);
        j2 = new JobModel();
        j2.setCreateBy(clientModel);
        j2.setUser(clientModel);
        j2.setStatus(StatusConstant.APPROVED_NO);
        j3 = new JobModel();
        j3.setCreateBy(clientModel);
        j3.setUser(clientModel);
        j3.setStatus(StatusConstant.CLOSED_NO);
        j4 = new JobModel();
        j4.setCreateBy(clientModel);
        j4.setUser(clientModel);
        j4.setStatus(StatusConstant.PENDING_NO);
        list.add(j1);
        list.add(j2);
        list.add(j3);
        list.add(j4);
    }

    @Test
    @DisplayName("任務卡清單_找不到用戶_錯誤")
    void findAll_userNotFound_error() {
        ResponseEntity<ApiResponse> all = jobService.findAll();
        Assertions.assertEquals(ApiResponse.error(ApiResponseCode.USER_NOT_FOUND), all);
    }

    @Test
    @DisplayName("任務卡清單_成功")
    void findAll_ok() {
        UserDetailImpl principal = new UserDetailImpl(clientModel, null);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(jobRepository.findByUserOrTracking(Mockito.any())).thenReturn(list);
        ResponseEntity<ApiResponse> all = jobService.findAll();
        Map<String, List<JobResponse>> map = new HashMap<>();
        map.put("tracking", List.of(new JobResponse(j1)));
        map.put(StatusConstant.get(j2.getStatus()), List.of(new JobResponse(j2)));
        map.put(StatusConstant.get(j3.getStatus()), List.of(new JobResponse(j3)));
        map.put(StatusConstant.get(j4.getStatus()), List.of(new JobResponse(j4)));
        Assertions.assertEquals(ApiResponse.success(ApiResponseCode.SUCCESS, map), all);
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("新增任務卡_找不到用戶_錯誤")
    void add_userNotFound_error() {
        ResponseEntity<ApiResponse> all = jobService.add(new JobRequest());
        Assertions.assertEquals(ApiResponse.error(ApiResponseCode.USER_NOT_FOUND), all);
    }

    @Test
    @DisplayName("新增任務卡_成功")
    void add_ok() {
        UserDetailImpl principal = new UserDetailImpl(clientModel, null);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        ResponseEntity<ApiResponse> response = jobService.add(new JobRequest());
        Assertions.assertEquals(ApiResponse.success(ApiResponseCode.SUCCESS), response);
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("更新任務卡_未知ID_錯誤")
    void update_unknownId_error() {
        Mockito.when(jobRepository.findById(Mockito.any())).thenReturn(Optional.empty());
        ResponseEntity<ApiResponse> response = jobService.update(new JobRequest());
        Assertions.assertEquals(ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "Id Not Found."), response);
    }

    @Test
    @DisplayName("更新任務卡_成功")
    void update_ok() {
        Mockito.when(jobRepository.findById(Mockito.any())).thenReturn(Optional.of(j1));
        Mockito.when(jobRepository.save(Mockito.any())).thenReturn(j2);
        ResponseEntity<ApiResponse> response = jobService.update(new JobRequest());
        Assertions.assertEquals(ApiResponse.success(ApiResponseCode.SUCCESS, new JobResponse(j2)), response);
    }
}