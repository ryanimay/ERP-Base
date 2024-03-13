package com.erp.base.service;

import com.erp.base.model.constant.StatusConstant;
import com.erp.base.model.constant.response.ApiResponseCode;
import com.erp.base.model.ClientIdentity;
import com.erp.base.model.dto.request.job.JobRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.dto.response.JobResponse;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.entity.JobModel;
import com.erp.base.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class JobService {
    private JobRepository jobRepository;

    @Autowired
    public void setJobRepository(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public ResponseEntity<ApiResponse> findAll() {
        ClientModel user = ClientIdentity.getUser();
        if(user == null) return ApiResponse.error(ApiResponseCode.USER_NOT_FOUND);
        List<JobModel> all = jobRepository.findByUserOrTracking(user);

        Map<String, List<JobResponse>> map = all.stream()
                .collect(Collectors.groupingBy(
                        model -> model.getUser().equals(user) ? StatusConstant.get(model.getStatus()) : "tracking",
                        Collectors.mapping(JobResponse::new, Collectors.toList())
                ));
        return ApiResponse.success(ApiResponseCode.SUCCESS, map);
    }

    public ResponseEntity<ApiResponse> add(JobRequest request) {
        ClientModel user = ClientIdentity.getUser();
        if(user == null) return ApiResponse.error(ApiResponseCode.USER_NOT_FOUND);
        JobModel jobModel = request.toModel();
        if (jobModel.getUser() == null) jobModel.setUser(user);
        jobModel.setCreateBy(user);
        jobRepository.save(jobModel);
        return ApiResponse.success(ApiResponseCode.SUCCESS);
    }

    public ResponseEntity<ApiResponse> update(JobRequest request) {
        Optional<JobModel> byId = jobRepository.findById(request.getId());
        if (byId.isPresent()) {
            JobModel model = byId.get();
            if (request.getInfo() != null) model.setInfo(request.getInfo());
            if (request.getUserId() != null) model.setUser(new ClientModel(request.getUserId()));
            if (request.getStartTime() != null) model.setStartTime(request.getStartTime());
            if (request.getEndTime() != null) model.setEndTime(request.getEndTime());
            if (request.getStatus() != null) model.setStatus(request.getStatus());
            if (request.getOrder() != null) model.setOrder(request.getOrder());
            if (request.getIdSet() != null) request.getIdSet().forEach(id -> model.addTracking(new ClientModel(id)));
            JobModel save = jobRepository.save(model);
            return ApiResponse.success(ApiResponseCode.SUCCESS, new JobResponse(save));
        }
        return ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "Id Not Found.");
    }

    public ResponseEntity<ApiResponse> deleteById(Long id) {
        jobRepository.deleteById(id);
        return ApiResponse.success(ApiResponseCode.SUCCESS);
    }
}
