package com.erp.base.service;

import com.erp.base.config.websocket.WebsocketConstant;
import com.erp.base.enums.NotificationEnum;
import com.erp.base.enums.response.ApiResponseCode;
import com.erp.base.model.ClientIdentity;
import com.erp.base.model.MessageModel;
import com.erp.base.model.dto.request.client.*;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.dto.response.ClientNameObject;
import com.erp.base.model.dto.response.ClientResponseModel;
import com.erp.base.model.dto.response.PageResponse;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.entity.NotificationModel;
import com.erp.base.model.entity.RoleModel;
import com.erp.base.model.mail.ResetPasswordModel;
import com.erp.base.repository.ClientRepository;
import com.erp.base.service.cache.ClientCache;
import com.erp.base.service.security.TokenService;
import com.erp.base.tool.EncodeTool;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClientService {
    private ClientRepository clientRepository;
    private EncodeTool encodeTool;
    private TokenService tokenService;
    private MailService mailService;
    private ResetPasswordModel resetPasswordModel;
    private ClientCache clientCache;
    private MessageService messageService;
    private NotificationService notificationService;
    private static final String RESET_PREFIX = "##";

    @Autowired
    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Autowired
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @Autowired
    public void setClientCache(ClientCache clientCache) {
        this.clientCache = clientCache;
    }

    @Autowired
    public void setRepository(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Autowired
    public void setEncodeTool(EncodeTool encodeTool) {
        this.encodeTool = encodeTool;
    }

    @Autowired
    public void setTokenService(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Autowired
    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    @Autowired
    public void setResetPasswordModel(ResetPasswordModel resetPasswordModel) {
        this.resetPasswordModel = resetPasswordModel;
    }

    public ResponseEntity<ApiResponse> register(RegisterRequest dto) {
        // 檢查使用者資料庫參數
        ApiResponseCode code = verifyRegistration(dto);
        if (code != null) return ApiResponse.error(code);

        ClientModel entity = dto.toModel();
        entity.setPassword(passwordEncode(entity.getPassword()));
        clientRepository.save(entity);

        return ApiResponse.success(ApiResponseCode.REGISTER_SUCCESS);
    }

    public ResponseEntity<ApiResponse> login(LoginRequest request) {
        HttpHeaders token = tokenService.createToken(request);
        ClientModel user = clientCache.getClient(request.getUsername());
        ClientResponseModel client = new ClientResponseModel(user);
        updateLastLoginTime(user);
        return ApiResponse.success(token, client);
    }

    private void updateLastLoginTime(ClientModel user) {
        user.setLastLoginTime(LocalDateTime.now());
        clientRepository.save(user);
        clientCache.refreshClient(user.getUsername());
    }

    public PageResponse<ClientResponseModel> list(ClientListRequest param) {
        Page<ClientModel> allClient = null;
        if (param.getId() == null && param.getName() == null) {
            allClient = clientRepository.findAll(param.getPage());
        } else {
            if (param.getType() == 1) {
                allClient = clientRepository.findByIdContaining(param.getId(), param.getPage());
            } else if (param.getType() == 2) {
                allClient = clientRepository.findByUsernameContaining(param.getName(), param.getPage());
            }
        }
        assert allClient != null;
        return new PageResponse<>(allClient, ClientResponseModel.class);
    }

    public ClientModel findByUsername(String username) {
        return clientRepository.findByUsername(username);
    }

    public ResponseEntity<ApiResponse> resetPassword(ResetPasswordRequest resetRequest) throws MessagingException {
        ApiResponseCode code = checkResetPassword(resetRequest);
        if (code != null) return ApiResponse.error(code);

        String username = resetRequest.getUsername();
        String password = RESET_PREFIX + encodeTool.randomPassword(18);

        Context context = mailService.createContext(username, password);
        int result = updatePassword(passwordEncode(password), true, username, resetRequest.getEmail());

        if (result == 1) {
            //更新成功才發送郵件
            mailService.sendMail(resetRequest.getEmail(), resetPasswordModel, context, null);
            return ApiResponse.success(ApiResponseCode.RESET_PASSWORD_SUCCESS);
        }
        return ApiResponse.error(ApiResponseCode.RESET_PASSWORD_FAILED);
    }

    public ResponseEntity<ApiResponse> updatePassword(UpdatePasswordRequest request) {
        ClientModel client = ClientIdentity.getUser();
        if (checkIdentity(client.getUsername(), request)) return ApiResponse.error(ApiResponseCode.IDENTITY_ERROR);
        if (checkOldPassword(client.getUsername(), request.getOldPassword())) return ApiResponse.error(ApiResponseCode.INVALID_LOGIN);

        int result = updatePassword(passwordEncode(request.getPassword()), false, client.getUsername(), client.getEmail());
        if (result == 1) {
            return ApiResponse.success(ApiResponseCode.UPDATE_PASSWORD_SUCCESS);
        }
        return ApiResponse.error(ApiResponseCode.RESET_PASSWORD_FAILED);
    }

    /**
     * 比對舊帳密
     * */
    private boolean checkOldPassword(String username, String oldPassword) {
        ClientModel originModel = clientRepository.findByUsername(username);
        return !encodeTool.match(oldPassword, originModel.getPassword());
    }

    /**
     * 不是本人拒絕更改
     * */
    private boolean checkIdentity(String username, UpdatePasswordRequest request) {
        return !Objects.equals(username, request.getUsername());
    }

    private int updatePassword(String password, boolean status, String username, String email) {
        return clientRepository.updatePasswordByClient(password, status, username, email);
    }

    private String passwordEncode(String password) {
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
        }
        return null;
    }

    private ApiResponseCode checkResetPassword(ResetPasswordRequest resetRequest) {
        // 檢查使用者名稱是否已存在
        if (!isUsernameExists(resetRequest.getUsername())) {
            return ApiResponseCode.USER_NOT_FOUND;
        }// 檢查使用者Email是否已存在
        if (!isEmailExists(resetRequest.getEmail())) {
            return ApiResponseCode.UNKNOWN_EMAIL;
        }
        return null;
    }

    public ResponseEntity<ApiResponse> findByUserId(long id) {
        Optional<ClientModel> modelOption = clientRepository.findById(id);
        return modelOption.map(model -> ApiResponse.success(new ClientResponseModel(model))).orElseGet(() -> ApiResponse.error(ApiResponseCode.USER_NOT_FOUND));
    }

    public ResponseEntity<ApiResponse> updateUser(UpdateClientInfoRequest request) {
        ClientModel client = clientCache.getClient(request.getUsername());
        if (client == null) throw new UsernameNotFoundException("User Not Found");
        String newMail = request.getEmail();
        if (newMail != null && !newMail.equals(client.getEmail())) {
            client.setEmail(newMail);
            client.setMustUpdatePassword(false);
        }
        if (request.getRoles() != null) client.setRoles(getRoles(request.getRoles()));
        ClientModel save = clientRepository.save(client);
        clientCache.refreshClient(client.getUsername());
        //非本人就發送通知
        checkUserOrSendMessage(client);
        return ApiResponse.success(ApiResponseCode.SUCCESS, new ClientResponseModel(save));
    }

    private void checkUserOrSendMessage(ClientModel client) {
        ClientModel user = ClientIdentity.getUser();
        if (user.getId() != client.getId()) {
            NotificationModel notification = notificationService.createNotification(NotificationEnum.UPDATE_USER, user.getUsername());
            MessageModel messageModel = new MessageModel(user.getUsername(), Long.toString(client.getId()), WebsocketConstant.TOPIC.NOTIFICATION, notification);
            messageService.sendTo(messageModel);
        }
    }

    private Set<RoleModel> getRoles(List<Long> roles) {
        return roles.stream().map(RoleModel::new).collect(Collectors.toSet());
    }

    public ResponseEntity<ApiResponse> lockClient(ClientStatusRequest request) {
        String username = request.getUsername();
        int count = clientRepository.lockClientByIdAndUsername(request.getClientId(), username, request.isStatus());
        if(count != 1) return ApiResponse.error(HttpStatus.BAD_REQUEST, "Update Failed");
        clientCache.refreshClient(username);
        return ApiResponse.success(ApiResponseCode.SUCCESS);
    }

    public ResponseEntity<ApiResponse> clientStatus(ClientStatusRequest request) {
        String username = request.getUsername();
        int count = clientRepository.switchClientStatusByIdAndUsername(request.getClientId(), username, request.isStatus());

        if(count != 1) return ApiResponse.error(HttpStatus.BAD_REQUEST, "Update Failed");
        clientCache.refreshClient(username);
        return ApiResponse.success(ApiResponseCode.SUCCESS);
    }

    public Set<ClientModel> findActiveUserAndNotExistAttend() {
        return clientRepository.findActiveUserAndNotExistAttend(LocalDate.now());
    }

    public Set<Long> findByHasAcceptPermission(String router) {
        return clientRepository.findByHasAcceptPermission(router);
    }

    public ResponseEntity<ApiResponse> nameList() {
        List<ClientNameObject> clientNameList = clientCache.getClientNameList();
        return ApiResponse.success(ApiResponseCode.SUCCESS, clientNameList);
    }

    public List<ClientNameObject> getClientNameList() {
        List<Object[]> allNameAndId = clientRepository.findAllNameAndId();
        return allNameAndId.stream().map(ClientNameObject::new).toList();
    }

    public String findNameByUserId(long id) {
        return clientRepository.findUsernameById(id);
    }
}
