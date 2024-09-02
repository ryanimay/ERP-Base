package com.erp.base.service;

import com.erp.base.model.ClientIdentity;
import com.erp.base.model.constant.StatusConstant;
import com.erp.base.model.constant.response.ApiResponseCode;
import com.erp.base.model.dto.request.job.JobRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.dto.response.JobResponse;
import com.erp.base.model.dto.security.ClientIdentityDto;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.entity.JobModel;
import com.erp.base.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class JobService {
    private JobRepository jobRepository;
    private CacheService cacheService;
    @Autowired
    public void setCacheService(CacheService cacheService){
        this.cacheService = cacheService;
    }
    @Autowired
    public void setJobRepository(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public ResponseEntity<ApiResponse> findAll() {
        ClientIdentityDto user = ClientIdentity.getUser();
        if(user == null) return ApiResponse.error(ApiResponseCode.USER_NOT_FOUND);
        ClientIdentityDto client = cacheService.getClient(user.getId());
        ClientModel clientModel = client.toEntity();
        List<JobModel> all = jobRepository.findByUserOrTracking(clientModel);

        Map<String, List<JobResponse>> map = all.stream()
                .collect(Collectors.groupingBy(
                        model -> model.getUser().equals(clientModel) ? StatusConstant.get(model.getStatus()) : "tracking",
                        Collectors.mapping(JobResponse::new, Collectors.toList())
                ));
        return ApiResponse.success(ApiResponseCode.SUCCESS, map);
    }

    public ResponseEntity<ApiResponse> add(JobRequest request) {
        ClientIdentityDto user = ClientIdentity.getUser();
        if(user == null) return ApiResponse.error(ApiResponseCode.USER_NOT_FOUND);
        JobModel jobModel = request.toModel();
        if (jobModel.getUser() == null) jobModel.setUser(new ClientModel(user.getId()));
        jobModel.setCreateBy(new ClientModel(user.getId()));
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
            Set<Long> idSet = request.getIdSet();
            if (idSet != null){
                if (idSet.isEmpty()) {
                    //沒元素就清空
                    model.setTrackingList(new HashSet<>());
                }else{
                    idSet.forEach(id -> model.addTracking(new ClientModel(id)));
                }
            }
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
