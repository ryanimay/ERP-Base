package com.erp.base.service;

import com.erp.base.enums.response.ApiResponseCode;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.entity.JobModel;
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
}
