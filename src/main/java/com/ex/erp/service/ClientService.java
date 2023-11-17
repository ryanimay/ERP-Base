package com.ex.erp.service;

import com.ex.erp.dto.request.ClientRegisterDto;
import com.ex.erp.model.ClientModel;
import com.ex.erp.repository.ClientRepository;
import com.ex.erp.tool.EncodeTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientService {
    private ClientRepository clientRepository;
    private EncodeTool encodeTool;

    @Autowired
    public void setRepository(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }
    @Autowired
    public void setEncodeTool(EncodeTool encodeTool) {
        this.encodeTool = encodeTool;
    }
    public void register(ClientRegisterDto dto) {
        String password = encodeTool.passwordEncode(dto.getPassword());
        dto.setPassword(password);
        clientRepository.save(dto.toModel());
    }

    public ClientModel login() {
        return null;
    }

    public List<ClientModel> list() {
        return clientRepository.findAll();
    }

    public ClientModel findByUsername(String username) {
        return clientRepository.findByUsername(username);
    }
}
