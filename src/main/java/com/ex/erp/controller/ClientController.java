package com.ex.erp.controller;

import com.ex.erp.dto.request.LoginRequest;
import com.ex.erp.dto.request.RegisterRequest;
import com.ex.erp.dto.request.ResetPasswordRequest;
import com.ex.erp.dto.response.ApiResponse;
import com.ex.erp.dto.response.ClientResponseModel;
import com.ex.erp.service.ClientService;
import com.ex.erp.service.cache.ClientCache;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/client")
public class ClientController {
    private final ClientService clientService;
    private final ClientCache clientCache;

    @Autowired
    public ClientController(ClientService service, ClientCache clientCache) {
        this.clientService = service;
        this.clientCache = clientCache;
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
        HttpHeaders token = clientService.login(request);
        ClientResponseModel client = new ClientResponseModel(clientCache.getClient(request.getUsername()));
        return ApiResponse.success(token, client);
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

}
