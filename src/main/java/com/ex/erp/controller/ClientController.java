package com.ex.erp.controller;

import com.ex.erp.dto.request.client.*;
import com.ex.erp.dto.response.ApiResponse;
import com.ex.erp.service.ClientService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/client")
public class ClientController {
    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService service) {
        this.clientService = service;
    }

    @GetMapping("/opValid")
    public ResponseEntity<ApiResponse> opValid(){
        return ApiResponse.success("OK");
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody @Valid RegisterRequest request){
        return clientService.register(request);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody @Valid LoginRequest request){
        return clientService.login(request);
    }

    @PutMapping("/resetPassword")
    public ResponseEntity<ApiResponse> resetPassword(@RequestBody @Valid ResetPasswordRequest resetRequest) throws MessagingException {
        return clientService.resetPassword(resetRequest);
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse> clientList(){
        return ApiResponse.success(clientService.list());
    }

    @GetMapping("/getClient")
    public ResponseEntity<ApiResponse> getClient(long id){
        return clientService.findByUserId(id);
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse> updateClient(@RequestBody @Valid UpdateClientInfoRequest request){
        return clientService.updateUser(request);
    }

    @PutMapping("/updatePassword")
    public ResponseEntity<ApiResponse> updatePassword(@RequestBody @Valid UpdatePasswordRequest request){
        return clientService.updatePassword(request);
    }

    @PutMapping("/lockClient")
    public ResponseEntity<ApiResponse> lockClient(@RequestBody LockClientRequest request){
        return clientService.lockClient(request);
    }
}
