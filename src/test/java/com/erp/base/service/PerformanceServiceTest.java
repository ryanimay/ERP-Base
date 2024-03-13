package com.erp.base.service;


import com.erp.base.model.constant.StatusConstant;
import com.erp.base.model.constant.response.ApiResponseCode;
import com.erp.base.model.dto.request.performance.PerformanceRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.dto.response.PageResponse;
import com.erp.base.model.dto.response.PerformanceResponse;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.entity.DepartmentModel;
import com.erp.base.model.entity.PerformanceModel;
import com.erp.base.repository.PerformanceRepository;
import com.erp.base.service.security.UserDetailImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class PerformanceServiceTest {
    @Mock
    private PerformanceRepository performanceRepository;
    @Mock
    private MessageService messageService;
    @Mock
    private NotificationService notificationService;
    @Mock
    private ClientService clientService;
    @InjectMocks
    private PerformanceService performanceService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("績效清單_成功")
    void getList_ok() {
        ArrayList<PerformanceModel> performanceModels = new ArrayList<>();
        PerformanceModel p = new PerformanceModel();
        p.setId(1L);
        ClientModel user = new ClientModel(1);
        user.setUsername("test");
        p.setUser(user);
        p.setCreateBy(user);
        p.setStatus(StatusConstant.PENDING_NO);
        performanceModels.add(p);
        Page<PerformanceModel> page = new PageImpl<>(performanceModels);
        Mockito.when(performanceRepository.findAll((Specification<PerformanceModel>)Mockito.any(), (PageRequest)Mockito.any())).thenReturn(page);
        ResponseEntity<ApiResponse> list = performanceService.getList(new PerformanceRequest());
        Assertions.assertEquals(ApiResponse.success(new PageResponse<>(page, PerformanceResponse.class)), list);
    }

    @Test
    @DisplayName("新增績效_找不到用戶_錯誤")
    void add_userNotFound_error() {
        ResponseEntity<ApiResponse> add = performanceService.add(new PerformanceRequest());
        Assertions.assertEquals(ApiResponse.error(ApiResponseCode.ACCESS_DENIED, "User Identity Not Found"), add);
    }

    @Test
    @DisplayName("新增績效_成功")
    void add_ok() {
        ClientModel clientModel = new ClientModel(1);
        clientModel.setDepartment(new DepartmentModel(1L));
        UserDetailImpl principal = new UserDetailImpl(clientModel, null);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        ResponseEntity<ApiResponse> add = performanceService.add(new PerformanceRequest());
        Assertions.assertEquals(ApiResponse.success(ApiResponseCode.SUCCESS), add);
    }

    @Test
    @DisplayName("更新績效_找不到用戶_錯誤")
    void save_userNotFound_error() {
        ResponseEntity<ApiResponse> add = performanceService.save(new PerformanceRequest());
        Assertions.assertEquals(ApiResponse.error(ApiResponseCode.ACCESS_DENIED, "User Identity Not Found"), add);
    }

    @Test
    @DisplayName("更新績效_未知ID_錯誤")
    void save_unknownId_error() {
        ClientModel clientModel = new ClientModel(1);
        clientModel.setDepartment(new DepartmentModel(1L));
        UserDetailImpl principal = new UserDetailImpl(clientModel, null);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(performanceRepository.findById(Mockito.any())).thenReturn(Optional.empty());
        PerformanceRequest request = new PerformanceRequest();
        request.setId(1L);
        ResponseEntity<ApiResponse> add = performanceService.save(request);
        Assertions.assertEquals(ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "Performance not found: id[" + request.getId() + "]"), add);
    }

    @Test
    @DisplayName("更新績效_只能更新Pending_錯誤")
    void save_statusCantEdit_error() {
        ClientModel clientModel = new ClientModel(1);
        clientModel.setDepartment(new DepartmentModel(1L));
        UserDetailImpl principal = new UserDetailImpl(clientModel, null);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        PerformanceModel performanceModel = new PerformanceModel();
        performanceModel.setId(1L);
        performanceModel.setStatus(StatusConstant.APPROVED_NO);
        Mockito.when(performanceRepository.findById(Mockito.any())).thenReturn(Optional.of(performanceModel));
        PerformanceRequest request = new PerformanceRequest();
        request.setId(1L);
        ResponseEntity<ApiResponse> add = performanceService.save(request);
        Assertions.assertEquals(ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "Can only modify performances in 'Pending' status."), add);
    }

    @Test
    @DisplayName("更新績效_成功")
    void save_ok() {
        ClientModel clientModel = new ClientModel(1);
        clientModel.setDepartment(new DepartmentModel(1L));
        UserDetailImpl principal = new UserDetailImpl(clientModel, null);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        PerformanceModel performanceModel = new PerformanceModel();
        performanceModel.setId(1L);
        performanceModel.setStatus(StatusConstant.PENDING_NO);
        Mockito.when(performanceRepository.findById(Mockito.any())).thenReturn(Optional.of(performanceModel));
        PerformanceRequest request = new PerformanceRequest();
        request.setId(1L);
        ResponseEntity<ApiResponse> add = performanceService.save(request);
        Assertions.assertEquals(ApiResponse.success(ApiResponseCode.SUCCESS), add);
    }

    @Test
    @DisplayName("移除績效_未知ID_錯誤")
    void remove_unknownId_error() {
        Mockito.when(performanceRepository.updateStatus(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(2);
        ResponseEntity<ApiResponse> remove = performanceService.remove(1L);
        Assertions.assertEquals(ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "Performance id[" + 1 + "] not found"), remove);
    }

    @Test
    @DisplayName("移除績效_成功")
    void remove_ok() {
        Mockito.when(performanceRepository.updateStatus(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(1);
        ResponseEntity<ApiResponse> remove = performanceService.remove(1L);
        Assertions.assertEquals(ApiResponse.success(ApiResponseCode.SUCCESS), remove);
    }
}