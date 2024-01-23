package com.erp.base.controller;

import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.service.AttendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AttendController {
    private AttendService attendService;
    @Autowired
    public void setAttendService(AttendService attendService){
        this.attendService = attendService;
    }
    @PutMapping(Router.ATTEND.SIGN_IN)
    public ResponseEntity<ApiResponse> signIn(){
        return attendService.signIn();
    }

    @PutMapping(Router.ATTEND.SIGN_OUT)
    public ResponseEntity<ApiResponse> signOut(){
        return attendService.signOut();
    }
}
