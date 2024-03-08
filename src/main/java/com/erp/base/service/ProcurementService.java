package com.erp.base.service;

import com.erp.base.model.constant.response.ApiResponseCode;
import com.erp.base.model.ClientIdentity;
import com.erp.base.model.dto.request.procurement.ProcurementRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.dto.response.PageResponse;
import com.erp.base.model.dto.response.ProcurementResponse;
import com.erp.base.model.entity.ProcurementModel;
import com.erp.base.repository.ProcurementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class ProcurementService {
    private ProcurementRepository procurementRepository;

    @Autowired
    public void setProcurementRepository(ProcurementRepository procurementRepository) {
        this.procurementRepository = procurementRepository;
    }

    public ResponseEntity<ApiResponse> findAll(ProcurementRequest request) {
        Page<ProcurementModel> pageResult = procurementRepository.findAll(request.getSpecification(), request.getPage());
        return ApiResponse.success(new PageResponse<>(pageResult, ProcurementResponse.class));
    }

    public ResponseEntity<ApiResponse> add(ProcurementRequest request) {
        ProcurementModel procurementModel = request.toModel();
        procurementModel.setCreateBy(ClientIdentity.getUser());
        procurementRepository.save(procurementModel);
        return ApiResponse.success(ApiResponseCode.SUCCESS);
    }

    public ResponseEntity<ApiResponse> update(ProcurementRequest request) {
        Optional<ProcurementModel> model = procurementRepository.findById(request.getId());
        if (model.isPresent()) {
            ProcurementModel procurementModel = model.get();
            if (request.getType() != null) procurementModel.setType(request.getType());
            if (request.getName() != null) procurementModel.setName(request.getName());
            if (request.getPrice() != null) procurementModel.setPrice(request.getPrice());
            if (request.getCount() != null) procurementModel.setCount(request.getCount());
            if (request.getInfo() != null) procurementModel.setInfo(request.getInfo());
            if (request.getStatus() != null) procurementModel.setStatus(request.getStatus());
            procurementRepository.save(procurementModel);
            return ApiResponse.success(ApiResponseCode.SUCCESS);
        }
        return ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR);
    }

    public ResponseEntity<ApiResponse> delete(Long id) {
        procurementRepository.deleteById(id);
        return ApiResponse.success(ApiResponseCode.SUCCESS);
    }
}
