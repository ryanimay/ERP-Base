package com.erp.base.controller;

import com.erp.base.aspect.Loggable;
import com.erp.base.model.constant.response.ApiResponseCode;
import com.erp.base.model.dto.request.client.*;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.service.ClientService;
import com.erp.base.service.security.TokenService;
import com.erp.base.tool.LogFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "ClientController", description = "用戶相關API")
public class ClientController {
    LogFactory LOG = new LogFactory(ClientController.class);
    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService service) {
        this.clientService = service;
    }

    @GetMapping(Router.CLIENT.OP_VALID)
    @Operation(summary = "測試接口")
    public ResponseEntity<ApiResponse> opValid(){
        return ApiResponse.success(ApiResponseCode.SUCCESS);
    }

    @PostMapping(Router.CLIENT.REGISTER)
    @Operation(summary = "註冊")
    public ResponseEntity<ApiResponse> register(@Parameter(description = "用戶註冊請求") @RequestBody @Valid RegisterRequest request){
        return clientService.register(request);
    }

    @PostMapping(Router.CLIENT.LOGIN)
    @Operation(summary = "登入")
    public ResponseEntity<ApiResponse> login(@Parameter(description = "用戶登入請求") @RequestBody @Valid LoginRequest request){
        return clientService.login(request);
    }

    @PutMapping(Router.CLIENT.RESET_PASSWORD)
    @Operation(summary = "重設密碼")
    public ResponseEntity<ApiResponse> resetPassword(@Parameter(description = "重設密碼請求") @RequestBody @Valid ResetPasswordRequest resetRequest) {
        ResponseEntity<ApiResponse> response;
        try{
            response = clientService.resetPassword(resetRequest);
        } catch (MessagingException e) {
            LOG.error(e.getMessage());
            response = ApiResponse.error(ApiResponseCode.MESSAGING_ERROR);
        }
        return response;
    }

    @GetMapping(Router.CLIENT.LIST)
    @Operation(summary = "用戶清單")
    public ResponseEntity<ApiResponse> clientList(@Parameter(description = "用戶清單請求") @ModelAttribute ClientListRequest param){
        return ApiResponse.success(clientService.list(param));
    }

    @GetMapping(Router.CLIENT.GET_CLIENT)
    @Operation(summary = "搜尋單一用戶")
    public ResponseEntity<ApiResponse> getClient(@Parameter(description = "用戶ID") long id){
        return clientService.findByUserId(id);
    }

    @PutMapping(Router.CLIENT.UPDATE)
    @Operation(summary = "更新用戶")
    public ResponseEntity<ApiResponse> updateClient(@Parameter(description = "更新用戶請求") @RequestBody @Valid UpdateClientInfoRequest request){
        return clientService.updateUser(request);
    }

    @PutMapping(Router.CLIENT.UPDATE_PASSWORD)
    @Operation(summary = "更新密碼")
    public ResponseEntity<ApiResponse> updatePassword(@Parameter(description = "更新密碼請求") @RequestBody @Valid UpdatePasswordRequest request){
        ResponseEntity<ApiResponse> response;
        try{
            response = clientService.updatePassword(request);
        } catch (IncorrectResultSizeDataAccessException e) {
            LOG.error(e.getMessage());
            response = ApiResponse.error(ApiResponseCode.RESET_PASSWORD_FAILED);
        }
        return response;
    }
    @Loggable
    @PutMapping(Router.CLIENT.CLIENT_LOCK)
    @Operation(summary = "用戶鎖定/解鎖")
    public ResponseEntity<ApiResponse> clientLock(@Parameter(description = "用戶狀態請求") @RequestBody ClientStatusRequest request){
        ResponseEntity<ApiResponse> response;
        try{
            response = clientService.lockClient(request);
        } catch (IncorrectResultSizeDataAccessException e) {
            LOG.error(e.getMessage());
            response = ApiResponse.error(ApiResponseCode.UPDATE_ERROR);
        }
        return response;
    }

    @Loggable
    @PutMapping(Router.CLIENT.CLIENT_STATUS)
    @Operation(summary = "用戶啟用/停用")
    public ResponseEntity<ApiResponse> clientStatus(@Parameter(description = "用戶狀態請求") @RequestBody ClientStatusRequest request){
        ResponseEntity<ApiResponse> response;
        try{
            response = clientService.clientStatus(request);
        } catch (IncorrectResultSizeDataAccessException e) {
            LOG.error(e.getMessage());
            response = ApiResponse.error(ApiResponseCode.UPDATE_ERROR);
        }
        return response;
    }

    @GetMapping(Router.CLIENT.NAME_LIST)
    @Operation(summary = "用戶名清單")
    public ResponseEntity<ApiResponse> nameList(){
        return clientService.nameList();
    }

    @PostMapping(Router.CLIENT.LOGOUT)
    @Operation(summary = "登入")
    public ResponseEntity<ApiResponse> logout(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
            @RequestHeader(value = TokenService.REFRESH_TOKEN, required = false) String refreshToken
            ){
        return clientService.logout(accessToken, refreshToken);
    }
}
