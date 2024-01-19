package com.erp.base.service;

import com.erp.base.enums.response.ApiResponseCode;
import com.erp.base.model.ClientIdentity;
import com.erp.base.model.dto.request.procurement.ProcurementRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.dto.response.PageResponse;
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
    public void setProcurementRepository(ProcurementRepository procurementRepository){
        this.procurementRepository = procurementRepository;
    }

    public ResponseEntity<ApiResponse> findAll(ProcurementRequest request) {
        Page<ProcurementModel> pageResult = procurementRepository.findAll(request.getSpecification(), request.getPage());
        return ApiResponse.success(new PageResponse<>(pageResult, ProcurementModel.class));
    }

    public ResponseEntity<ApiResponse> add(ProcurementRequest request) {
        ProcurementModel procurementModel = request.toModel();
        procurementModel.setCreateBy(ClientIdentity.getUser().getId());
        procurementRepository.save(procurementModel);
        return ApiResponse.success(ApiResponseCode.SUCCESS);
    }

    public ResponseEntity<ApiResponse> update(ProcurementRequest request) {
        Optional<ProcurementModel> model = procurementRepository.findById(request.getId());
        if(model.isPresent()){
            ProcurementModel procurementModel = model.get();
            procurementModel.setType(request.getType());
            procurementModel.setName(request.getName());
            procurementModel.setPrice(request.getPrice());
            procurementModel.setCount(request.getCount());
            procurementRepository.save(procurementModel);
            return ApiResponse.success(ApiResponseCode.SUCCESS);
        }
        return ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR);
    }
}
