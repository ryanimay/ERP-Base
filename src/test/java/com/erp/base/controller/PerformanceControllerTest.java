package com.erp.base.controller;

import com.erp.base.testConfig.TestUtils;
import com.erp.base.testConfig.redis.TestRedisConfiguration;
import com.erp.base.enums.StatusConstant;
import com.erp.base.enums.response.ApiResponseCode;
import com.erp.base.model.dto.request.performance.PerformanceAcceptRequest;
import com.erp.base.model.dto.request.performance.PerformanceRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.dto.response.ClientNameObject;
import com.erp.base.model.dto.response.PerformanceCalculateResponse;
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
import com.erp.base.tool.ObjectTool;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].eventTime").value(performance1.getEventTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createTime").value(performance1.getCreateTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createBy").value(performance1.getCreateBy()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].status").value(performance1.getStatus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].id").value(performance2.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].event").value(performance2.getEvent()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].user.id").value(performance2.getUser().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].user.username").value(performance2.getUser().getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].fixedBonus").value(performance2.getFixedBonus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].performanceRatio").value(performance2.getPerformanceRatio()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].eventTime").value(performance2.getEventTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].createTime").value(performance2.getCreateTime()))
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].eventTime").value(performance2.getEventTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createTime").value(performance2.getCreateTime()))
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].eventTime").value(performance2.getEventTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createTime").value(performance2.getCreateTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createBy").value(performance2.getCreateBy()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].status").value(performance2.getStatus()));

        clientRepository.deleteById(newClient1.getId());
        clientRepository.deleteById(newClient2.getId());
        performanceRepository.deleteById(performance1.getId());
        performanceRepository.deleteById(performance2.getId());
    }

    @Test
    @DisplayName("績效清單_全搜_成功")
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].eventTime").value(selfPerformance1.getEventTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createTime").value(selfPerformance1.getCreateTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createBy").value(selfPerformance1.getCreateBy()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].status").value(selfPerformance1.getStatus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].id").value(selfPerformance2.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].event").value(selfPerformance2.getEvent()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].user.id").value(selfPerformance2.getUser().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].user.username").value(selfPerformance2.getUser().getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].fixedBonus").value(selfPerformance2.getFixedBonus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].performanceRatio").value(selfPerformance2.getPerformanceRatio()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].eventTime").value(selfPerformance2.getEventTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].createTime").value(selfPerformance2.getCreateTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].createBy").value(selfPerformance2.getCreateBy()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].status").value(selfPerformance2.getStatus()));

        performanceRepository.deleteById(selfPerformance1.getId());
        performanceRepository.deleteById(selfPerformance2.getId());
    }

    @Test
    @DisplayName("績效清單_指定用戶_成功")
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].eventTime").value(selfPerformance2.getEventTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createTime").value(selfPerformance2.getCreateTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createBy").value(selfPerformance2.getCreateBy()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].status").value(selfPerformance2.getStatus()));
        clientRepository.deleteById(newClient1.getId());
        performanceRepository.deleteById(performance1.getId());
        performanceRepository.deleteById(selfPerformance2.getId());
    }

    @Test
    @DisplayName("績效清單_測試分頁_成功")
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].eventTime").value(performance1.getEventTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createTime").value(performance1.getCreateTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createBy").value(performance1.getCreateBy()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].status").value(performance1.getStatus()));
        clientRepository.deleteById(newClient1.getId());
        performanceRepository.deleteById(performance1.getId());
        performanceRepository.deleteById(selfPerformance2.getId());
    }

    @Test
    @DisplayName("新增績效_成功")
    void addPerformance_ok() throws Exception {
        PerformanceRequest performanceRequest = new PerformanceRequest();
        performanceRequest.setEvent("測試績效:" + me.getUsername());
        performanceRequest.setUserId(me.getId());
        performanceRequest.setFixedBonus(new BigDecimal(1000));
        performanceRequest.setPerformanceRatio(new BigDecimal("0.5"));
        performanceRequest.setEventTime(DateTool.now());

        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(Router.PERFORMANCE.ADD)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(performanceRequest))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
        entityManager.flush();
        entityManager.clear();
        List<PerformanceModel> all = performanceRepository.findAll();
        Optional<PerformanceModel> first = all.stream().filter(p -> p.getEvent().equals(performanceRequest.getEvent())).findFirst();
        Assertions.assertTrue(first.isPresent());
        PerformanceResponse model = new PerformanceResponse(first.get());
        Assertions.assertEquals(performanceRequest.getEvent(), model.getEvent());
        Assertions.assertEquals(performanceRequest.getUserId(), model.getUser().getId());
        Assertions.assertEquals(performanceRequest.getFixedBonus().toString(), model.getFixedBonus());
        Assertions.assertEquals(performanceRequest.getPerformanceRatio().toString(), model.getPerformanceRatio());
        Assertions.assertEquals(DateTool.format(performanceRequest.getEventTime()), model.getEventTime());
        Assertions.assertEquals(me.getUsername(), model.getCreateBy());
        Assertions.assertEquals(StatusConstant.get(StatusConstant.PENDING_NO), model.getStatus());
        performanceRepository.deleteById(model.getId());
    }

    @Test
    @DisplayName("更新績效_未知ID_錯誤")
    void updatePerformance_unknownId_error() throws Exception {
        ResponseEntity<ApiResponse> response = ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "Performance not found: id[" + 99 + "]");
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.PERFORMANCE.UPDATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "id": 99
                        }
                        """)
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("更新績效_只能更改Pending績效_錯誤")
    void updatePerformance_canOnlyModifyPerformancesInPending_error() throws Exception {
        PerformanceModel performance = createPerformance(me);
        performance.setStatus(StatusConstant.APPROVED_NO);
        performanceRepository.save(performance);
        entityManager.flush();
        entityManager.clear();

        PerformanceRequest performanceRequest = new PerformanceRequest();
        performanceRequest.setId(performance.getId());
        ResponseEntity<ApiResponse> response = ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "Can only modify performances in 'Pending' status.");
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.PERFORMANCE.UPDATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(performanceRequest))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
        performanceRepository.deleteById(performance.getId());
    }

    @Test
    @DisplayName("更新績效_成功")
    void updatePerformance_ok() throws Exception {
        PerformanceModel performance = createPerformance(me);
        PerformanceRequest performanceRequest = new PerformanceRequest();
        performanceRequest.setId(performance.getId());
        performanceRequest.setEvent(performance.getEvent() + "test");
        performanceRequest.setUserId(performance.getUser().getId());
        performanceRequest.setFixedBonus(performance.getFixedBonus());
        performanceRequest.setPerformanceRatio(performance.getPerformanceRatio());
        performanceRequest.setEventTime(performance.getEventTime().plusDays(10));
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.PERFORMANCE.UPDATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(performanceRequest))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
        Optional<PerformanceModel> byId = performanceRepository.findById(performance.getId());
        Assertions.assertTrue(byId.isPresent());
        PerformanceModel performanceModel = byId.get();
        Assertions.assertEquals(performanceRequest.getId(), performanceModel.getId());
        Assertions.assertEquals(performanceRequest.getEvent(), performanceModel.getEvent());
        Assertions.assertEquals(performanceRequest.getUserId(), performanceModel.getUser().getId());
        Assertions.assertEquals(performanceRequest.getFixedBonus(), performanceModel.getFixedBonus());
        Assertions.assertEquals(performanceRequest.getPerformanceRatio(), performanceModel.getPerformanceRatio());
        Assertions.assertEquals(performanceRequest.getEventTime(), performanceModel.getEventTime());
        Assertions.assertEquals(StatusConstant.PENDING_NO, performanceModel.getStatus());
        performanceRepository.deleteById(performance.getId());
    }

    @Test
    @DisplayName("刪除績效_未知ID_錯誤")
    void removePerformance_unknownId_error() throws Exception {
        ResponseEntity<ApiResponse> response = ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "Performance id[" + 99 + "] not found");
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete(Router.PERFORMANCE.REMOVE)
                .contentType(MediaType.APPLICATION_JSON)
                .param("eventId", "99")
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("刪除績效_成功")
    void removePerformance_ok() throws Exception {
        PerformanceModel performance = createPerformance(me);
        Optional<PerformanceModel> byId = performanceRepository.findById(performance.getId());
        Assertions.assertTrue(byId.isPresent());
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete(Router.PERFORMANCE.REMOVE)
                .contentType(MediaType.APPLICATION_JSON)
                .param("eventId", String.valueOf(performance.getId()))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
        entityManager.flush();
        entityManager.clear();
        byId = performanceRepository.findById(performance.getId());
        Assertions.assertTrue(byId.isPresent());
        PerformanceModel performanceModel = byId.get();
        Assertions.assertEquals(StatusConstant.REMOVED_NO, performanceModel.getStatus());
        performanceRepository.deleteById(performance.getId());
    }

    @Test
    @DisplayName("審核績效_未知ID_錯誤")
    void acceptPerformance_unknownId_error() throws Exception {
        PerformanceAcceptRequest performanceAcceptRequest = new PerformanceAcceptRequest();
        performanceAcceptRequest.setEventId(99L);
        ResponseEntity<ApiResponse> response = ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "Performance id[" + 99 + "] not found");
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.PERFORMANCE.ACCEPT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(performanceAcceptRequest))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("審核績效_成功")
    void acceptPerformance_ok() throws Exception {
        PerformanceModel performance = createPerformance(me);
        Optional<PerformanceModel> byId = performanceRepository.findById(performance.getId());
        Assertions.assertTrue(byId.isPresent());
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        PerformanceAcceptRequest performanceAcceptRequest = new PerformanceAcceptRequest();
        performanceAcceptRequest.setEventId(performance.getId());
        performanceAcceptRequest.setEventUserId(me.getId());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.PERFORMANCE.ACCEPT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(performanceAcceptRequest))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
        entityManager.flush();
        entityManager.clear();
        byId = performanceRepository.findById(performance.getId());
        Assertions.assertTrue(byId.isPresent());
        PerformanceModel performanceModel = byId.get();
        Assertions.assertEquals(StatusConstant.APPROVED_NO, performanceModel.getStatus());
        performanceRepository.deleteById(performance.getId());
    }

    @Test
    @DisplayName("統計年度績效_成功")
    void calculatePerformance_ok() throws Exception {
        PerformanceModel performance = createPerformance(me);
        performance.setStatus(StatusConstant.APPROVED_NO);
        performanceRepository.save(performance);
        entityManager.flush();
        entityManager.clear();
        PerformanceCalculateResponse performanceCalculateResponse = new PerformanceCalculateResponse();
        performanceCalculateResponse.setUser(new ClientNameObject(me));
        performanceCalculateResponse.setFixedBonus(performance.getFixedBonus());
        performanceCalculateResponse.setPerformanceRatio(performance.getPerformanceRatio());
        performanceCalculateResponse.setSettleYear(String.valueOf(performance.getCreateTime().getYear()));
        performanceCalculateResponse.setCount(1L);
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.PERFORMANCE.CALCULATE)
                .contentType(MediaType.APPLICATION_JSON)
                .param("id", String.valueOf(me.getId()))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.user.id").value(performanceCalculateResponse.getUser().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.user.username").value(performanceCalculateResponse.getUser().getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.fixedBonus").value(performanceCalculateResponse.getFixedBonus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.performanceRatio").value(performanceCalculateResponse.getPerformanceRatio()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.settleYear").value(performanceCalculateResponse.getSettleYear()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.count").value(performanceCalculateResponse.getCount()));
        performanceRepository.deleteById(performance.getId());
    }

    @Test
    @DisplayName("統計年度績效_未知用戶_成功")
    void calculatePerformance_unknownUser_ok() throws Exception {
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.PERFORMANCE.CALCULATE)
                .contentType(MediaType.APPLICATION_JSON)
                .param("id", String.valueOf(99L))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.user").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.fixedBonus").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.performanceRatio").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.settleYear").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.count").isEmpty());
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