package com.ex.erp.controller;

import com.ex.erp.service.security.TokenService;
import com.ex.erp.dto.request.ClientRegisterDto;
import com.ex.erp.dto.request.LoginRequest;
import com.ex.erp.dto.response.LoginResponse;
import com.ex.erp.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/client")
public class ClientController {
    private final ClientService service;
    private final TokenService tokenService;

    @Autowired
    public ClientController(ClientService service, TokenService tokenService) {
        this.service = service;
        this.tokenService = tokenService;
    }

    @GetMapping("/opValid")
    public String opValid(){
        return "ok";
    }

    @PostMapping("/register")
    public void register(@RequestBody ClientRegisterDto dto){
        service.register(dto);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequest request){
        LoginResponse token = tokenService.createToken(request);
        return ResponseEntity.ok(token);
    }

    @GetMapping("/list")
    public ResponseEntity<Object> clientList(){
        return ResponseEntity.ok(service.list());
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<Object> refreshToken(@RequestBody Map<String, String> request){
        String refreshToken = request.get("refreshToken");
        String accessToken = tokenService.refreshAccessToken(refreshToken);
        Map<String, String> token = Map.of(accessToken, refreshToken);
        return ResponseEntity.ok(token);
    }
}
