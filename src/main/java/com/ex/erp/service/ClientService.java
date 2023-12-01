package com.ex.erp.service;

import com.ex.erp.dto.request.LoginRequest;
import com.ex.erp.dto.request.RegisterRequest;
import com.ex.erp.dto.request.ResetPasswordRequest;
import com.ex.erp.dto.response.ApiResponse;
import com.ex.erp.dto.response.ApiResponseCode;
import com.ex.erp.dto.response.ClientResponseModel;
import com.ex.erp.model.ClientModel;
import com.ex.erp.model.mail.ResetPasswordModel;
import com.ex.erp.repository.ClientRepository;
import com.ex.erp.service.security.TokenService;
import com.ex.erp.tool.EncodeTool;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClientService {
    private ClientRepository clientRepository;
    private EncodeTool encodeTool;
    private TokenService tokenService;
    private MailService mailService;
    private ResetPasswordModel resetPasswordModel;
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
    @Autowired
    public void setMailService(MailService mailService){
        this.mailService = mailService;
    }
    @Autowired
    public void setResetPasswordModel(ResetPasswordModel resetPasswordModel){
        this.resetPasswordModel = resetPasswordModel;
    }

    public ResponseEntity<ApiResponse> register(RegisterRequest dto) {
        // 檢查使用者資料庫參數
        ApiResponseCode code = verifyRegistration(dto);
        if(code != null) return ApiResponse.error(code);

        dto.setPassword(passwordEncode(dto.getPassword()));
        clientRepository.save(dto.toModel());

        return ApiResponse.success(ApiResponseCode.REGISTER_SUCCESS);
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

    public ResponseEntity<ApiResponse> resetPassword(ResetPasswordRequest resetRequest) throws MessagingException {
        ApiResponseCode code = checkResetPassword(resetRequest);
        if(code != null) return ApiResponse.error(code);

        String password = encodeTool.randomPassword(10);
        Context context = mailService.createContext(password);
        int result = updatePassword(passwordEncode(password), resetRequest);

        if(result == 1) {
            //更新成功才發送郵件
            mailService.sendMail(resetRequest.getEmail(), resetPasswordModel, context);
            return ApiResponse.success(ApiResponseCode.RESET_PASSWORD_SUCCESS);
        }
        return ApiResponse.error(ApiResponseCode.RESET_PASSWORD_FAILED);
    }

    public int updatePassword(String password, ResetPasswordRequest resetRequest){
        return clientRepository.updatePasswordByClient(password, resetRequest);
    }

    private String passwordEncode(String password){
        return encodeTool.passwordEncode(password);
    }

    private boolean isEmailExists(String email) {
        return clientRepository.existsByEmail(email);
    }

    private boolean isUsernameExists(String username) {
        return clientRepository.existsByUsername(username);
    }

    private ApiResponseCode verifyRegistration(RegisterRequest dto) {
        // 檢查使用者名稱是否已存在
        if (isUsernameExists(dto.getUsername())) {
            return ApiResponseCode.USERNAME_ALREADY_EXIST;
        }// 檢查使用者Email是否已存在
        if (isEmailExists(dto.getEmail())) {
            return ApiResponseCode.EMAIL_ALREADY_EXIST;
        }
        return null;
    }

    private ApiResponseCode checkResetPassword(ResetPasswordRequest resetRequest) {
        // 檢查使用者名稱是否已存在
        if (!isUsernameExists(resetRequest.getUsername())) {
            return ApiResponseCode.USERNAME_NOT_EXIST;
        }// 檢查使用者Email是否已存在
        if (!isEmailExists(resetRequest.getEmail())) {
            return ApiResponseCode.UNKNOWN_EMAIL;
        }
        return null;
    }

    public ResponseEntity<ApiResponse> findByUserId(long id) {
        Optional<ClientModel> modelOption = clientRepository.findById(id);
        return modelOption.map(ApiResponse::success).orElseGet(() -> ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, null));
    }
}
