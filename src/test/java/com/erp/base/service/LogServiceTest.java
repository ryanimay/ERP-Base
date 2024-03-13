package com.erp.base.service;


import com.erp.base.model.dto.request.log.LogRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.dto.response.LogResponse;
import com.erp.base.model.dto.response.PageResponse;
import com.erp.base.model.entity.LogModel;
import com.erp.base.repository.LogRepository;
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

@ExtendWith(MockitoExtension.class)
class LogServiceTest {
    @Mock
    private LogRepository logRepository;
    @InjectMocks
    private LogService logService;

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("部門清單_成功")
    void findAll_ok() {
        ArrayList<LogModel> logModels = new ArrayList<>();
        LogModel lm = new LogModel();
        lm.setId(1);
        logModels.add(lm);
        Page<LogModel> page = new PageImpl<>(logModels);
        Mockito.when(logRepository.findAll((Specification<LogModel>)Mockito.any(), (PageRequest)Mockito.any())).thenReturn(page);
        ResponseEntity<ApiResponse> all = logService.findAll(new LogRequest());
        Assertions.assertEquals(ApiResponse.success(new PageResponse<>(page, LogResponse.class)), all);
    }
}