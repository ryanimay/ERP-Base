package com.ex.erp.controller;

import com.ex.erp.config.jwt.TokenService;
import com.ex.erp.dto.ClientRegisterDto;
import com.ex.erp.dto.LoginRequest;
import com.ex.erp.dto.LoginResponse;
import com.ex.erp.model.ClientModel;
import com.ex.erp.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        ResponseEntity<Object> response;
        LoginResponse token = tokenService.createToken(request);
        response = ResponseEntity.ok(token);
        return response;
    }

    @GetMapping("/list")
    public List<ClientModel> clientList(){
        return service.list();
    }
}
