package com.erp.base.controller;

import com.erp.base.dto.request.PageRequestParam;
import com.erp.base.dto.request.client.*;
import com.erp.base.dto.response.ApiResponse;
import com.erp.base.enums.response.ApiResponseCode;
import com.erp.base.service.ClientService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ClientController {
    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService service) {
        this.clientService = service;
    }

    @GetMapping(Router.CLIENT.OP_VALID)
    public ResponseEntity<ApiResponse> opValid(){
        return ApiResponse.success(ApiResponseCode.SUCCESS);
    }

    @PostMapping(Router.CLIENT.REGISTER)
    public ResponseEntity<ApiResponse> register(@RequestBody @Valid RegisterRequest request){
        return clientService.register(request);
    }

    @PostMapping(Router.CLIENT.LOGIN)
    public ResponseEntity<ApiResponse> login(@RequestBody @Valid LoginRequest request){
        return clientService.login(request);
    }

    @PutMapping(Router.CLIENT.RESET_PASSWORD)
    public ResponseEntity<ApiResponse> resetPassword(@RequestBody @Valid ResetPasswordRequest resetRequest) throws MessagingException {
        return clientService.resetPassword(resetRequest);
    }

    @GetMapping(Router.CLIENT.LIST)
    public ResponseEntity<ApiResponse> clientList(@ModelAttribute PageRequestParam param){
        return ApiResponse.success(clientService.list(param));
    }

    @GetMapping(Router.CLIENT.GET_CLIENT)
    public ResponseEntity<ApiResponse> getClient(long id){
        return clientService.findByUserId(id);
    }

    @PutMapping(Router.CLIENT.UPDATE)
    public ResponseEntity<ApiResponse> updateClient(@RequestBody @Valid UpdateClientInfoRequest request){
        return clientService.updateUser(request);
    }

    @PutMapping(Router.CLIENT.UPDATE_PASSWORD)
    public ResponseEntity<ApiResponse> updatePassword(@RequestBody @Valid UpdatePasswordRequest request){
        return clientService.updatePassword(request);
    }

    @PutMapping(Router.CLIENT.CLIENT_LOCK)
    public ResponseEntity<ApiResponse> clientLock(@RequestBody ClientStatusRequest request){
        return clientService.lockClient(request);
    }

    @PutMapping(Router.CLIENT.CLIENT_STATUS)
    public ResponseEntity<ApiResponse> clientStatus(@RequestBody ClientStatusRequest request){
        return clientService.clientStatus(request);
    }
}
