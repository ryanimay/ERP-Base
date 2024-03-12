package com.erp.base.service.cache;

import com.erp.base.model.dto.security.RolePermissionDto;
import com.erp.base.model.entity.DepartmentModel;
import com.erp.base.model.entity.PermissionModel;
import com.erp.base.model.entity.RoleModel;
import com.erp.base.service.DepartmentService;
import com.erp.base.service.PermissionService;
import com.erp.base.service.RoleService;
import com.erp.base.service.RouterService;
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
    private RouterService routerService;
    @MockBean
    private DepartmentService departmentService;
    @Autowired
    private RolePermissionCache rolePermissionCache;
    private static final List<RoleModel> roles = new ArrayList<>();
    private static final List<PermissionModel> permissions = new ArrayList<>();
    static {
        roles.add(new RoleModel(3));
        roles.add(new RoleModel(2));
        roles.add(new RoleModel(1));
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
        Map<Long, RoleModel> role1 = rolePermissionCache.getRole();

        Assertions.assertEquals(new RoleModel(1), role.get(1L));
        Assertions.assertEquals(new RoleModel(2), role.get(2L));
        Assertions.assertEquals(new RoleModel(3), role.get(3L));
        Assertions.assertEquals(new RoleModel(1), role1.get(1L));
        Assertions.assertEquals(new RoleModel(2), role1.get(2L));
        Assertions.assertEquals(new RoleModel(3), role1.get(3L));

        Mockito.verify(roleService, Mockito.times(1)).findAll();
        Mockito.verifyNoMoreInteractions(roleService);
    }

    @Test
    void refreshRole_ok() {
        Mockito.when(roleService.findAll()).thenReturn(roles);
        rolePermissionCache.refreshRole();
        rolePermissionCache.getRole();
        rolePermissionCache.refreshRole();
        rolePermissionCache.getRole();

        Mockito.verify(roleService, Mockito.times(2)).findAll();
        Mockito.verifyNoMoreInteractions(roleService);
    }

    @Test
    void getPermission_ok() {
        Mockito.when(permissionService.findAll()).thenReturn(permissions);
        rolePermissionCache.refreshAll();
        List<PermissionModel> permission = rolePermissionCache.getPermission();
        List<PermissionModel> permission1 = rolePermissionCache.getPermission();

        Assertions.assertEquals(permissions, permission);
        Assertions.assertEquals(permissions, permission1);

        Mockito.verify(permissionService, Mockito.times(1)).findAll();
        Mockito.verifyNoMoreInteractions(permissionService);
    }

    @Test
    void getPermissionMap_ok() {
        Mockito.when(permissionService.findAll()).thenReturn(permissions);
        rolePermissionCache.refreshAll();
        Map<String, List<PermissionModel>> permissionMap = rolePermissionCache.getPermissionMap();

        Assertions.assertEquals(permissions.get(0), permissionMap.get("TEST1").get(0));
        Assertions.assertEquals(permissions.get(1), permissionMap.get("TEST1").get(1));
        Assertions.assertEquals(permissions.get(2), permissionMap.get("TEST2").get(0));
    }

    @Test
    void getRolePermission_ok() {
        RoleModel r = new RoleModel(1);
        r.setPermissions(new HashSet<>(permissions));
        Mockito.when(roleService.findById(Mockito.anyLong())).thenReturn(r);
        rolePermissionCache.refreshAll();
        Set<RolePermissionDto> rolePermission = rolePermissionCache.getRolePermission(1L);
        List<RolePermissionDto> rolePermissionDtoList = new ArrayList<>(rolePermission);
        Assertions.assertEquals(new RolePermissionDto(permissions.get(1)), rolePermissionDtoList.get(0));
        Assertions.assertEquals(new RolePermissionDto(permissions.get(0)), rolePermissionDtoList.get(1));
        Assertions.assertEquals(new RolePermissionDto(permissions.get(2)), rolePermissionDtoList.get(2));
    }

    @Test
    void getDepartment_ok() {
        Mockito.when(departmentService.findById(Mockito.anyLong())).thenReturn(new DepartmentModel(1L));
        rolePermissionCache.refreshAll();
        DepartmentModel department = rolePermissionCache.getDepartment(1L);
        Assertions.assertEquals(1L, department.getId());
    }
}