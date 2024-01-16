package com.erp.base.service;

import com.erp.base.enums.response.ApiResponseCode;
import com.erp.base.model.ClientIdentity;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.entity.AttendModel;
import com.erp.base.model.entity.UserModel;
import com.erp.base.repository.AttendRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AttendService {
    private AttendRepository attendRepository;
    @Autowired
    public void setAttendRepository(AttendRepository attendRepository){
        this.attendRepository = attendRepository;
    }

    public ResponseEntity<ApiResponse> signIn() {
        UserModel user = ClientIdentity.getUser();
        if(user == null) return ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "UserNotFount");
        LocalDate nowDate = LocalDate.now();
        LocalDateTime nowTime = LocalDateTime.now();
        attendRepository.signIn(user.getId(), nowDate, nowTime);
        return ApiResponse.success(ApiResponseCode.SUCCESS);
    }

    public ResponseEntity<ApiResponse> signOut() {
        UserModel user = ClientIdentity.getUser();
        if(user == null) return ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "UserNotFount");
        LocalDate nowDate = LocalDate.now();
        LocalDateTime nowTime = LocalDateTime.now();
        attendRepository.signOut(user.getId(), nowDate, nowTime);
        return ApiResponse.success(ApiResponseCode.SUCCESS);
    }

    public void saveAll(List<AttendModel> attends) {
        attendRepository.saveAll(attends);
    }
}
