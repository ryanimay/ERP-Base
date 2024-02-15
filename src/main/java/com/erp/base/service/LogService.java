package com.erp.base.service;

import com.erp.base.model.dto.request.log.LogRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.dto.response.LogResponse;
import com.erp.base.model.dto.response.PageResponse;
import com.erp.base.model.entity.LogModel;
import com.erp.base.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LogService {
    private LogRepository logRepository;
    @Autowired
    public void setLogRepository(LogRepository logRepository){
        this.logRepository = logRepository;
    }

    public void save(LogModel model){
        logRepository.save(model);
    }

    public ResponseEntity<ApiResponse> findAll(LogRequest request) {
        Page<LogModel> all = logRepository.findAll(request.getSpecification(), request.getPage());
        return ApiResponse.success(new PageResponse<>(all, LogResponse.class));
    }
}
