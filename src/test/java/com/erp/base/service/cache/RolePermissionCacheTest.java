package com.erp.base.service.cache;

import com.erp.base.model.dto.response.MenuResponse;
import com.erp.base.model.dto.response.role.PermissionListResponse;
import com.erp.base.model.dto.security.RolePermissionDto;
import com.erp.base.model.entity.DepartmentModel;
import com.erp.base.model.entity.PermissionModel;
import com.erp.base.model.entity.RoleModel;
import com.erp.base.service.*;
import com.erp.base.testConfig.redis.TestRedisConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@SpringBootTest(classes = TestRedisConfiguration.class)
@TestPropertySource(locations = {
        "classpath:application-redis-test.properties",
        "classpath:application-quartz-test.properties"
})
@AutoConfigureMockMvc
@Transactional
@DirtiesContext
class RolePermissionCacheTest {
    @MockBean
    private RoleService roleService;
    @MockBean
    private PermissionService permissionService;
    @MockBean
    private ClientService clientService;
    @MockBean
    private MenuService menuService;
    @MockBean
    private DepartmentService departmentService;
    @Autowired
    private RolePermissionCache rolePermissionCache;
    private static final List<RoleModel> roles = new ArrayList<>();
    private static final List<PermissionModel> permissions = new ArrayList<>();
    private static final RoleModel r3;
    private static final RoleModel r2;
    private static final RoleModel r1;
    static {
        r3 = new RoleModel(3);
        roles.add(r3);
        r2 = new RoleModel(2);
        roles.add(r2);
        r1 = new RoleModel(1);
        roles.add(r1);
        PermissionModel p1 = new PermissionModel(1);
        p1.setAuthority("TEST1_1");
        permissions.add(p1);
        PermissionModel p2 = new PermissionModel(2);
        p2.setAuthority("TEST1_2");
        permissions.add(p2);
        PermissionModel p3 = new PermissionModel(3);
        p3.setAuthority("TEST2_1");
        permissions.add(p3);
    }

    @Test
    void getRole_ok() {
        Mockito.when(roleService.findAll()).thenReturn(roles);
        Map<Long, RoleModel> role = rolePermissionCache.getRole();
        RoleModel r4 = new RoleModel(4);
        roles.add(r4);
        Mockito.when(roleService.findAll()).thenReturn(roles);
        Map<Long, RoleModel> role1 = rolePermissionCache.getRole();

        Assertions.assertEquals(3, role.size());
        Assertions.assertEquals(3, role1.size());

        Mockito.verify(roleService, Mockito.times(1)).findAll();
        Mockito.verifyNoMoreInteractions(roleService);
    }

    @Test
    void getPermission_ok() {
        Mockito.when(permissionService.findAll()).thenReturn(permissions);
        List<PermissionModel> permission = rolePermissionCache.getPermission();
        List<PermissionModel> permission1 = rolePermissionCache.getPermission();

        Assertions.assertEquals(permissions, permission);
        Assertions.assertEquals(permissions, permission1);

        Mockito.verify(permissionService, Mockito.times(1)).findAll();
        Mockito.verifyNoMoreInteractions(permissionService);
    }

    @Test
    void getPermissionList_ok() {
        Mockito.when(permissionService.findAll()).thenReturn(permissions);
        List<PermissionListResponse> permissionList = rolePermissionCache.getPermissionList();
        Assertions.assertEquals(permissions.get(0), permissionList.get(1).getChildren().get(0));
        Assertions.assertEquals(permissions.get(1), permissionList.get(1).getChildren().get(1));
        Assertions.assertEquals(permissions.get(2), permissionList.get(0).getChildren().get(0));
    }

    @Test
    void getRolePermission_ok() {
        RoleModel r = new RoleModel(1);
        r.setPermissions(new HashSet<>(permissions));
        Mockito.when(roleService.findById(Mockito.anyLong())).thenReturn(r);
        Set<RolePermissionDto> rolePermission = rolePermissionCache.getRolePermission(1L);
        List<RolePermissionDto> rolePermissionDtoList = new ArrayList<>(rolePermission);
        Assertions.assertEquals(new RolePermissionDto(permissions.get(1)), rolePermissionDtoList.get(0));
        Assertions.assertEquals(new RolePermissionDto(permissions.get(0)), rolePermissionDtoList.get(1));
        Assertions.assertEquals(new RolePermissionDto(permissions.get(2)), rolePermissionDtoList.get(2));
    }

    @Test
    void permissionStatus_ok() {
        Mockito.when(permissionService.checkPermissionIfDeny(Mockito.any())).thenReturn(true);
        boolean status = rolePermissionCache.permissionStatus("/test");
        Mockito.when(permissionService.checkPermissionIfDeny(Mockito.any())).thenReturn(false);
        boolean status1 = rolePermissionCache.permissionStatus("/test");

        Assertions.assertTrue(status);
        Assertions.assertTrue(status1);

        Mockito.verify(permissionService, Mockito.times(1)).checkPermissionIfDeny(Mockito.any());
        Mockito.verifyNoMoreInteractions(permissionService);
    }

    @Test
    void getDepartment_ok() {
        Mockito.when(departmentService.findById(Mockito.anyLong())).thenReturn(new DepartmentModel(1L));
        DepartmentModel department = rolePermissionCache.getDepartment(1L);
        Assertions.assertEquals(1L, department.getId());
    }

    @Test
    void findMenuTree_ok() {
        Mockito.when(menuService.findAllTree()).thenReturn(new ArrayList<>());
        List<MenuResponse> result = rolePermissionCache.findMenuTree();
        Mockito.when(menuService.findAllTree()).thenReturn(null);
        List<MenuResponse> result1 = rolePermissionCache.findMenuTree();

        Assertions.assertEquals(new ArrayList<>(), result);
        Assertions.assertEquals(result, result1);

        Mockito.verify(menuService, Mockito.times(1)).findAllTree();
        Mockito.verifyNoMoreInteractions(menuService);
    }

    @Test
    void getRoleMenu_ok() {
        Mockito.when(menuService.getRoleMenu(Mockito.anyLong())).thenReturn(new ArrayList<>());
        List<MenuResponse> result = rolePermissionCache.getRoleMenu(1L);
        Mockito.when(menuService.getRoleMenu(Mockito.anyLong())).thenReturn(null);
        List<MenuResponse> result1 = rolePermissionCache.getRoleMenu(1L);

        Assertions.assertEquals(new ArrayList<>(), result);
        Assertions.assertEquals(result, result1);

        Mockito.verify(menuService, Mockito.times(1)).getRoleMenu(Mockito.anyLong());
        Mockito.verifyNoMoreInteractions(menuService);
    }
}