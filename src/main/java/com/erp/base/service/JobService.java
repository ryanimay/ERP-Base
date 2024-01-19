package com.erp.base.service;

import com.erp.base.enums.response.ApiResponseCode;
import com.erp.base.model.ClientIdentity;
import com.erp.base.model.dto.request.job.JobRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.entity.JobModel;
import com.erp.base.model.entity.UserModel;
import com.erp.base.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class JobService {
    private JobRepository jobRepository;
    @Autowired
    public void setJobRepository(JobRepository jobRepository){
        this.jobRepository = jobRepository;
    }

    public ResponseEntity<ApiResponse> findAll() {
        List<JobModel> all = jobRepository.findAll();
        Map<String, List<JobModel>> map = new HashMap<>();
        all.forEach(model -> {
            List<JobModel> list = map.computeIfAbsent(model.getStatus(), v -> new ArrayList<>());
            list.add(model);
        });
        return ApiResponse.success(ApiResponseCode.SUCCESS, map);
    }

    public ResponseEntity<ApiResponse> add(JobRequest request) {
        UserModel user = ClientIdentity.getUser();
        JobModel jobModel = request.toModel();
        if(jobModel.getUser() == null) jobModel.setUser(user);
        jobModel.setCreateBy(user.getId());
        jobRepository.save(jobModel);
        return ApiResponse.success(ApiResponseCode.SUCCESS);
    }
}
