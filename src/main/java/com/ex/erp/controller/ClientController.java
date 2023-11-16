package com.ex.erp.controller;

import com.ex.erp.config.jwt.TokenService;
import com.ex.erp.dto.ClientRegisterDto;
import com.ex.erp.dto.LoginRequest;
import com.ex.erp.dto.LoginResponse;
import com.ex.erp.model.ClientModel;
import com.ex.erp.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request){
        ResponseEntity<LoginResponse> response;
        try{
            LoginResponse token = tokenService.createToken(request);
            response = ResponseEntity.ok(token);
        }catch (Exception e){
            response = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse(null, null, "Authentication Failed"));
        }
        return response;
    }

    @GetMapping("/list")
    public List<ClientModel> clientList(){
        return service.list();
    }
}
