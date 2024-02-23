package com.erp.base.controller;

import com.erp.base.config.TestUtils;
import com.erp.base.config.redis.TestRedisConfiguration;
import com.erp.base.enums.StatusConstant;
import com.erp.base.enums.response.ApiResponseCode;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.dto.response.PerformanceResponse;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.entity.DepartmentModel;
import com.erp.base.model.entity.PerformanceModel;
import com.erp.base.model.entity.RoleModel;
import com.erp.base.repository.ClientRepository;
import com.erp.base.repository.PerformanceRepository;
import com.erp.base.repository.RoleRepository;
import com.erp.base.service.CacheService;
import com.erp.base.tool.DateTool;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

@SpringBootTest(classes = TestRedisConfiguration.class)
@TestPropertySource(locations = {
        "classpath:application-redis-test.properties",
        "classpath:application-quartz-test.properties"
})
@AutoConfigureMockMvc
@Transactional
@DirtiesContext
class PerformanceControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TestUtils testUtils;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private PerformanceRepository performanceRepository;
    @Autowired
    private CacheService cacheService;
    @PersistenceContext
    private EntityManager entityManager;
    private static final String DEFAULT_USER_NAME = "test";
    private static ClientModel me;

    @BeforeAll
    static void beforeAll(){
        me = new ClientModel(1L);
        me.setUsername(DEFAULT_USER_NAME);
        me.setRoles(Set.of(new RoleModel(2L)));
        me.setDepartment(new DepartmentModel(1L));
    }

    @Test
    @DisplayName("待審核績效_管理層不分部門全搜_不搜自己_成功")
    @WithUserDetails(DEFAULT_USER_NAME)
    void performancePendingList_managerSearch_ok() throws Exception {
        //不同部門非本人
        ClientModel newClient1 = createDifferentDepartmentUser("testPerformance1", 3L);//
        PerformanceResponse performance1 = new PerformanceResponse(createPerformance(newClient1));
        //同部門非本人
        ClientModel newClient2 = createDifferentDepartmentUser("testPerformance2", me.getDepartment().getId());
        PerformanceResponse performance2 = new PerformanceResponse(createPerformance(newClient2));
        //本人
        PerformanceModel selfPerformance = createPerformance(me);
        //權限設為level3
        updateRoleLevel(3);
        refreshCache();

        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.PERFORMANCE.PENDING_LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));

        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        testUtils.comparePage(resultActions, 15, 1, 2, 1);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].id").value(performance1.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].event").value(performance1.getEvent()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].user.id").value(performance1.getUser().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].user.username").value(performance1.getUser().getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].fixedBonus").value(performance1.getFixedBonus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].performanceRatio").value(performance1.getPerformanceRatio()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].eventTime").value(performance1.getEventTime().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createTime").value(performance1.getCreateTime().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createBy").value(performance1.getCreateBy()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].status").value(performance1.getStatus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].id").value(performance2.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].event").value(performance2.getEvent()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].user.id").value(performance2.getUser().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].user.username").value(performance2.getUser().getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].fixedBonus").value(performance2.getFixedBonus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].performanceRatio").value(performance2.getPerformanceRatio()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].eventTime").value(performance2.getEventTime().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].createTime").value(performance2.getCreateTime().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].createBy").value(performance2.getCreateBy()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].status").value(performance2.getStatus()));
        updateRoleLevel(2);
        clientRepository.deleteById(newClient1.getId());
        clientRepository.deleteById(newClient2.getId());
        performanceRepository.deleteById(performance1.getId());
        performanceRepository.deleteById(performance2.getId());
        performanceRepository.deleteById(selfPerformance.getId());
    }

    @Test
    @DisplayName("待審核績效_部門主管部門全搜_不搜自己_成功")
    @WithUserDetails(DEFAULT_USER_NAME)
    void performancePendingList_departmentManagerSearch_ok() throws Exception {
        //不同部門非本人
        ClientModel newClient1 = createDifferentDepartmentUser("testPerformance1", 3L);//
        PerformanceResponse performance1 = new PerformanceResponse(createPerformance(newClient1));
        //同部門非本人
        ClientModel newClient2 = createDifferentDepartmentUser("testPerformance2", me.getDepartment().getId());
        PerformanceResponse performance2 = new PerformanceResponse(createPerformance(newClient2));
        //本人
        PerformanceModel selfPerformance = createPerformance(me);
        refreshCache();

        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.PERFORMANCE.PENDING_LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));

        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        testUtils.comparePage(resultActions, 15, 1, 1, 1);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].id").value(performance2.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].event").value(performance2.getEvent()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].user.id").value(performance2.getUser().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].user.username").value(performance2.getUser().getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].fixedBonus").value(performance2.getFixedBonus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].performanceRatio").value(performance2.getPerformanceRatio()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].eventTime").value(performance2.getEventTime().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createTime").value(performance2.getCreateTime().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createBy").value(performance2.getCreateBy()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].status").value(performance2.getStatus()));

        clientRepository.deleteById(newClient1.getId());
        clientRepository.deleteById(newClient2.getId());
        performanceRepository.deleteById(performance1.getId());
        performanceRepository.deleteById(performance2.getId());
        performanceRepository.deleteById(selfPerformance.getId());
    }

    @Test
    @DisplayName("待審核績效_測試分頁2_成功")
    @WithUserDetails(DEFAULT_USER_NAME)
    void performancePendingList_page2_ok() throws Exception {
        //同部門非本人
        ClientModel newClient1 = createDifferentDepartmentUser("testPerformance1", me.getDepartment().getId());
        PerformanceResponse performance1 = new PerformanceResponse(createPerformance(newClient1));
        ClientModel newClient2 = createDifferentDepartmentUser("testPerformance2", me.getDepartment().getId());
        PerformanceResponse performance2 = new PerformanceResponse(createPerformance(newClient2));
        refreshCache();

        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.PERFORMANCE.PENDING_LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .param("pageSize", "1")
                .param("pageNum", "2")
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));

        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        testUtils.comparePage(resultActions, 1, 2, 2, 2);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].id").value(performance2.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].event").value(performance2.getEvent()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].user.id").value(performance2.getUser().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].user.username").value(performance2.getUser().getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].fixedBonus").value(performance2.getFixedBonus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].performanceRatio").value(performance2.getPerformanceRatio()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].eventTime").value(performance2.getEventTime().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createTime").value(performance2.getCreateTime().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createBy").value(performance2.getCreateBy()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].status").value(performance2.getStatus()));

        clientRepository.deleteById(newClient1.getId());
        clientRepository.deleteById(newClient2.getId());
        performanceRepository.deleteById(performance1.getId());
        performanceRepository.deleteById(performance2.getId());
    }

    @Test
    @DisplayName("績效清單_全搜_成功")
    @WithUserDetails(DEFAULT_USER_NAME)
    void performanceList_findAll_ok() throws Exception {
        //本人
        PerformanceResponse selfPerformance1 = new PerformanceResponse(createPerformance(me));
        PerformanceResponse selfPerformance2 = new PerformanceResponse(createPerformance(me));
        refreshCache();

        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.PERFORMANCE.LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));

        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        testUtils.comparePage(resultActions, 15, 1, 2, 1);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].id").value(selfPerformance1.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].event").value(selfPerformance1.getEvent()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].user.id").value(selfPerformance1.getUser().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].user.username").value(selfPerformance1.getUser().getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].fixedBonus").value(selfPerformance1.getFixedBonus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].performanceRatio").value(selfPerformance1.getPerformanceRatio()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].eventTime").value(selfPerformance1.getEventTime().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createTime").value(selfPerformance1.getCreateTime().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createBy").value(selfPerformance1.getCreateBy()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].status").value(selfPerformance1.getStatus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].id").value(selfPerformance2.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].event").value(selfPerformance2.getEvent()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].user.id").value(selfPerformance2.getUser().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].user.username").value(selfPerformance2.getUser().getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].fixedBonus").value(selfPerformance2.getFixedBonus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].performanceRatio").value(selfPerformance2.getPerformanceRatio()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].eventTime").value(selfPerformance2.getEventTime().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].createTime").value(selfPerformance2.getCreateTime().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].createBy").value(selfPerformance2.getCreateBy()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].status").value(selfPerformance2.getStatus()));

        performanceRepository.deleteById(selfPerformance1.getId());
        performanceRepository.deleteById(selfPerformance2.getId());
    }

    @Test
    @DisplayName("績效清單_指定用戶_成功")
    @WithUserDetails(DEFAULT_USER_NAME)
    void performanceList_findByUser_ok() throws Exception {
        //同部門非本人
        ClientModel newClient1 = createDifferentDepartmentUser("testPerformance1", me.getDepartment().getId());
        PerformanceResponse performance1 = new PerformanceResponse(createPerformance(newClient1));
        //本人
        PerformanceResponse selfPerformance2 = new PerformanceResponse(createPerformance(me));
        refreshCache();

        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.PERFORMANCE.LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .param("userId", String.valueOf(me.getId()))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));

        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        testUtils.comparePage(resultActions, 15, 1, 1, 1);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].id").value(selfPerformance2.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].event").value(selfPerformance2.getEvent()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].user.id").value(selfPerformance2.getUser().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].user.username").value(selfPerformance2.getUser().getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].fixedBonus").value(selfPerformance2.getFixedBonus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].performanceRatio").value(selfPerformance2.getPerformanceRatio()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].eventTime").value(selfPerformance2.getEventTime().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createTime").value(selfPerformance2.getCreateTime().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createBy").value(selfPerformance2.getCreateBy()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].status").value(selfPerformance2.getStatus()));
        clientRepository.deleteById(newClient1.getId());
        performanceRepository.deleteById(performance1.getId());
        performanceRepository.deleteById(selfPerformance2.getId());
    }

    @Test
    @DisplayName("績效清單_測試分頁_成功")
    @WithUserDetails(DEFAULT_USER_NAME)
    void performanceList_testPaging_ok() throws Exception {
        //同部門非本人
        ClientModel newClient1 = createDifferentDepartmentUser("testPerformance1", me.getDepartment().getId());
        PerformanceResponse performance1 = new PerformanceResponse(createPerformance(newClient1));
        //本人
        PerformanceResponse selfPerformance2 = new PerformanceResponse(createPerformance(me));
        refreshCache();

        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.PERFORMANCE.LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .param("pageSize", "1")
                .param("pageNum", "2")
                .param("sort", "2")
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));

        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        testUtils.comparePage(resultActions, 1, 2, 2, 2);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].id").value(performance1.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].event").value(performance1.getEvent()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].user.id").value(performance1.getUser().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].user.username").value(performance1.getUser().getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].fixedBonus").value(performance1.getFixedBonus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].performanceRatio").value(performance1.getPerformanceRatio()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].eventTime").value(performance1.getEventTime().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createTime").value(performance1.getCreateTime().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createBy").value(performance1.getCreateBy()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].status").value(performance1.getStatus()));
        clientRepository.deleteById(newClient1.getId());
        performanceRepository.deleteById(performance1.getId());
        performanceRepository.deleteById(selfPerformance2.getId());
    }

    private void refreshCache(){
        entityManager.flush();
        entityManager.clear();
        cacheService.refreshAllCache();
    }

    private PerformanceModel createPerformance(ClientModel model){
        PerformanceModel performanceModel = new PerformanceModel();
        performanceModel.setEvent("測試績效:" + model.getUsername());
        performanceModel.setUser(model);
        performanceModel.setFixedBonus(new BigDecimal(1000));
        performanceModel.setPerformanceRatio(new BigDecimal("0.5"));
        performanceModel.setEventTime(DateTool.now());
        performanceModel.setCreateBy(me);
        performanceModel.setStatus(StatusConstant.PENDING_NO);
        performanceRepository.save(performanceModel);
        return performanceModel;
    }

    private ClientModel createDifferentDepartmentUser(String key, long departmentId){
        ClientModel newClient = new ClientModel();
        newClient.setUsername(key);
        newClient.setPassword(key);
        newClient.setDepartment(new DepartmentModel(departmentId));
        newClient = clientRepository.save(newClient);
        return newClient;
    }

    private void updateRoleLevel(int level){
        Optional<RoleModel> roleOptional = roleRepository.findById(2L);
        Assertions.assertTrue(roleOptional.isPresent());
        RoleModel roleModel = roleOptional.get();
        roleModel.setLevel(level);
        roleRepository.save(roleModel);
    }
}