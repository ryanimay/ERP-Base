package com.erp.base.controller;

import com.erp.base.enums.LeaveConstant;
import com.erp.base.enums.response.ApiResponseCode;
import com.erp.base.model.dto.request.PageRequestParam;
import com.erp.base.model.dto.request.leave.LeaveAcceptRequest;
import com.erp.base.model.dto.request.leave.LeaveRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.service.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class LeaveController {
    private LeaveService leaveService;
    @Autowired
    public void setLeaveService(LeaveService leaveService){
        this.leaveService = leaveService;
    }
    //主管權限搜待審核
    @GetMapping(Router.LEAVE.PENDING_LIST)
    public ResponseEntity<ApiResponse> pendingList(PageRequestParam page){
        return leaveService.pendingList(page);
    }
    //搜本人假單
    @GetMapping(Router.LEAVE.LIST)
    public ResponseEntity<ApiResponse> list(PageRequestParam page){
        return leaveService.list(page);
    }

    @PostMapping(Router.LEAVE.ADD)
    public ResponseEntity<ApiResponse> add(@RequestBody LeaveRequest leaveRequest){
        return leaveService.add(leaveRequest);
    }

    @PutMapping(Router.LEAVE.UPDATE)
    public ResponseEntity<ApiResponse> update(@RequestBody LeaveRequest addLeaveRequest){
        return leaveService.update(addLeaveRequest);
    }

    @DeleteMapping(Router.LEAVE.DELETE)
    public ResponseEntity<ApiResponse> delete(Long id){
        return leaveService.delete(id);
    }

    @PostMapping(Router.LEAVE.ACCEPT)
    public ResponseEntity<ApiResponse> accept(@RequestBody LeaveAcceptRequest request){
        return leaveService.accept(request);
    }

    @GetMapping(Router.LEAVE.TYPE_LIST)
    public ResponseEntity<ApiResponse> enumList(){
        return ApiResponse.success(ApiResponseCode.SUCCESS, LeaveConstant.list());
    }
}
