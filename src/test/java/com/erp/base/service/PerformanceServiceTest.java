package com.erp.base.service;


import com.erp.base.model.constant.RoleConstant;
import com.erp.base.model.constant.StatusConstant;
import com.erp.base.model.constant.response.ApiResponseCode;
import com.erp.base.model.dto.request.PageRequestParam;
import com.erp.base.model.dto.request.performance.PerformanceAcceptRequest;
import com.erp.base.model.dto.request.performance.PerformanceRequest;
import com.erp.base.model.dto.response.*;
import com.erp.base.model.dto.security.ClientIdentityDto;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.entity.DepartmentModel;
import com.erp.base.model.entity.PerformanceModel;
import com.erp.base.model.entity.RoleModel;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class PerformanceServiceTest {
    @Mock
    private PerformanceRepository performanceRepository;
    @InjectMocks
    private PerformanceService performanceService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        performanceService.setMessageService(Mockito.mock(MessageService.class));
        performanceService.setNotificationService(Mockito.mock(NotificationService.class));
        performanceService.setClientService(Mockito.mock(ClientService.class));
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
        UserDetailImpl principal = new UserDetailImpl(new ClientIdentityDto(clientModel), null);
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
        UserDetailImpl principal = new UserDetailImpl(new ClientIdentityDto(clientModel), null);
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
        UserDetailImpl principal = new UserDetailImpl(new ClientIdentityDto(clientModel), null);
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
        UserDetailImpl principal = new UserDetailImpl(new ClientIdentityDto(clientModel), null);
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

    @Test
    @DisplayName("審核績效_未知ID_錯誤")
    void accept_unknownId_error() {
        ClientModel clientModel = new ClientModel(1);
        clientModel.setDepartment(new DepartmentModel(1L));
        UserDetailImpl principal = new UserDetailImpl(new ClientIdentityDto(clientModel), null);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(performanceRepository.updateStatus(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(2);
        PerformanceAcceptRequest request = new PerformanceAcceptRequest();
        request.setEventId(1L);
        ResponseEntity<ApiResponse> remove = performanceService.accept(request);
        Assertions.assertEquals(ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "Performance id[" + request.getEventId() + "] not found"), remove);
    }

    @Test
    @DisplayName("審核績效_無用戶_錯誤")
    void accept_identityError_error() {
        PerformanceAcceptRequest request = new PerformanceAcceptRequest();
        request.setEventId(1L);
        ResponseEntity<ApiResponse> remove = performanceService.accept(request);
        Assertions.assertEquals(ApiResponse.error(ApiResponseCode.ACCESS_DENIED, "User Identity Not Found"), remove);
    }

    @Test
    @DisplayName("審核績效_成功")
    void accept_ok() {
        ClientModel clientModel = new ClientModel(1);
        clientModel.setDepartment(new DepartmentModel(1L));
        UserDetailImpl principal = new UserDetailImpl(new ClientIdentityDto(clientModel), null);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(performanceRepository.updateStatus(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(1);
        PerformanceAcceptRequest request = new PerformanceAcceptRequest();
        request.setEventId(1L);
        request.setEventUserId(1L);
        ResponseEntity<ApiResponse> remove = performanceService.accept(request);
        Assertions.assertEquals(ApiResponse.success(ApiResponseCode.SUCCESS), remove);
    }

    @Test
    @DisplayName("待核績效清單_用戶驗證_錯誤")
    void pendingList_identityError_error() {
        ResponseEntity<ApiResponse> pendingList = performanceService.pendingList(new PageRequestParam());
        Assertions.assertEquals(ApiResponse.error(ApiResponseCode.ACCESS_DENIED, "User Identity Not Found"), pendingList);
    }

    @Test
    @DisplayName("待核績效清單_管理層權搜_成功")
    void pendingList_ok() {
        ClientModel clientModel = new ClientModel(1);
        HashSet<RoleModel> roles = new HashSet<>();
        RoleModel role = new RoleModel(1L);
        role.setLevel(RoleConstant.LEVEL_3);
        roles.add(role);
        clientModel.setUsername("test");
        clientModel.setRoles(roles);
        clientModel.setDepartment(new DepartmentModel(1L));
        UserDetailImpl principal = new UserDetailImpl(new ClientIdentityDto(clientModel), null);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        ArrayList<PerformanceModel> performanceModels = new ArrayList<>();
        PerformanceModel performanceModel = new PerformanceModel();
        performanceModel.setId(1L);
        performanceModel.setUser(clientModel);
        performanceModel.setCreateBy(clientModel);
        performanceModel.setEvent("test");
        performanceModel.setStatus(StatusConstant.PENDING_NO);
        performanceModels.add(performanceModel);
        Page<PerformanceModel> page = new PageImpl<>(performanceModels);
        Mockito.when(performanceRepository.findAllByStatus(Mockito.anyInt(), Mockito.anyLong(), Mockito.any())).thenReturn(page);
        ResponseEntity<ApiResponse> remove = performanceService.pendingList(new PageRequestParam());
        Assertions.assertEquals(ApiResponse.success(new PageResponse<>(page, PerformanceResponse.class)), remove);
    }

    @Test
    @DisplayName("結算績效_成功")
    void calculate_ok() {
        Set<Object[]> set = new HashSet<>();
        ClientModel model = new ClientModel(1);
        model.setUsername("test");
        Object[] obj = {model, new BigDecimal(1000), new BigDecimal("0.5"), 2024, 1L};
        set.add(obj);
        Mockito.when(performanceRepository.calculateByCreateYear(Mockito.anyLong(), Mockito.anyInt())).thenReturn(set);
        ResponseEntity<ApiResponse> calculate = performanceService.calculate(1L);
        PerformanceCalculateResponse performanceCalculateResponse = new PerformanceCalculateResponse();
        ClientModel user = (ClientModel) obj[0];
        performanceCalculateResponse.setUser(new ClientNameObject(user));
        performanceCalculateResponse.setFixedBonus((BigDecimal) obj[1]);
        performanceCalculateResponse.setPerformanceRatio((BigDecimal) obj[2]);
        performanceCalculateResponse.setSettleYear(String.valueOf(obj[3]));
        performanceCalculateResponse.setCount((Long) obj[4]);
        Assertions.assertEquals(ApiResponse.success(ApiResponseCode.SUCCESS, performanceCalculateResponse), calculate);
    }
}