package com.erp.base.controller;

import com.erp.base.model.constant.LeaveConstant;
import com.erp.base.model.constant.StatusConstant;
import com.erp.base.model.constant.response.ApiResponseCode;
import com.erp.base.model.dto.request.leave.LeaveAcceptRequest;
import com.erp.base.model.dto.request.leave.LeaveRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.dto.response.LeaveResponse;
import com.erp.base.model.dto.response.LeaveTypeResponse;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.entity.DepartmentModel;
import com.erp.base.model.entity.LeaveModel;
import com.erp.base.model.entity.RoleModel;
import com.erp.base.repository.ClientRepository;
import com.erp.base.repository.LeaveRepository;
import com.erp.base.repository.RoleRepository;
import com.erp.base.service.CacheService;
import com.erp.base.testConfig.TestUtils;
import com.erp.base.testConfig.redis.TestRedisConfiguration;
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
class LeaveControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TestUtils testUtils;
    @Autowired
    private LeaveRepository leaveRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private CacheService cacheService;
    @PersistenceContext
    private EntityManager entityManager;
    private static final long DEFAULT_UID = 1L;
    private static ClientModel me;

    @BeforeAll
    static void beforeAll(){
        me = new ClientModel(1L);
        me.setUsername("test");
        me.setRoles(Set.of(new RoleModel(2L)));
        me.setDepartment(new DepartmentModel(1L));
    }

    @Test
    @DisplayName("待審核假單_管理層不分部門全搜_不搜自己_成功")
    void leavePendingList_managerSearch_ok() throws Exception {
        //不同部門非本人
        ClientModel newClient1 = createDifferentDepartmentUser("testLeave1", 3L);//
        LeaveResponse otherLeave1 = new LeaveResponse(createLeave(newClient1));
        //同部門非本人
        ClientModel newClient2 = createDifferentDepartmentUser("testLeave2", me.getDepartment().getId());
        LeaveResponse otherLeave2 = new LeaveResponse(createLeave(newClient2));
        //本人
        LeaveModel selfLeave = createLeave(me);
        //權限設為level3
        updateRoleLevel(3);
        refreshCache();

        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.LEAVE.PENDING_LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));

        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        testUtils.comparePage(resultActions, 10, 1, 2, 1);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].id").value(otherLeave1.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].user.id").value(otherLeave1.getUser().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].user.username").value(otherLeave1.getUser().getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].type").value(otherLeave1.getType()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].startTime").value(otherLeave1.getStartTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].endTime").value(otherLeave1.getEndTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].status").value(otherLeave1.getStatus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].info").value(otherLeave1.getInfo()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createdTime").value(otherLeave1.getCreatedTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].id").value(otherLeave2.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].user.id").value(otherLeave2.getUser().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].user.username").value(otherLeave2.getUser().getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].type").value(otherLeave2.getType()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].startTime").value(otherLeave2.getStartTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].endTime").value(otherLeave2.getEndTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].status").value(otherLeave2.getStatus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].info").value(otherLeave2.getInfo()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].createdTime").value(otherLeave2.getCreatedTime()));

        clientRepository.deleteById(newClient1.getId());
        clientRepository.deleteById(newClient2.getId());
        leaveRepository.deleteById(otherLeave1.getId());
        leaveRepository.deleteById(otherLeave2.getId());
        leaveRepository.deleteById(selfLeave.getId());
    }

    @Test
    @DisplayName("待審核假單_部門主管部門全搜_不搜自己_成功")
    void leavePendingList_departmentManagerSearch_ok() throws Exception {
        //不同部門非本人
        ClientModel newClient1 = createDifferentDepartmentUser("testLeave1", 3L);//
        LeaveResponse otherLeave1 = new LeaveResponse(createLeave(newClient1));
        //同部門非本人
        ClientModel newClient2 = createDifferentDepartmentUser("testLeave2", me.getDepartment().getId());
        LeaveResponse otherLeave2 = new LeaveResponse(createLeave(newClient2));
        //本人
        LeaveModel selfLeave = createLeave(me);
        //權限設為level3
        updateRoleLevel(2);
        refreshCache();

        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.LEAVE.PENDING_LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));

        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        testUtils.comparePage(resultActions, 10, 1, 1, 1);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].id").value(otherLeave2.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].user.id").value(otherLeave2.getUser().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].user.username").value(otherLeave2.getUser().getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].type").value(otherLeave2.getType()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].startTime").value(otherLeave2.getStartTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].endTime").value(otherLeave2.getEndTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].status").value(otherLeave2.getStatus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].info").value(otherLeave2.getInfo()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createdTime").value(otherLeave2.getCreatedTime()));

        clientRepository.deleteById(newClient1.getId());
        clientRepository.deleteById(newClient2.getId());
        leaveRepository.deleteById(otherLeave1.getId());
        leaveRepository.deleteById(otherLeave2.getId());
        leaveRepository.deleteById(selfLeave.getId());
    }

    @Test
    @DisplayName("待審核假單_測試分頁2_成功")
    void leavePendingList_page2_ok() throws Exception {
        //同部門非本人
        ClientModel newClient1 = createDifferentDepartmentUser("testLeave1", me.getDepartment().getId());
        LeaveResponse otherLeave1 = new LeaveResponse(createLeave(newClient1));
        ClientModel newClient2 = createDifferentDepartmentUser("testLeave2", me.getDepartment().getId());
        LeaveResponse otherLeave2 = new LeaveResponse(createLeave(newClient2));
        //權限設為level3
        updateRoleLevel(2);
        refreshCache();

        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.LEAVE.PENDING_LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .param("pageSize", "1")
                .param("pageNum", "2")
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));

        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        testUtils.comparePage(resultActions, 1, 2, 2, 2);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].id").value(otherLeave2.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].user.id").value(otherLeave2.getUser().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].user.username").value(otherLeave2.getUser().getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].type").value(otherLeave2.getType()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].startTime").value(otherLeave2.getStartTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].endTime").value(otherLeave2.getEndTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].status").value(otherLeave2.getStatus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].info").value(otherLeave2.getInfo()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createdTime").value(otherLeave2.getCreatedTime()));

        clientRepository.deleteById(newClient1.getId());
        clientRepository.deleteById(newClient2.getId());
        leaveRepository.deleteById(otherLeave1.getId());
        leaveRepository.deleteById(otherLeave2.getId());
    }

    @Test
    @DisplayName("假單清單_特定id_成功")
    void leaveList_userId_ok() throws Exception {
        LeaveResponse selfLeave1 = new LeaveResponse(createLeave(me));
        LeaveResponse selfLeave2 = new LeaveResponse(createLeave(me));
        LeaveResponse selfLeave3 = new LeaveResponse(createLeave(me));
        refreshCache();
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.LEAVE.LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .param("userId", String.valueOf(me.getId()))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        testUtils.comparePage(resultActions, 10, 1, 3, 1);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].id").value(selfLeave1.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].user.id").value(selfLeave1.getUser().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].user.username").value(selfLeave1.getUser().getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].type").value(selfLeave1.getType()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].startTime").value(selfLeave1.getStartTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].endTime").value(selfLeave1.getEndTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].status").value(selfLeave1.getStatus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].info").value(selfLeave1.getInfo()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createdTime").value(selfLeave1.getCreatedTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].id").value(selfLeave2.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].user.id").value(selfLeave2.getUser().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].user.username").value(selfLeave2.getUser().getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].type").value(selfLeave2.getType()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].startTime").value(selfLeave2.getStartTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].endTime").value(selfLeave2.getEndTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].status").value(selfLeave2.getStatus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].info").value(selfLeave2.getInfo()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].createdTime").value(selfLeave2.getCreatedTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[2].id").value(selfLeave3.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[2].user.id").value(selfLeave3.getUser().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[2].user.username").value(selfLeave3.getUser().getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[2].type").value(selfLeave3.getType()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[2].startTime").value(selfLeave3.getStartTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[2].endTime").value(selfLeave3.getEndTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[2].status").value(selfLeave3.getStatus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[2].info").value(selfLeave3.getInfo()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[2].createdTime").value(selfLeave3.getCreatedTime()));
        leaveRepository.deleteById(selfLeave1.getId());
        leaveRepository.deleteById(selfLeave2.getId());
        leaveRepository.deleteById(selfLeave3.getId());
    }

    @Test
    @DisplayName("假單清單_搜尋時間_成功")
    void leaveList_searchTime_ok() throws Exception {
        LeaveResponse selfLeave1 = new LeaveResponse(createLeave(me));
        LeaveResponse selfLeave2 = new LeaveResponse(createLeave(me));
        LeaveResponse selfLeave3 = new LeaveResponse(createLeave(me));
        refreshCache();
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.LEAVE.LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .param("searchTime", DateTool.now().toString())
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        testUtils.comparePage(resultActions, 10, 1, 3, 1);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].id").value(selfLeave1.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].user.id").value(selfLeave1.getUser().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].user.username").value(selfLeave1.getUser().getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].type").value(selfLeave1.getType()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].startTime").value(selfLeave1.getStartTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].endTime").value(selfLeave1.getEndTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].status").value(selfLeave1.getStatus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].info").value(selfLeave1.getInfo()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createdTime").value(selfLeave1.getCreatedTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].id").value(selfLeave2.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].user.id").value(selfLeave2.getUser().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].user.username").value(selfLeave2.getUser().getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].type").value(selfLeave2.getType()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].startTime").value(selfLeave2.getStartTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].endTime").value(selfLeave2.getEndTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].status").value(selfLeave2.getStatus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].info").value(selfLeave2.getInfo()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].createdTime").value(selfLeave2.getCreatedTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[2].id").value(selfLeave3.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[2].user.id").value(selfLeave3.getUser().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[2].user.username").value(selfLeave3.getUser().getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[2].type").value(selfLeave3.getType()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[2].startTime").value(selfLeave3.getStartTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[2].endTime").value(selfLeave3.getEndTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[2].status").value(selfLeave3.getStatus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[2].info").value(selfLeave3.getInfo()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[2].createdTime").value(selfLeave3.getCreatedTime()));
        leaveRepository.deleteById(selfLeave1.getId());
        leaveRepository.deleteById(selfLeave2.getId());
        leaveRepository.deleteById(selfLeave3.getId());
    }

    @Test
    @DisplayName("新增假單_成功")
    void addLeave_ok() throws Exception {
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setType(2);
        leaveRequest.setInfo("測試新增");
        leaveRequest.setStartTime(DateTool.now().withHour(0).withMinute(0).withSecond(0));
        leaveRequest.setEndTime(DateTool.now().withHour(23).withMinute(59).withSecond(59));
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(Router.LEAVE.ADD)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(leaveRequest))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.user.id").value(me.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.user.username").value(me.getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.type.name").value(LeaveConstant.get(leaveRequest.getType())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.startTime").value(DateTool.format(leaveRequest.getStartTime())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.endTime").value(DateTool.format(leaveRequest.getEndTime())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.status").value(StatusConstant.get(StatusConstant.PENDING_NO)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.info").value(leaveRequest.getInfo()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.createdTime").isNotEmpty());
        leaveRepository.deleteAll();
    }

    @Test
    @DisplayName("更新假單_未知Id_錯誤")
    void updateLeave_unknownId_error() throws Exception {
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setId(99L);
        ResponseEntity<ApiResponse> response = ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "Id Not Found");
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.LEAVE.UPDATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(leaveRequest))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("更新假單_成功")
    void updateLeave_ok() throws Exception {
        LeaveModel leaveModel = createLeave(me);
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setId(leaveModel.getId());
        leaveRequest.setInfo("測試更新");
        leaveRequest.setType(3);
        leaveRequest.setStartTime(DateTool.now().withHour(0).withMinute(0).withSecond(0));
        leaveRequest.setEndTime(DateTool.now().withHour(23).withMinute(59).withSecond(59));
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.LEAVE.UPDATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(leaveRequest))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(leaveRequest.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.user.id").value(me.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.user.username").value(me.getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.type.name").value(LeaveConstant.get(leaveRequest.getType())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.startTime").value(DateTool.format(leaveRequest.getStartTime())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.endTime").value(DateTool.format(leaveRequest.getEndTime())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.status").value(StatusConstant.get(StatusConstant.PENDING_NO)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.info").value(leaveRequest.getInfo()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.createdTime").value(DateTool.format(leaveModel.getCreatedTime())));
        leaveRepository.deleteById(leaveModel.getId());
    }

    @Test
    @DisplayName("刪除假單_未知Id_錯誤")
    void deleteLeave_unknownId_error() throws Exception {
        ResponseEntity<ApiResponse> response = ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "Id Not Found");
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete(Router.LEAVE.DELETE)
                .contentType(MediaType.APPLICATION_JSON)
                .param("id", "99")
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("刪除假單_成功")
    void deleteLeave_ok() throws Exception {
        LeaveModel leaveModel = createLeave(me);
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete(Router.LEAVE.DELETE)
                .contentType(MediaType.APPLICATION_JSON)
                .param("id", String.valueOf(leaveModel.getId()))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
        entityManager.flush();
        entityManager.clear();
        Optional<LeaveModel> byId = leaveRepository.findById(leaveModel.getId());
        Assertions.assertTrue(byId.isEmpty());
        leaveRepository.deleteById(leaveModel.getId());
    }

    @Test
    @DisplayName("審核假單_未知Id_錯誤")
    void acceptLeave_unknownId_error() throws Exception {
        LeaveAcceptRequest leaveAcceptRequest = new LeaveAcceptRequest();
        leaveAcceptRequest.setId(99L);
        leaveAcceptRequest.setEventUserId(99L);
        ResponseEntity<ApiResponse> response = ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "Id Not Found");
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.LEAVE.ACCEPT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(leaveAcceptRequest))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("審核假單_成功")
    void acceptLeave_ok() throws Exception {
        LeaveModel leaveModel = createLeave(me);
        Assertions.assertEquals(StatusConstant.PENDING_NO, leaveModel.getStatus());
        LeaveAcceptRequest leaveAcceptRequest = new LeaveAcceptRequest();
        leaveAcceptRequest.setId(leaveModel.getId());
        leaveAcceptRequest.setEventUserId(leaveModel.getUser().getId());
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.LEAVE.ACCEPT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(leaveAcceptRequest))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
        entityManager.flush();
        entityManager.clear();
        Optional<LeaveModel> byId = leaveRepository.findById(leaveModel.getId());
        Assertions.assertTrue(byId.isPresent());
        LeaveModel model = byId.get();
        Assertions.assertEquals(StatusConstant.APPROVED_NO, model.getStatus());
        leaveRepository.deleteById(leaveModel.getId());
    }

    @Test
    @DisplayName("駁回假單_未知Id_錯誤")
    void rejectLeave_unknownId_error() throws Exception {
        LeaveAcceptRequest leaveAcceptRequest = new LeaveAcceptRequest();
        leaveAcceptRequest.setId(99L);
        leaveAcceptRequest.setEventUserId(99L);
        ResponseEntity<ApiResponse> response = ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "Id Not Found");
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.LEAVE.REJECT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(leaveAcceptRequest))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("駁回假單_成功")
    void rejectLeave_ok() throws Exception {
        LeaveModel leaveModel = createLeave(me);
        Assertions.assertEquals(StatusConstant.PENDING_NO, leaveModel.getStatus());
        LeaveAcceptRequest leaveAcceptRequest = new LeaveAcceptRequest();
        leaveAcceptRequest.setId(leaveModel.getId());
        leaveAcceptRequest.setEventUserId(leaveModel.getUser().getId());
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.LEAVE.REJECT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(leaveAcceptRequest))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
        entityManager.flush();
        entityManager.clear();
        Optional<LeaveModel> byId = leaveRepository.findById(leaveModel.getId());
        Assertions.assertTrue(byId.isPresent());
        LeaveModel model = byId.get();
        Assertions.assertEquals(StatusConstant.REMOVED_NO, model.getStatus());
        leaveRepository.deleteById(leaveModel.getId());
    }

    @Test
    @DisplayName("請假類別清單_成功")
    void leaveTypeList_ok() throws Exception {
        List<LeaveTypeResponse> list = LeaveConstant.list();
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.LEAVE.TYPE_LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0]").value(list.get(0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1]").value(list.get(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[2]").value(list.get(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[3]").value(list.get(3)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[4]").value(list.get(4)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[5]").value(list.get(5)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[6]").value(list.get(6)));
    }

    private void refreshCache(){
        entityManager.flush();
        entityManager.clear();
        cacheService.refreshAllCache();
    }

    private LeaveModel createLeave(ClientModel model){
        LeaveModel leaveEntity = new LeaveModel();
        leaveEntity.setUser(model);
        leaveEntity.setType(2);
        leaveEntity.setStartTime(DateTool.now().withHour(0).withMinute(0).withSecond(0));
        leaveEntity.setEndTime(DateTool.now().withHour(23).withMinute(59).withSecond(59));
        leaveEntity.setInfo("測試請假:" + model.getUsername());
        leaveRepository.save(leaveEntity);
        return leaveEntity;
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