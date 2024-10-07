package com.erp.base.service.cache;

import com.erp.base.service.ClientService;
import com.erp.base.service.DepartmentService;
import com.erp.base.service.ProcurementService;
import com.erp.base.service.ProjectService;
import com.erp.base.testConfig.redis.TestRedisConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(classes = TestRedisConfiguration.class)
@TestPropertySource(locations = {
        "classpath:application-redis-test.properties",
        "classpath:application-quartz-test.properties"
})
@AutoConfigureMockMvc
@Transactional
@DirtiesContext
class OtherCacheTest {
    @MockBean
    private DepartmentService departmentService;
    @MockBean
    private ClientService clientService;
    @MockBean
    private ProjectService projectService;
    @MockBean
    private ProcurementService procureService;
    @Autowired
    private OtherCache otherCache;

    @BeforeEach
    void setUp() {
        otherCache.setDepartmentService(departmentService);
        otherCache.setProjectService(projectService);
        otherCache.setProcureService(procureService);
    }

    @Test
    void getSystemDepartment_ok() {
        Mockito.when(departmentService.getSystemDepartment()).thenReturn("1");
        String result = otherCache.getSystemDepartment();
        Mockito.when(departmentService.getSystemDepartment()).thenReturn("2");
        String result1 = otherCache.getSystemDepartment();
        Assertions.assertEquals("1", result);
        Assertions.assertEquals("1", result1);
        Mockito.verify(departmentService, Mockito.times(1)).getSystemDepartment();
        Mockito.verifyNoMoreInteractions(departmentService);
    }

    @Test
    void getSystemProject_ok() {
        Mockito.when(projectService.getSystemProject()).thenReturn("1");
        String result = otherCache.getSystemProject();
        Mockito.when(projectService.getSystemProject()).thenReturn("2");
        String result1 = otherCache.getSystemProject();
        Assertions.assertEquals("1", result);
        Assertions.assertEquals("1", result1);
        Mockito.verify(projectService, Mockito.times(1)).getSystemProject();
        Mockito.verifyNoMoreInteractions(projectService);
    }

    @Test
    void getSystemProcure_ok() {
        Mockito.when(procureService.getSystemProcure()).thenReturn(new Object[]{"1", "2"});
        Object[] result = otherCache.getSystemProcure();
        Mockito.when(procureService.getSystemProcure()).thenReturn(new Object[]{"2", "3"});
        Object[] result1 = otherCache.getSystemProcure();
        Assertions.assertEquals("1", result[0]);
        Assertions.assertEquals("2", result[1]);
        Assertions.assertEquals("1", result1[0]);
        Assertions.assertEquals("2", result1[1]);
        Mockito.verify(procureService, Mockito.times(1)).getSystemProcure();
        Mockito.verifyNoMoreInteractions(procureService);
    }
}