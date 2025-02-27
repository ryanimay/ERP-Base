package com.erp.base.service;


import com.erp.base.model.constant.response.ApiResponseCode;
import com.erp.base.model.dto.request.client.*;
import com.erp.base.model.dto.response.*;
import com.erp.base.model.dto.security.ClientIdentityDto;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.mail.ResetPasswordModel;
import com.erp.base.repository.ClientRepository;
import com.erp.base.service.security.TokenService;
import com.erp.base.service.security.UserDetailImpl;
import com.erp.base.tool.EncodeTool;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private EncodeTool encodeTool;
    @Mock
    private TokenService tokenService;
    @Mock
    private DepartmentService departmentService;
    @Mock
    private PerformanceService performanceService;
    @Mock
    private CacheService cacheService;
    @InjectMocks
    private ClientService clientService;

    @BeforeEach
    void setUp() {
        clientService.setMailService(Mockito.mock(MailService.class));
        clientService.setResetPasswordModel(Mockito.mock(ResetPasswordModel.class));
        clientService.setMessageService(Mockito.mock(MessageService.class));
        clientService.setNotificationService(Mockito.mock(NotificationService.class));
        clientService.setAttendService(Mockito.mock(AttendService.class));
    }

    @Test
    @DisplayName("註冊_找不到用戶_失敗")
    void register_userNameExists_error() {
        Mockito.when(clientRepository.existsByUsername(Mockito.any())).thenReturn(true);
        ResponseEntity<ApiResponse> response = clientService.register(new RegisterRequest());
        Assertions.assertEquals(ApiResponse.error(ApiResponseCode.USERNAME_ALREADY_EXIST), response);
    }

    @Test
    @DisplayName("註冊_成功")
    void register_ok() {
        RegisterRequest dto = new RegisterRequest();
        dto.setUsername("");
        dto.setCreateBy(1L);
        dto.setDepartmentId(1L);
        Mockito.when(clientRepository.existsByUsername(Mockito.any())).thenReturn(false);
        Mockito.when(departmentService.setDepartmentDefaultRole(Mockito.any(), Mockito.any())).thenReturn(dto.toModel());
        ResponseEntity<ApiResponse> response = clientService.register(dto);
        Assertions.assertEquals(ApiResponse.success(ApiResponseCode.REGISTER_SUCCESS), response);
    }

    @Test
    @DisplayName("登入_成功")
    void login_ok() {
        ClientModel client = new ClientModel(1);
        Mockito.when(tokenService.createToken(Mockito.any())).thenReturn(new HttpHeaders());
        Mockito.when(clientRepository.findByUsername(Mockito.any())).thenReturn(client);
        ResponseEntity<ApiResponse> response = clientService.login(new LoginRequest());
        ResponseEntity<ApiResponse> expected = ApiResponse.success(new HttpHeaders(), new ClientResponseModel(client));
        Assertions.assertEquals(expected.getHeaders(), response.getHeaders());
        Assertions.assertEquals(expected.getStatusCode(), response.getStatusCode());
        Assertions.assertEquals(Objects.requireNonNull(expected.getBody()).getCode(), Objects.requireNonNull(response.getBody()).getCode());
        Assertions.assertEquals(Objects.requireNonNull(expected.getBody()).getMessage(), Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    @DisplayName("用戶清單_全搜_成功")
    void list_findAll_ok() {
        Page<ClientModel> page = new PageImpl<>(new ArrayList<>());
        Mockito.when(clientRepository.findAll((Pageable) Mockito.any())).thenReturn(page);
        PageResponse<ClientResponseModel> response = clientService.list(new ClientListRequest());
        Assertions.assertEquals(new PageResponse<>(Objects.requireNonNull(page), ClientResponseModel.class), response);
    }

    @Test
    @DisplayName("用戶清單_找ID_成功")
    void list_findById_ok() {
        Page<ClientModel> page = new PageImpl<>(new ArrayList<>());
        Mockito.when(clientRepository.findById(Mockito.any(), Mockito.any())).thenReturn(page);
        ClientListRequest request = new ClientListRequest();
        request.setId(1L);
        request.setType(1);
        PageResponse<ClientResponseModel> response = clientService.list(request);
        Assertions.assertEquals(new PageResponse<>(Objects.requireNonNull(page), ClientResponseModel.class), response);
    }

    @Test
    @DisplayName("用戶清單_找用戶名_成功")
    void list_findByUsernameContaining_ok() {
        Page<ClientModel> page = new PageImpl<>(new ArrayList<>());
        Mockito.when(clientRepository.findByUsernameContaining(Mockito.any(), Mockito.any())).thenReturn(page);
        ClientListRequest request = new ClientListRequest();
        request.setId(1L);
        request.setType(2);
        PageResponse<ClientResponseModel> response = clientService.list(request);
        Assertions.assertEquals(new PageResponse<>(Objects.requireNonNull(page), ClientResponseModel.class), response);
    }

    @Test
    @DisplayName("重設密碼_找不到對應用戶名/mail_錯誤")
    void resetPassword_UserEmailNotExists_error() throws MessagingException {
        Mockito.when(clientRepository.existsByUsernameAndEmail(Mockito.any(), Mockito.any())).thenReturn(false);
        ResponseEntity<ApiResponse> response = clientService.resetPassword(new ResetPasswordRequest());
        Assertions.assertEquals(ApiResponse.error(ApiResponseCode.UNKNOWN_USER_OR_EMAIL), response);
    }

    @Test
    @DisplayName("重設密碼_成功")
    void resetPassword_ok() throws MessagingException {
        Mockito.when(clientRepository.existsByUsernameAndEmail(Mockito.any(), Mockito.any())).thenReturn(true);
        Mockito.when(encodeTool.randomPassword(Mockito.anyInt())).thenReturn(new EncodeTool().randomPassword(18));
        Mockito.when(clientRepository.updatePasswordByUsernameAndEmailAndId(Mockito.any(), Mockito.anyBoolean(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(1);
        Mockito.when(clientRepository.findByUsername(Mockito.any())).thenReturn(new ClientModel(1));
        ResponseEntity<ApiResponse> response = clientService.resetPassword(new ResetPasswordRequest());
        Assertions.assertEquals(ApiResponse.success(ApiResponseCode.RESET_PASSWORD_SUCCESS), response);
    }

    @Test
    @DisplayName("更新密碼_找不到用戶_錯誤")
    void updatePassword_userNotFound_error() {
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setId(77L);
        ResponseEntity<ApiResponse> response = clientService.updatePassword(request);
        Assertions.assertEquals(ApiResponse.error(ApiResponseCode.IDENTITY_ERROR), response);
    }

    @Test
    @DisplayName("更新密碼_非本人不可更新_錯誤")
    void updatePassword_identityError_error() {
        UserDetailImpl principal = new UserDetailImpl(new ClientIdentityDto(new ClientModel(1)), null);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setId(2L);
        ResponseEntity<ApiResponse> response = clientService.updatePassword(request);
        Assertions.assertEquals(ApiResponse.error(ApiResponseCode.IDENTITY_ERROR), response);
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("更新密碼_舊密碼驗證錯誤_錯誤")
    void updatePassword_invalidOldPassword_error() {
        Mockito.when(clientRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        UserDetailImpl principal = new UserDetailImpl(new ClientIdentityDto(new ClientModel(1)), null);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setId(1L);
        ResponseEntity<ApiResponse> response = clientService.updatePassword(request);
        Assertions.assertEquals(ApiResponse.error(ApiResponseCode.INVALID_LOGIN), response);
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("更新密碼_成功")
    void updatePassword_ok() {
        Mockito.when(clientRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new ClientModel(1L)));
        Mockito.when(encodeTool.match(Mockito.any(), Mockito.any())).thenReturn(true);
        Mockito.when(clientRepository.updatePasswordByUsernameAndEmailAndId(Mockito.any(), Mockito.anyBoolean(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(1);
        UserDetailImpl principal = new UserDetailImpl(new ClientIdentityDto(new ClientModel(1)), null);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setId(1L);
        ResponseEntity<ApiResponse> response = clientService.updatePassword(request);
        Assertions.assertEquals(ApiResponse.success(ApiResponseCode.UPDATE_PASSWORD_SUCCESS), response);
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("找用戶ID_找不到_成功")
    void findByUserId_userNotFound_ok() {
        Mockito.when(clientRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        ResponseEntity<ApiResponse> response = clientService.findByUserId(1);
        Assertions.assertEquals(ApiResponse.error(ApiResponseCode.USER_NOT_FOUND), response);
    }

    @Test
    @DisplayName("找用戶ID_成功")
    void findByUserId_ok() {
        ClientModel model = new ClientModel(1);
        Mockito.when(clientRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(model));
        ResponseEntity<ApiResponse> response = clientService.findByUserId(1);
        Assertions.assertEquals(ApiResponse.success(new ClientResponseModel(model)), response);
    }

    @Test
    @DisplayName("更新用戶_找不到用戶_錯誤")
    void updateUser_userNotFound_error() {
        Mockito.when(clientRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        UpdateClientInfoRequest request = new UpdateClientInfoRequest();
        request.setId(1L);
        Assertions.assertThrows(UsernameNotFoundException.class, () -> clientService.updateUser(request));
    }

    @Test
    @DisplayName("更新用戶_成功")
    void updateUser_ok() {
        ClientModel model = new ClientModel(1);
        Mockito.when(clientRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(model));
        Mockito.when(clientRepository.save(Mockito.any())).thenReturn(model);
        UpdateClientInfoRequest request = new UpdateClientInfoRequest();
        request.setId(1L);
        ResponseEntity<ApiResponse> response = clientService.updateUser(request);
        Assertions.assertEquals(ApiResponse.success(ApiResponseCode.SUCCESS, new ClientResponseModel(model)), response);
    }

    @Test
    @DisplayName("用戶鎖定_成功")
    void lockClient_ok() {
        Mockito.when(clientRepository.lockClientByIdAndUsername(Mockito.anyLong(), Mockito.any(), Mockito.anyBoolean())).thenReturn(1);
        ResponseEntity<ApiResponse> response = clientService.lockClient(new ClientStatusRequest());
        Assertions.assertEquals(ApiResponse.success(ApiResponseCode.SUCCESS), response);
    }

    @Test
    @DisplayName("更改用戶狀態_成功")
    void clientStatus_ok() {
        Mockito.when(clientRepository.switchClientStatusByIdAndUsername(Mockito.anyLong(), Mockito.any(), Mockito.anyBoolean())).thenReturn(1);
        ResponseEntity<ApiResponse> response = clientService.clientStatus(new ClientStatusRequest());
        Assertions.assertEquals(ApiResponse.success(ApiResponseCode.SUCCESS), response);
    }

    @Test
    @DisplayName("系統參數_成功")
    void systemInfo_ok() {
        UserDetailImpl principal = new UserDetailImpl(new ClientIdentityDto(new ClientModel(1)), null);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        AnnualLeaveDto annualLeaveDto = new AnnualLeaveDto("1", "2", 3L);
        Mockito.when(clientRepository.getClientLeave(Mockito.anyLong())).thenReturn(annualLeaveDto);
        PerformanceCountDto performanceCountDto = new PerformanceCountDto(1L, 2L);
        Mockito.when(performanceService.getClientPerformance(Mockito.anyLong())).thenReturn(performanceCountDto);
        HashMap<String, Object> map = new HashMap<>();
        map.put("systemUser", 1);
        map.put("systemDepartment", 2);
        map.put("systemProject", 3);
        map.put("systemProcure", 4);
        Mockito.when(cacheService.getSystemInfo()).thenReturn(map);
        ResponseEntity<ApiResponse> response = clientService.systemInfo();
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("annualLeave", annualLeaveDto);
        responseMap.put("clientPerformance", performanceCountDto);
        responseMap.put("systemInfo", map);

        ResponseEntity<ApiResponse> expected = ApiResponse.success(ApiResponseCode.SUCCESS, responseMap);
        Assertions.assertEquals(expected, response);
    }

    @Test
    @DisplayName("統計用戶_成功")
    void getSystemUser_ok() {
        List<Object[]> objects = new ArrayList<>();
        objects.add(new Object[]{"1", "3"});
        Mockito.when(clientRepository.getSystemUser()).thenReturn(objects);
        String response = clientService.getSystemUser();
        Assertions.assertEquals("1/3", response);
    }
}