package com.erp.base.service;


import com.erp.base.model.constant.response.ApiResponseCode;
import com.erp.base.model.dto.request.salary.SalaryRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.dto.response.PageResponse;
import com.erp.base.model.dto.response.SalaryResponse;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.entity.SalaryModel;
import com.erp.base.repository.SalaryRepository;
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
import java.util.List;

@ExtendWith(MockitoExtension.class)
class SalaryServiceTest {
    @Mock
    private SalaryRepository salaryRepository;
    @Mock
    private ClientService clientService;
    @InjectMocks
    private SalaryService salaryService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        salaryService.setMessageService(Mockito.mock(MessageService.class));
        salaryService.setNotificationService(Mockito.mock(NotificationService.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("薪資設定清單_成功")
    void getRoots_ok() {
        ArrayList<SalaryModel> salaryModels = new ArrayList<>();
        SalaryModel r = new SalaryModel();
        r.setId(1L);
        r.setRoot(true);
        ClientModel user = new ClientModel(1);
        user.setUsername("test");
        r.setUser(user);
        salaryModels.add(r);
        Page<SalaryModel> page = new PageImpl<>(salaryModels);
        Mockito.when(salaryRepository.findAll((Specification<SalaryModel>)Mockito.any(), (PageRequest)Mockito.any())).thenReturn(page);
        ResponseEntity<ApiResponse> response = salaryService.getRoots(new SalaryRequest());
        Assertions.assertEquals(ApiResponse.success(new PageResponse<>(page, SalaryResponse.class)), response);
    }

    @Test
    @DisplayName("新增薪資設定_找不到用戶_錯誤")
    void editRoot_userNotFound_error() {
        ResponseEntity<ApiResponse> response = salaryService.editRoot(new SalaryRequest());
        Assertions.assertEquals(ApiResponse.error(ApiResponseCode.USER_NOT_FOUND), response);
    }

    @Test
    @DisplayName("新增薪資設定_成功")
    void editRoot_add_ok() {
        UserDetailImpl principal = new UserDetailImpl(new ClientModel(1), null);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(clientService.findNameByUserId(Mockito.anyLong())).thenReturn("test");
        Mockito.when(salaryRepository.findByUserIdAndRoot(Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(null);
        SalaryRequest request = new SalaryRequest();
        request.setUserId(1L);
        ResponseEntity<ApiResponse> response = salaryService.editRoot(request);
        Assertions.assertEquals(ApiResponse.success(ApiResponseCode.SUCCESS), response);
    }

    @Test
    @DisplayName("編輯薪資設定_成功")
    void editRoot_update_ok() {
        UserDetailImpl principal = new UserDetailImpl(new ClientModel(1), null);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(clientService.findNameByUserId(Mockito.anyLong())).thenReturn("test");
        Mockito.when(salaryRepository.findByUserIdAndRoot(Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(new SalaryModel());
        SalaryRequest request = new SalaryRequest();
        request.setUserId(1L);
        ResponseEntity<ApiResponse> response = salaryService.editRoot(request);
        Assertions.assertEquals(ApiResponse.success(ApiResponseCode.SUCCESS), response);
    }

    @Test
    @DisplayName("用戶薪資單_找不到用戶_錯誤")
    void get_userNotFound_error() {
        ResponseEntity<ApiResponse> response = salaryService.get();
        Assertions.assertEquals(ApiResponse.success(ApiResponseCode.USER_NOT_FOUND), response);
    }

    @Test
    @DisplayName("用戶薪資單_成功")
    void get_ok() {
        UserDetailImpl principal = new UserDetailImpl(new ClientModel(1), null);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        ArrayList<SalaryModel> salaryModels = new ArrayList<>();
        SalaryModel salaryModel = new SalaryModel();
        salaryModel.setId(1L);
        salaryModel.setUser(new ClientModel(1L));
        salaryModels.add(salaryModel);
        Mockito.when(salaryRepository.findByUserIdAndNotRoot(Mockito.anyLong())).thenReturn(salaryModels);
        ResponseEntity<ApiResponse> response = salaryService.get();
        List<SalaryResponse> salaryResponses = new ArrayList<>();
        salaryResponses.add(new SalaryResponse(salaryModel));
        Assertions.assertEquals(ApiResponse.success(ApiResponseCode.SUCCESS, salaryResponses), response);
    }

    @Test
    @DisplayName("用戶薪資單詳細_未知ID_錯誤")
    void info_unknownId_error() {
        Mockito.when(salaryRepository.findByIdAndRootIsFalse(Mockito.anyLong())).thenReturn(null);
        ResponseEntity<ApiResponse> response = salaryService.info(1L);
        Assertions.assertEquals(ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "Id[" + 1L + "] Not Found"), response);
    }

    @Test
    @DisplayName("用戶薪資單詳細_成功")
    void info_ok() {
        SalaryModel salaryModel = new SalaryModel();
        salaryModel.setId(1L);
        ClientModel user = new ClientModel(1L);
        user.setUsername("test");
        salaryModel.setUser(user);
        Mockito.when(salaryRepository.findByIdAndRootIsFalse(Mockito.anyLong())).thenReturn(salaryModel);
        ResponseEntity<ApiResponse> response = salaryService.info(1L);
        Assertions.assertEquals(ApiResponse.success(ApiResponseCode.SUCCESS, new SalaryResponse(salaryModel)), response);
    }

    @Test
    @DisplayName("月結薪資單統整_成功")
    void execCalculate_ok() {
        List<SalaryModel> salaryModels = new ArrayList<>();
        SalaryModel salaryModel = new SalaryModel();
        salaryModel.setId(1L);
        salaryModel.setUser(new ClientModel(1L));
        salaryModels.add(salaryModel);
        Mockito.when(salaryRepository.findByRoot()).thenReturn(salaryModels);
        salaryService.execCalculate();
        Mockito.verify(salaryRepository, Mockito.times(1)).saveAll(Mockito.any());
    }
}