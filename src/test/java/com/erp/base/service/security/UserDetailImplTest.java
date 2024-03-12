package com.erp.base.service.security;

import com.erp.base.model.dto.security.RolePermissionDto;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.entity.PermissionModel;
import com.erp.base.model.entity.RoleModel;
import com.erp.base.service.CacheService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashSet;
import java.util.Set;

class UserDetailImplTest {
    private static UserDetailImpl userDetail;
    private static ClientModel clientModel;
    private static final CacheService cacheService = Mockito.mock(CacheService.class);

    @BeforeAll
    static void beforeAll() {
        RoleModel roleModel = new RoleModel(1);
        Set<RoleModel> set = new HashSet<>();
        set.add(roleModel);
        clientModel = new ClientModel();
        clientModel.setUsername("test");
        clientModel.setPassword("test");
        clientModel.setActive(true);
        clientModel.setLock(false);
        clientModel.setRoles(set);
        userDetail = new UserDetailImpl(clientModel, cacheService);
    }

    @Test
    void getUsername_ok() {
        Assertions.assertEquals(userDetail.getUsername(), clientModel.getUsername());
    }

    @Test
    void getPassword_ok() {
        Assertions.assertEquals(userDetail.getPassword(), clientModel.getPassword());
    }

    @Test
    void isEnabled_ok() {
        Assertions.assertEquals(userDetail.isEnabled(), clientModel.isActive());
    }

    @Test
    void isAccountNonLocked_ok() {
        Assertions.assertEquals(userDetail.isAccountNonLocked(), !clientModel.isLock());
    }

    @Test
    void isCredentialsNonExpired_ok() {
        Assertions.assertTrue(userDetail.isCredentialsNonExpired());
    }

    @Test
    void getAuthorities_ok() {
        Set<RolePermissionDto> set = new HashSet<>();
        set.add(new RolePermissionDto(new PermissionModel(1)));
        Mockito.when(cacheService.getRolePermission(Mockito.anyLong())).thenReturn(set);
        Assertions.assertEquals(userDetail.getAuthorities(), set);
    }

    @Test
    void isAccountNonExpired_ok() {
        Assertions.assertTrue(userDetail.isAccountNonExpired());
    }
}