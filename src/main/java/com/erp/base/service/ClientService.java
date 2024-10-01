package com.erp.base.service;

import com.erp.base.config.websocket.WebsocketConstant;
import com.erp.base.model.ClientIdentity;
import com.erp.base.model.MessageModel;
import com.erp.base.model.constant.NotificationEnum;
import com.erp.base.model.constant.RoleConstant;
import com.erp.base.model.constant.cache.CacheConstant;
import com.erp.base.model.constant.response.ApiResponseCode;
import com.erp.base.model.dto.request.client.*;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.dto.response.ClientNameObject;
import com.erp.base.model.dto.response.ClientResponseModel;
import com.erp.base.model.dto.response.PageResponse;
import com.erp.base.model.dto.security.ClientIdentityDto;
import com.erp.base.model.entity.AttendModel;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.entity.NotificationModel;
import com.erp.base.model.entity.RoleModel;
import com.erp.base.model.mail.ResetPasswordModel;
import com.erp.base.repository.ClientRepository;
import com.erp.base.service.security.TokenService;
import com.erp.base.tool.DateTool;
import com.erp.base.tool.EncodeTool;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClientService {
    private ClientRepository clientRepository;
    private EncodeTool encodeTool;
    private TokenService tokenService;
    private MailService mailService;
    private ResetPasswordModel resetPasswordModel;
    private CacheService cacheService;
    private MessageService messageService;
    private NotificationService notificationService;
    private DepartmentService departmentService;
    private AttendService attendService;
    private PerformanceService performanceService;
    private static final String RESET_PREFIX = "##";
    @Autowired
    public void setPerformanceService(@Lazy PerformanceService performanceService){
        this.performanceService = performanceService;
    }

    @Autowired
    public void setAttendService(AttendService attendService) {
        this.attendService = attendService;
    }

    @Autowired
    public void setDepartmentService(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @Autowired
    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Autowired
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @Autowired
    public void setCacheService(CacheService cacheService) {
        this.cacheService = cacheService;
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
        //依部門設置註冊用戶的默認權限
        entity = departmentService.setDepartmentDefaultRole(entity, dto.getDepartmentId());
        entity.setPassword(passwordEncode(entity.getPassword()));
        clientRepository.save(entity);
        //新增空的簽到表
        addNewAttend(entity.getId());
        //刷新系統用戶緩存
        cacheService.refreshCache(CacheConstant.CLIENT.NAME_CLIENT + CacheConstant.SPLIT_CONSTANT + CacheConstant.CLIENT.SYSTEM_USER);
        return ApiResponse.success(ApiResponseCode.REGISTER_SUCCESS);
    }

    private void addNewAttend(long id) {
        AttendModel attendModel = new AttendModel(new ClientModel(id));
        attendService.save(attendModel);
    }

    public ResponseEntity<ApiResponse> login(LoginRequest request) {
        HttpHeaders token = tokenService.createToken(request);
        ClientModel user = findByUsername(request.getUsername());
        ClientResponseModel client = new ClientResponseModel(user);
        updateLastLoginTime(user);
        return ApiResponse.success(token, client);
    }

    private void updateLastLoginTime(ClientModel user) {
        user.setLastLoginTime(DateTool.now());
        clientRepository.save(user);
        cacheService.refreshCache(CacheConstant.CLIENT.NAME_CLIENT + CacheConstant.SPLIT_CONSTANT + CacheConstant.CLIENT.CLIENT + user.getId());
    }

    public PageResponse<ClientResponseModel> list(ClientListRequest param) {
        Page<ClientModel> allClient = null;
        if (param.getId() == null && param.getName() == null) {
            allClient = clientRepository.findAll(param.getPage());
        } else {
            if (param.getType() == 1) {
                allClient = clientRepository.findById(param.getId(), param.getPage());
            } else if (param.getType() == 2) {
                allClient = clientRepository.findByUsernameContaining(param.getName(), param.getPage());
            }
        }
        return new PageResponse<>(Objects.requireNonNull(allClient), ClientResponseModel.class);
    }

    public ClientModel findByUsername(String username) {
        return clientRepository.findByUsername(username);
    }

    public ClientModel findById(Long id) {
        return clientRepository.findById(id).orElse(null);
    }

    public ResponseEntity<ApiResponse> resetPassword(ResetPasswordRequest resetRequest) throws MessagingException {
        ApiResponseCode code = checkResetPassword(resetRequest);
        if (code != null) return ApiResponse.error(code);

        String username = resetRequest.getUsername();
        String password = RESET_PREFIX + encodeTool.randomPassword(18);

        int result = updatePassword(passwordEncode(password), true, username, resetRequest.getEmail(), null);

        if (result == 1) {
            //更新成功才發送郵件
            Context context = mailService.createContext(username, password);
            mailService.sendMail(resetRequest.getEmail(), resetPasswordModel, context, null);
            ClientModel newClient = findByUsername(username);
            cacheService.refreshCache(CacheConstant.CLIENT.NAME_CLIENT + CacheConstant.SPLIT_CONSTANT + CacheConstant.CLIENT.CLIENT + newClient.getId());
            return ApiResponse.success(ApiResponseCode.RESET_PASSWORD_SUCCESS);
        }
        return ApiResponse.error(ApiResponseCode.RESET_PASSWORD_FAILED);
    }

    public ResponseEntity<ApiResponse> updatePassword(UpdatePasswordRequest request) throws IncorrectResultSizeDataAccessException {
        ClientIdentityDto client = ClientIdentity.getUser();
        if (client == null || checkIdentity(client.getId(), request))
            return ApiResponse.error(ApiResponseCode.IDENTITY_ERROR);
        String username = client.getUsername();
        if (checkNotEqualsOldPassword(client.getId(), request.getOldPassword()))
            return ApiResponse.error(ApiResponseCode.INVALID_LOGIN);
        int result = updatePassword(passwordEncode(request.getPassword()), false, username, client.getEmail(), client.getId());
        //如果不為1代表更改有問題，拋出並回滾
        if (result != 1) throw new IncorrectResultSizeDataAccessException(1, result);
        cacheService.refreshCache(CacheConstant.CLIENT.NAME_CLIENT + CacheConstant.SPLIT_CONSTANT + CacheConstant.CLIENT.CLIENT + client.getId());
        return ApiResponse.success(ApiResponseCode.UPDATE_PASSWORD_SUCCESS);
    }

    /**
     * 比對舊帳密
     */
    private boolean checkNotEqualsOldPassword(long id, String oldPassword) {
        Optional<ClientModel> optionalModel = clientRepository.findById(id);
        return optionalModel.map(clientModel -> !encodeTool.match(oldPassword, clientModel.getPassword())).orElse(true);
    }

    /**
     * 不是本人拒絕更改
     */
    private boolean checkIdentity(long uid, UpdatePasswordRequest request) {
        return uid != request.getId();
    }

    private int updatePassword(String password, boolean status, String username, String email, Long id) {
        return clientRepository.updatePasswordByUsernameAndEmailAndId(password, status, username, email, id);
    }

    private String passwordEncode(String password) {
        return encodeTool.passwordEncode(password);
    }

    private boolean isUserEmailExists(String username, String email) {
        return clientRepository.existsByUsernameAndEmail(username, email);
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
        // 檢查使用者名稱和對應Email是否已存在
        if (!isUserEmailExists(resetRequest.getUsername(), resetRequest.getEmail())) {
            return ApiResponseCode.UNKNOWN_USER_OR_EMAIL;
        }
        return null;
    }

    public ResponseEntity<ApiResponse> findByUserId(long id) {
        Optional<ClientModel> modelOption = clientRepository.findById(id);
        return modelOption.map(model -> ApiResponse.success(new ClientResponseModel(model))).orElseGet(() -> ApiResponse.error(ApiResponseCode.USER_NOT_FOUND));
    }

    public ResponseEntity<ApiResponse> updateUser(UpdateClientInfoRequest request) {
        Optional<ClientModel> clientOptional = clientRepository.findById(request.getId());
        if (clientOptional.isEmpty()) throw new UsernameNotFoundException("User Not Found");
        ClientModel client = clientOptional.get();
        if (request.getEmail() != null) client.setEmail(request.getEmail());
        if (request.getRoles() != null) client.setRoles(getRoles(request.getRoles()));
        if (request.getDepartmentId() != null) departmentService.setDepartmentDefaultRole(client, request.getDepartmentId());
        ClientModel save = clientRepository.save(client);
        cacheService.refreshCache(CacheConstant.CLIENT.NAME_CLIENT + CacheConstant.SPLIT_CONSTANT + CacheConstant.CLIENT.CLIENT + client.getId());
        //非本人就發送通知
        checkUserOrSendMessage(client);
        return ApiResponse.success(ApiResponseCode.SUCCESS, new ClientResponseModel(save));
    }

    private void checkUserOrSendMessage(ClientModel client) {
        ClientIdentityDto user = ClientIdentity.getUser();
        if (user != null && user.getId() != client.getId()) {
            Set<ClientModel> set = new HashSet<>();
            set.add(client);
            NotificationModel notification = notificationService.createNotificationToUser(NotificationEnum.UPDATE_USER, set, user.getUsername());
            MessageModel messageModel = new MessageModel(user.getUsername(), Long.toString(client.getId()), WebsocketConstant.TOPIC.NOTIFICATION, notification);
            messageService.sendTo(messageModel);
        }
    }

    private Set<RoleModel> getRoles(List<Long> roles) {
        if (roles == null) return Set.of();
        return roles.stream().map(RoleModel::new).collect(Collectors.toSet());
    }

    public ResponseEntity<ApiResponse> lockClient(ClientStatusRequest request) {
        String username = request.getUsername();
        long uid = request.getClientId();
        int count = clientRepository.lockClientByIdAndUsername(uid, username, request.isStatus());
        if (count != 1) throw new IncorrectResultSizeDataAccessException(1, count);
        cacheService.refreshCache(CacheConstant.CLIENT.NAME_CLIENT + CacheConstant.SPLIT_CONSTANT + CacheConstant.CLIENT.CLIENT + uid);
        //刷新系統用戶緩存
        cacheService.refreshCache(CacheConstant.CLIENT.NAME_CLIENT + CacheConstant.SPLIT_CONSTANT + CacheConstant.CLIENT.SYSTEM_USER);
        //如果是更新成鎖定，就觸發用戶
        if (request.isStatus()) callClientLogout(uid);
        return ApiResponse.success(ApiResponseCode.SUCCESS);
    }

    public ResponseEntity<ApiResponse> clientStatus(ClientStatusRequest request) {
        String username = request.getUsername();
        long uid = request.getClientId();
        int count = clientRepository.switchClientStatusByIdAndUsername(uid, username, request.isStatus());
        if (count != 1) throw new IncorrectResultSizeDataAccessException(1, count);
        cacheService.refreshCache(CacheConstant.CLIENT.NAME_CLIENT + CacheConstant.SPLIT_CONSTANT + CacheConstant.CLIENT.CLIENT + uid);
        //刷新系統用戶緩存
        cacheService.refreshCache(CacheConstant.CLIENT.NAME_CLIENT + CacheConstant.SPLIT_CONSTANT + CacheConstant.CLIENT.SYSTEM_USER);
        //如果是更新成停用，就觸發用戶
        if (!request.isStatus()) callClientLogout(uid);
        return ApiResponse.success(ApiResponseCode.SUCCESS);
    }

    //發websocket觸發，踢出被鎖定用戶
    private void callClientLogout(long uid) {
        MessageModel messageModel = new MessageModel("system", Long.toString(uid), WebsocketConstant.TOPIC.CLIENT_STATUS, "message.kickOut");
        messageService.sendTo(messageModel);
    }

    public Set<ClientModel> findActiveUserAndNotExistAttend() {
        return clientRepository.findActiveUserAndNotExistAttend(LocalDate.now());
    }

    //用戶簽到狀態改為未簽
    public void updateClientAttendStatus() {
        clientRepository.updateClientAttendStatus(LocalDate.now());
    }

    public Set<ClientModel> queryReviewer(Long departmentId) {
        return clientRepository.queryReviewer(departmentId, RoleConstant.LEVEL_1, RoleConstant.LEVEL_3);
    }

    public ResponseEntity<ApiResponse> nameList() {
        List<ClientNameObject> clientNameList = cacheService.getClientNameList();
        return ApiResponse.success(ApiResponseCode.SUCCESS, clientNameList);
    }

    public List<ClientNameObject> getClientNameList() {
        List<Object[]> allNameAndId = clientRepository.findAllNameAndId();
        return allNameAndId.stream().map(ClientNameObject::new).toList();
    }

    public String findNameByUserId(long id) {
        return clientRepository.findUsernameById(id);
    }

    public ClientResponseModel updateClientAttendStatus(ClientIdentityDto model, int status) {
        int resultCount = clientRepository.updateClientAttendStatus(model.getId(), status);
        if (resultCount == 1) cacheService.refreshCache(CacheConstant.CLIENT.NAME_CLIENT + CacheConstant.SPLIT_CONSTANT + CacheConstant.CLIENT.CLIENT + model.getId());
        ClientIdentityDto clientDto = cacheService.getClient(model.getId());
        return new ClientResponseModel(clientDto);
    }

    public boolean checkExistsRoleId(Long id) {
        return clientRepository.checkExistsRoleId(id);
    }

    /**
     * 主要就是處理JWT加黑名單
     * */
    public ResponseEntity<ApiResponse> logout(String accessToken, String refreshToken) {
        if(accessToken != null) cacheService.addTokenBlackList(accessToken);
        if(refreshToken != null) cacheService.addTokenBlackList(refreshToken);
        return ApiResponse.success(ApiResponseCode.SUCCESS);
    }

    public Set<NotificationModel> findNotificationByUserId(Long uid) {
        return clientRepository.findNotificationByUserId(uid);
    }

    public ResponseEntity<ApiResponse> refreshToken() {
        ClientIdentityDto user = ClientIdentity.getUser();
        if(user == null) return ApiResponse.error(ApiResponseCode.ACCESS_DENIED);

        HttpHeaders httpHeaders = new HttpHeaders();
        String accessToken = tokenService.createToken(TokenService.ACCESS_TOKEN, user.getId(), TokenService.ACCESS_TOKEN_EXPIRE_TIME);
        httpHeaders.add(HttpHeaders.AUTHORIZATION, TokenService.TOKEN_PREFIX + accessToken);
        return ApiResponse.success(httpHeaders, null);
    }

    public ResponseEntity<ApiResponse> systemInfo() {
        ClientIdentityDto user = ClientIdentity.getUser();
        Map<String, Object> responseMap = new HashMap<>();
        assert user != null;
        long uid = user.getId();
        responseMap.put("annualLeave", clientRepository.getClientLeave(uid));
        responseMap.put("clientPerformance", performanceService.getClientPerformance(uid));
        responseMap.put("systemInfo", cacheService.getSystemInfo());
        return ApiResponse.success(ApiResponseCode.SUCCESS, responseMap);
    }

    //統計 已打卡/總用戶數
    public String getSystemUser() {
        Object[] systemUser = clientRepository.getSystemUser().get(0);
        return systemUser[0] + "/" + systemUser[1];
    }
}
