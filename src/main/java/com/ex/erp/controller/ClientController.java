package com.ex.erp.controller;

import com.ex.erp.dto.request.ClientRegisterDto;
import com.ex.erp.dto.request.LoginRequest;
import com.ex.erp.dto.security.ClientIdentity;
import com.ex.erp.service.ClientService;
import com.ex.erp.service.cache.ClientCache;
import com.ex.erp.service.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/client")
public class ClientController {
    private final ClientService service;
    private final TokenService tokenService;
    private final ClientCache clientCache;

    @Autowired
    public ClientController(ClientService service, TokenService tokenService, ClientCache clientCache) {
        this.service = service;
        this.tokenService = tokenService;
        this.clientCache = clientCache;
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
        HttpHeaders token = tokenService.createToken(request);
        return ResponseEntity.ok().headers(token).body(clientCache.getClient(request.getUsername()));
    }

    @GetMapping("/list")
    public ResponseEntity<Object> clientList(){
        return ResponseEntity.ok(service.list());
    }

    @GetMapping("/get")
    public ResponseEntity<Object> getuser(){
        return ResponseEntity.ok(ClientIdentity.getUser());
    }

//    @PostMapping("/refreshToken")
//    public ResponseEntity<Object> refreshToken(@RequestBody Map<String, String> request){
//        String refreshToken = request.get("refreshToken");
//        String accessToken = tokenService.refreshAccessToken(refreshToken);
//        Map<String, String> token = Map.of(accessToken, refreshToken);
//        return ResponseEntity.ok(token);
//    }
}
