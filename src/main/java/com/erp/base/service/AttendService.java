package com.erp.base.service;

import com.erp.base.model.constant.response.ApiResponseCode;
import com.erp.base.model.ClientIdentity;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.dto.response.ClientResponseModel;
import com.erp.base.model.entity.AttendModel;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.repository.AttendRepository;
import com.erp.base.tool.DateTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
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
    private ClientService clientService;
    @Autowired
    public void setClientService(ClientService clientService){
        this.clientService = clientService;
    }
    @Autowired
    public void setAttendRepository(AttendRepository attendRepository){
        this.attendRepository = attendRepository;
    }

    public ResponseEntity<ApiResponse> signIn() throws IncorrectResultSizeDataAccessException {
        return sign(1);
    }

    public ResponseEntity<ApiResponse> signOut() throws IncorrectResultSizeDataAccessException {
        return sign(2);
    }

    private ResponseEntity<ApiResponse> sign(int type){
        ClientModel user = ClientIdentity.getUser();
        if(user == null) return ApiResponse.error(ApiResponseCode.USER_NOT_FOUND);
        LocalDate nowDate = LocalDate.now();
        LocalDateTime nowTime = DateTool.now();
        int count = 0;
        int status = 1;
        switch(type){
            case 1 -> {
                count = attendRepository.signIn(user.getId(), nowDate, nowTime);
                status = 2;
            }
            case 2 -> {
                count = attendRepository.signOut(user.getId(), nowDate, nowTime);
                status = 3;
            }
        }
        if(count != 1) throw new IncorrectResultSizeDataAccessException(1, count);
        ClientModel clientModel = clientService.updateClientAttendStatus(user, status);

        return ApiResponse.success(ApiResponseCode.SUCCESS, new ClientResponseModel(clientModel));
    }

    public void saveAll(List<AttendModel> attends) {
        attendRepository.saveAll(attends);
    }
}
