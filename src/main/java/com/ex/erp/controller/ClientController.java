package com.ex.erp.controller;

import com.ex.erp.dto.ClientRegisterDto;
import com.ex.erp.model.ClientModel;
import com.ex.erp.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/client")
public class ClientController {
    private final ClientService service;

    @Autowired
    public ClientController(ClientService service) {
        this.service = service;
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
    public ClientModel login(){
        return service.login();
    }

    @GetMapping("/list")
    public List<ClientModel> clientList(){
        return service.list();
    }
}
