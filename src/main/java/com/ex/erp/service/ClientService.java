package com.ex.erp.service;

import com.ex.erp.dto.request.RegisterRequest;
import com.ex.erp.dto.request.LoginRequest;
import com.ex.erp.dto.response.ClientResponseModel;
import com.ex.erp.model.ClientModel;
import com.ex.erp.repository.ClientRepository;
import com.ex.erp.service.security.TokenService;
import com.ex.erp.tool.EncodeTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ClientService {
    private ClientRepository clientRepository;
    private EncodeTool encodeTool;
    private TokenService tokenService;

    @Autowired
    public void setRepository(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }
    @Autowired
    public void setEncodeTool(EncodeTool encodeTool) {
        this.encodeTool = encodeTool;
    }
    @Autowired
    public void setTokenService(TokenService tokenService){
        this.tokenService = tokenService;
    }
    public void register(RegisterRequest dto) {
        dto.setPassword(passwordEncode(dto.getPassword()));
        clientRepository.save(dto.toModel());
    }

    public HttpHeaders login(LoginRequest request){
        return tokenService.createToken(request);
    }

    public List<ClientResponseModel> list() {
        List<ClientModel> allClient = clientRepository.findAll();
        return allClient.stream().map(ClientResponseModel::new).toList();
    }

    public ClientModel findByUsername(String username) {
        return clientRepository.findByUsername(username);
    }

    private String passwordEncode(String password){
        return encodeTool.passwordEncode(password);
    }

    public boolean isUsernameExists(String username) {
        return clientRepository.existsByUsername(username);
    }
}
