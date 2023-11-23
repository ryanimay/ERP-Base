package com.ex.erp.controller;

import com.ex.erp.dto.request.ClientRegisterDto;
import com.ex.erp.dto.request.LoginRequest;
import com.ex.erp.dto.response.ClientResponse;
import com.ex.erp.service.ClientService;
import com.ex.erp.service.cache.ClientCache;
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
    public String opValid(){
        return "ok";
    }

    @PostMapping("/register")
    public void register(@RequestBody ClientRegisterDto dto){
        clientService.register(dto);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequest request){
        HttpHeaders token = clientService.login(request);
        ClientResponse client = clientCache.getClient(request.getUsername());
        return ResponseEntity.ok().headers(token).body(client);
    }

    @GetMapping("/list")
    public ResponseEntity<Object> clientList(){
        return ResponseEntity.ok(clientService.list());
    }

//    @PostMapping("/refreshToken")
//    public ResponseEntity<Object> refreshToken(@RequestBody Map<String, String> request){
//        String refreshToken = request.get("refreshToken");
//        String accessToken = tokenService.refreshAccessToken(refreshToken);
//        Map<String, String> token = Map.of(accessToken, refreshToken);
//        return ResponseEntity.ok(token);
//    }
}
