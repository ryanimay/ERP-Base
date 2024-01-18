package com.erp.base.controller;

import com.erp.base.model.dto.request.PageRequestParam;
import com.erp.base.model.dto.request.leave.AddLeaveRequest;
import com.erp.base.model.dto.request.leave.UpdateLeaveRequest;
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

    @GetMapping(Router.LEAVE.PENDING_LIST)
    public ResponseEntity<ApiResponse> pendingList(PageRequestParam page){
        return leaveService.pendingList(page);
    }

    @GetMapping(Router.LEAVE.LIST)
    public ResponseEntity<ApiResponse> list(PageRequestParam page){
        return leaveService.list(page);
    }

    @PostMapping(Router.LEAVE.ADD)
    public ResponseEntity<ApiResponse> add(@RequestBody AddLeaveRequest addLeaveRequest){
        return leaveService.add(addLeaveRequest);
    }

    @PutMapping(Router.LEAVE.UPDATE)
    public ResponseEntity<ApiResponse> update(@RequestBody UpdateLeaveRequest addLeaveRequest){
        return leaveService.update(addLeaveRequest);
    }

    @DeleteMapping(Router.LEAVE.DELETE)
    public ResponseEntity<ApiResponse> delete(@RequestBody Long id){
        return leaveService.delete(id);
    }

    @PostMapping(Router.LEAVE.ACCEPT)
    public ResponseEntity<ApiResponse> accept(@RequestBody Long id, Long eventUserId){
        return leaveService.accept(id, eventUserId);
    }
}
