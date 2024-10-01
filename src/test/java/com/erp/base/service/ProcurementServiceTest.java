package com.erp.base.service;


import com.erp.base.model.constant.response.ApiResponseCode;
import com.erp.base.model.dto.request.procurement.ProcurementRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.dto.response.PageResponse;
import com.erp.base.model.dto.response.ProcurementResponse;
import com.erp.base.model.dto.security.ClientIdentityDto;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.entity.ProcurementModel;
import com.erp.base.repository.ProcurementRepository;
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
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ProcurementServiceTest {
    @Mock
    private ProcurementRepository procurementRepository;
    @InjectMocks
    private ProcurementService procurementService;

    @BeforeEach
    void setUp() {
        procurementService.setCacheService(Mockito.mock(CacheService.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("採購清單_成功")
    void findAll_ok() {
        ArrayList<ProcurementModel> procurementModels = new ArrayList<>();
        ProcurementModel procurementModel = new ProcurementModel();
        procurementModel.setId(1L);
        ClientModel createBy = new ClientModel(1);
        createBy.setUsername("test");
        procurementModel.setCreateBy(createBy);
        procurementModel.setInfo("test");
        procurementModels.add(procurementModel);
        Page<ProcurementModel> page = new PageImpl<>(procurementModels);
        Mockito.when(procurementRepository.findAll((Specification<ProcurementModel>)Mockito.any(), (PageRequest)Mockito.any())).thenReturn(page);
        ResponseEntity<ApiResponse> all = procurementService.findAll(new ProcurementRequest());
        Assertions.assertEquals(ApiResponse.success(new PageResponse<>(page, ProcurementResponse.class)), all);
    }

    @Test
    @DisplayName("新增採購_成功")
    void add_ok() {
        UserDetailImpl principal = new UserDetailImpl(new ClientIdentityDto(new ClientModel(1)), null);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        ProcurementRequest request = new ProcurementRequest();
        request.setType(1);
        request.setCount(1L);
        ResponseEntity<ApiResponse> all = procurementService.add(request);
        Assertions.assertEquals(ApiResponse.success(ApiResponseCode.SUCCESS), all);
    }

    @Test
    @DisplayName("更新採購_未知ID_錯誤")
    void update_unknownId_error() {
        Mockito.when(procurementRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        ProcurementRequest request = new ProcurementRequest();
        request.setId(1L);
        request.setType(1);
        request.setCount(1L);
        ResponseEntity<ApiResponse> all = procurementService.update(request);
        Assertions.assertEquals(ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR), all);
    }

    @Test
    @DisplayName("更新採購_成功")
    void update_ok() {
        ProcurementModel procurementModel = new ProcurementModel();
        procurementModel.setId(1L);
        procurementModel.setType(1);
        procurementModel.setCount(1L);
        Mockito.when(procurementRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(procurementModel));
        ProcurementRequest request = new ProcurementRequest();
        request.setId(1L);
        ResponseEntity<ApiResponse> all = procurementService.update(request);
        Assertions.assertEquals(ApiResponse.success(ApiResponseCode.SUCCESS), all);
    }

    @Test
    @DisplayName("刪除採購_成功")
    void delete_ok() {
        ResponseEntity<ApiResponse> response = procurementService.delete(1L);
        Assertions.assertEquals(ApiResponse.success(ApiResponseCode.SUCCESS), response);
    }

    @Test
    @DisplayName("拿採購參數_成功")
    void getSystemProcure_ok() {
        List<Object[]> objects = new ArrayList<>();
        Object[] array = {"1", "2"};
        objects.add(array);
        Mockito.when(procurementRepository.getSystemProcure()).thenReturn(objects);
        Object[] response = procurementService.getSystemProcure();
        Assertions.assertEquals(array[0], response[0]);
        Assertions.assertEquals(array[1], response[1]);
    }
}