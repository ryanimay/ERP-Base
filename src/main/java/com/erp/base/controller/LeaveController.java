package com.erp.base.controller;

import com.erp.base.aspect.Loggable;
import com.erp.base.model.constant.LeaveConstant;
import com.erp.base.model.constant.response.ApiResponseCode;
import com.erp.base.model.dto.request.PageRequestParam;
import com.erp.base.model.dto.request.leave.LeaveAcceptRequest;
import com.erp.base.model.dto.request.leave.LeaveRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.service.LeaveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "LeaveController", description = "休假相關API")
public class LeaveController {
    private LeaveService leaveService;
    @Autowired
    public void setLeaveService(LeaveService leaveService){
        this.leaveService = leaveService;
    }
    //主管權限搜待審核
    @GetMapping(Router.LEAVE.PENDING_LIST)
    @Operation(summary = "待審核休假清單")
    public ResponseEntity<ApiResponse> pendingList(@Parameter(description = "假單請求") PageRequestParam page){
        return leaveService.pendingList(page);
    }

    @GetMapping(Router.LEAVE.LIST)
    @Operation(summary = "休假清單")
    public ResponseEntity<ApiResponse> list(@Parameter(description = "假單請求") LeaveRequest request){
        return leaveService.list(request);
    }
    @Loggable
    @PostMapping(Router.LEAVE.ADD)
    @Operation(summary = "新增假單")
    public ResponseEntity<ApiResponse> add(@RequestBody LeaveRequest leaveRequest){
        return leaveService.add(leaveRequest);
    }
    @Loggable
    @PutMapping(Router.LEAVE.UPDATE)
    @Operation(summary = "更新假單")
    public ResponseEntity<ApiResponse> update(@RequestBody LeaveRequest addLeaveRequest){
        return leaveService.update(addLeaveRequest);
    }
    @Loggable
    @DeleteMapping(Router.LEAVE.DELETE)
    @Operation(summary = "刪除假單")
    public ResponseEntity<ApiResponse> delete(@Parameter(description = "假單ID") Long id){
        return leaveService.delete(id);
    }
    @Loggable
    @PutMapping(Router.LEAVE.ACCEPT)
    @Operation(summary = "審核假單")
    public ResponseEntity<ApiResponse> accept(@Parameter(description = "審核假單請求") @RequestBody LeaveAcceptRequest request){
        return leaveService.accept(request);
    }
    @Loggable
    @PutMapping(Router.LEAVE.REJECT)
    @Operation(summary = "駁回假單")
    public ResponseEntity<ApiResponse> reject(@Parameter(description = "駁回假單請求") @RequestBody LeaveAcceptRequest request){
        return leaveService.reject(request);
    }

    @GetMapping(Router.LEAVE.TYPE_LIST)
    @Operation(summary = "請假類別清單")
    public ResponseEntity<ApiResponse> enumList(){
        return ApiResponse.success(ApiResponseCode.SUCCESS, LeaveConstant.list());
    }
}
