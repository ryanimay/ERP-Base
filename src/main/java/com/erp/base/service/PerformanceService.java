package com.erp.base.service;

import com.erp.base.enums.response.ApiResponseCode;
import com.erp.base.model.ClientIdentity;
import com.erp.base.model.dto.request.performance.AddPerformanceRequest;
import com.erp.base.model.dto.request.performance.PerformanceListRequest;
import com.erp.base.model.dto.request.performance.UpdatePerformanceRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.entity.PerformanceModel;
import com.erp.base.model.entity.UserModel;
import com.erp.base.repository.PerformanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PerformanceService {
    private PerformanceRepository performanceRepository;
    @Autowired
    public void setPerformanceRepository(PerformanceRepository performanceRepository){
        this.performanceRepository = performanceRepository;
    }

    public ResponseEntity<ApiResponse> getAllList(PerformanceListRequest request) {
        List<PerformanceModel> allPerformance = performanceRepository.findAllPerformance(request.getUserId(), request.getStartTime(), request.getEndTime(), request.getPage());
        return ApiResponse.success(ApiResponseCode.SUCCESS, allPerformance);
    }

    public ResponseEntity<ApiResponse> getList(PerformanceListRequest request) {
        UserModel user = ClientIdentity.getUser();
        if(user == null){
            return ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "User Not Found");
        }
        request.setUserId(user.getId());//限搜本人
        return getAllList(request);
    }

    public ResponseEntity<ApiResponse> add(AddPerformanceRequest request) {
        UserModel user = ClientIdentity.getUser();
        if(user == null){
            return ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "User Not Found");
        }
        request.setCreateBy(user.getId());
        performanceRepository.save(request.toModel());
        return ApiResponse.success(ApiResponseCode.SUCCESS);
    }

    public ResponseEntity<ApiResponse> save(UpdatePerformanceRequest request) {
        performanceRepository.save(request.toModel());
        return ApiResponse.success(ApiResponseCode.SUCCESS);
    }

    public ResponseEntity<ApiResponse> remove(Long eventId) {
        performanceRepository.updateStateRemoved(eventId);
        return ApiResponse.success(ApiResponseCode.SUCCESS);
    }

    public ResponseEntity<ApiResponse> accept(Long eventId) {
        performanceRepository.updateStateAccept(eventId);
        return ApiResponse.success(ApiResponseCode.SUCCESS);
    }
}
