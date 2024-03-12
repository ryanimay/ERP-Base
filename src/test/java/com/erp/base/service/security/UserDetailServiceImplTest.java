package com.erp.base.service.security;

import com.erp.base.model.entity.ClientModel;
import com.erp.base.service.ClientService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class UserDetailServiceImplTest {
    @Mock
    private ClientService clientService;
    @Mock
    private UserDetailFactory userDetailFactory;
    @InjectMocks
    private UserDetailServiceImpl userDetailService;

    @Test
    void loadUserByUsername_userNotFound() {
        Mockito.when(clientService.findByUsername(Mockito.any())).thenReturn(null);
        Assertions.assertThrows(UsernameNotFoundException.class, () -> userDetailService.loadUserByUsername(""));
    }

    @Test
    void loadUserByUsername_ok() {
        ClientModel t = new ClientModel(1);
        t.setUsername("test");
        Mockito.when(clientService.findByUsername(Mockito.any())).thenReturn(t);
        Mockito.when(userDetailFactory.build(Mockito.any())).thenReturn(new UserDetailImpl(t, null));
        Assertions.assertEquals(new UserDetailImpl(t, null).getUsername(), userDetailService.loadUserByUsername("").getUsername());
    }
}