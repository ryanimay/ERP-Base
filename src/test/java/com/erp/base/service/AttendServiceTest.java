package com.erp.base.service;

import com.erp.base.model.constant.response.ApiResponseCode;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.dto.response.ClientResponseModel;
import com.erp.base.model.dto.security.ClientIdentityDto;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.repository.AttendRepository;
import com.erp.base.service.security.UserDetailImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class AttendServiceTest {
    @Mock
    private AttendRepository attendRepository;
    @Mock
    private ClientService clientService;
    @InjectMocks
    private AttendService attendService;
    private static final ClientModel clientModel;
    static {
        clientModel = new ClientModel(1);
        clientModel.setUsername("test");
    }

    @Test
    @DisplayName("簽到_找不到用戶_錯誤")
    void signIn_userNotFound_error() {
        SecurityContextHolder.clearContext();
        ResponseEntity<ApiResponse> response = attendService.signIn();
        Assertions.assertEquals(ApiResponse.error(ApiResponseCode.USER_NOT_FOUND), response);
    }

    @Test
    @DisplayName("簽到_成功")
    void signIn_ok() {
        Mockito.when(attendRepository.signIn(Mockito.anyLong(), Mockito.any(), Mockito.any())).thenReturn(1);
        Mockito.when(clientService.updateClientAttendStatus(Mockito.any(), Mockito.anyInt())).thenReturn(clientModel);
        UserDetailImpl principal = new UserDetailImpl(new ClientIdentityDto(clientModel), null);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        ResponseEntity<ApiResponse> response = attendService.signIn();
        Assertions.assertEquals(ApiResponse.success(ApiResponseCode.SUCCESS, new ClientResponseModel(clientModel)), response);

    }

    @Test
    @DisplayName("簽到_未知錯誤")
    void signIn_IncorrectResultSizeDataAccessException() {
        UserDetailImpl principal = new UserDetailImpl(new ClientIdentityDto(clientModel), null);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Assertions.assertThrows(IncorrectResultSizeDataAccessException.class, () ->  attendService.signIn());
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("簽退_找不到用戶_錯誤")
    void signOut_userNotFound_error() {
        SecurityContextHolder.clearContext();
        ResponseEntity<ApiResponse> response = attendService.signOut();
        Assertions.assertEquals(ApiResponse.error(ApiResponseCode.USER_NOT_FOUND), response);
    }

    @Test
    @DisplayName("簽退_成功")
    void signOut_ok() {
        Mockito.when(attendRepository.signOut(Mockito.anyLong(), Mockito.any(), Mockito.any())).thenReturn(1);
        Mockito.when(clientService.updateClientAttendStatus(Mockito.any(), Mockito.anyInt())).thenReturn(clientModel);
        UserDetailImpl principal = new UserDetailImpl(new ClientIdentityDto(clientModel), null);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        ResponseEntity<ApiResponse> response = attendService.signOut();
        Assertions.assertEquals(ApiResponse.success(ApiResponseCode.SUCCESS, new ClientResponseModel(clientModel)), response);
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("簽退_未知錯誤")
    void signOut_IncorrectResultSizeDataAccessException() {
        UserDetailImpl principal = new UserDetailImpl(new ClientIdentityDto(clientModel), null);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Assertions.assertThrows(IncorrectResultSizeDataAccessException.class, () ->  attendService.signOut());
        SecurityContextHolder.clearContext();
    }
}