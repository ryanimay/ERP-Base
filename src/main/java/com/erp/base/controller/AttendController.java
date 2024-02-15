package com.erp.base.controller;

import com.erp.base.aspect.Loggable;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.service.AttendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "AttendController", description = "簽到相關API")
public class AttendController {
    private AttendService attendService;
    @Autowired
    public void setAttendService(AttendService attendService){
        this.attendService = attendService;
    }
    @Loggable
    @PutMapping(Router.ATTEND.SIGN_IN)
    @Operation(summary = "簽到")
    public ResponseEntity<ApiResponse> signIn(){
        return attendService.signIn();
    }
    @Loggable
    @PutMapping(Router.ATTEND.SIGN_OUT)
    @Operation(summary = "簽退")
    public ResponseEntity<ApiResponse> signOut(){
        return attendService.signOut();
    }
}
