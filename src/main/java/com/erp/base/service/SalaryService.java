package com.erp.base.service;

import com.erp.base.enums.response.ApiResponseCode;
import com.erp.base.model.ClientIdentity;
import com.erp.base.model.dto.request.salary.EditSalaryRootRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.entity.SalaryModel;
import com.erp.base.model.entity.UserModel;
import com.erp.base.repository.SalaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SalaryService {
    private SalaryRepository salaryRepository;
    @Autowired
    public void setSalaryRepository(SalaryRepository salaryRepository){
        this.salaryRepository = salaryRepository;
    }

    public ResponseEntity<ApiResponse> getRoots() {
        List<SalaryModel> roots = salaryRepository.findByRoot();
        return ApiResponse.success(ApiResponseCode.SUCCESS, roots);
    }

    public ResponseEntity<ApiResponse> getRootById(long id) {
        return ApiResponse.success(ApiResponseCode.SUCCESS, salaryRepository.findByRootAndUserId(id).orElse(null));
    }

    public ResponseEntity<ApiResponse> editRoot(EditSalaryRootRequest editSalaryRootRequest) {
        SalaryModel salaryModel = editSalaryRootRequest.toModel();
        salaryRepository.save(salaryModel);
        return ApiResponse.success(ApiResponseCode.SUCCESS);
    }

    public ResponseEntity<ApiResponse> get() {
        UserModel user = ClientIdentity.getUser();
        if(user == null) {
            return ApiResponse.success(ApiResponseCode.UNKNOWN_ERROR, "UserNotFound");
        }
        List<SalaryModel> salaryList = salaryRepository.findByUserId(user.getId());
        return ApiResponse.success(ApiResponseCode.SUCCESS, salaryList);
    }

    public ResponseEntity<ApiResponse> info(long id) {
        Optional<SalaryModel> salaryModel = salaryRepository.findById(id);
        return ApiResponse.success(ApiResponseCode.SUCCESS, salaryModel.orElse(null));
    }
    //執行統計彙整的動作
    public List<SalaryModel> execCalculate(){
        LocalDate now = LocalDate.now();
        List<SalaryModel> roots = salaryRepository.findByRoot();
        List<SalaryModel> newList = new ArrayList<>();
        for (SalaryModel root : roots) {
            root.setId(null);
            root.setTime(now);
            root.setRoot(false);
            newList.add(root);
        }
        return salaryRepository.saveAll(newList);
    }
}
