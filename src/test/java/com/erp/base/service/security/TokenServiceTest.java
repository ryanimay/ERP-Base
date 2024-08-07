package com.erp.base.service.security;

import com.erp.base.model.dto.request.client.LoginRequest;
import com.erp.base.model.dto.security.ClientIdentityDto;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.service.CacheService;
import com.erp.base.service.ClientService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Map;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {
    @Mock
    private CacheService cacheService;
    @Mock
    private ClientService clientService;
    @Mock
    private AuthenticationProvider authenticationProvider;
    @InjectMocks
    private TokenService tokenService;
    private static final long DEFAULT_UID = 1L;

    @BeforeEach
    void setUp() {
        tokenService.init();
    }

    @Test
    void createToken_authenticateFailed() {
        Mockito.when(authenticationProvider.authenticate(Mockito.any())).thenThrow(BadCredentialsException.class);
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("test");
        loginRequest.setPassword("test");
        Assertions.assertThrows(BadCredentialsException.class, () -> tokenService.createToken(loginRequest));
    }

    @Test
    void createToken_notRememberMe_ok() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("test");
        loginRequest.setPassword("test");
        loginRequest.setRememberMe(false);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(new UserDetailImpl(new ClientIdentityDto(new ClientModel(1)), cacheService), null);
        Mockito.when(authenticationProvider.authenticate(Mockito.any())).thenReturn(authentication);
        Mockito.when(clientService.findByUsername(Mockito.any())).thenReturn(new ClientModel(1));
        HttpHeaders header = tokenService.createToken(loginRequest);
        Assertions.assertNotNull(header.get(HttpHeaders.AUTHORIZATION));
        Assertions.assertNull(header.get(TokenService.REFRESH_TOKEN));
    }

    @Test
    void createToken_rememberMe_ok() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("test");
        loginRequest.setPassword("test");
        loginRequest.setRememberMe(true);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(new UserDetailImpl(new ClientIdentityDto(new ClientModel(1)), cacheService), null);
        Mockito.when(authenticationProvider.authenticate(Mockito.any())).thenReturn(authentication);
        Mockito.when(clientService.findByUsername(Mockito.any())).thenReturn(new ClientModel(1));
        HttpHeaders header = tokenService.createToken(loginRequest);
        Assertions.assertNotNull(header.get(HttpHeaders.AUTHORIZATION));
        Assertions.assertNotNull(header.get(TokenService.REFRESH_TOKEN));
    }

    @Test
    void createToken_parseToken_ok() {
        String token = tokenService.createToken(TokenService.ACCESS_TOKEN, DEFAULT_UID, TokenService.ACCESS_TOKEN_EXPIRE_TIME);
        Map<String, Object> map = tokenService.parseToken(token);
        String uid = String.valueOf(map.get(TokenService.TOKEN_PROPERTIES_UID));
        Assertions.assertEquals(DEFAULT_UID, Long.parseLong(uid));
    }
}