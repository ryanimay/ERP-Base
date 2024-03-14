package com.erp.base.service;


import com.erp.base.model.constant.response.ApiResponseCode;
import com.erp.base.model.dto.request.procurement.ProcurementRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.dto.response.PageResponse;
import com.erp.base.model.dto.response.ProcurementResponse;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.entity.ProcurementModel;
import com.erp.base.repository.ProcurementRepository;
import org.junit.jupiter.api.Assertions;
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

import java.util.ArrayList;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ProcurementServiceTest {
    @Mock
    private ProcurementRepository procurementRepository;
    @InjectMocks
    private ProcurementService procurementService;

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
}