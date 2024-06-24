package com.erp.base.controller;

import com.erp.base.testConfig.TestUtils;
import com.erp.base.testConfig.redis.TestRedisConfiguration;
import com.erp.base.model.constant.StatusConstant;
import com.erp.base.model.constant.response.ApiResponseCode;
import com.erp.base.model.dto.request.IdRequest;
import com.erp.base.model.dto.request.project.ProjectRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.dto.response.ProjectResponse;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.entity.DepartmentModel;
import com.erp.base.model.entity.ProjectModel;
import com.erp.base.model.entity.RoleModel;
import com.erp.base.repository.ProjectRepository;
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
class ProjectControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TestUtils testUtils;
    @Autowired
    private ProjectRepository projectRepository;
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
    @DisplayName("專案清單_全搜_成功")
    void projectList_findAll_ok() throws Exception {
        ProjectResponse projectResponse1 = new ProjectResponse(createProject(me, "1"));
        ProjectResponse projectResponse2 = new ProjectResponse(createProject(me, "2"));
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.PROJECT.LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        testUtils.comparePage(resultActions, 10, 1, 2, 1);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].id").value(projectResponse1.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].name").value(projectResponse1.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].type").value(projectResponse1.getType()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createTime").value(projectResponse1.getCreateTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createBy.id").value(projectResponse1.getCreateBy().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createBy.username").value(projectResponse1.getCreateBy().getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].startTime").value(projectResponse1.getStartTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].endTime").value(projectResponse1.getEndTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].scheduledStartTime").value(projectResponse1.getScheduledStartTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].scheduledEndTime").value(projectResponse1.getScheduledEndTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].info").value(projectResponse1.getInfo()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].manager.id").value(projectResponse1.getManager().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].manager.username").value(projectResponse1.getManager().getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].status").value(projectResponse1.getStatus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].id").value(projectResponse2.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].name").value(projectResponse2.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].type").value(projectResponse2.getType()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].createTime").value(projectResponse2.getCreateTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].createBy.id").value(projectResponse2.getCreateBy().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].createBy.username").value(projectResponse2.getCreateBy().getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].startTime").value(projectResponse2.getStartTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].endTime").value(projectResponse2.getEndTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].scheduledStartTime").value(projectResponse2.getScheduledStartTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].scheduledEndTime").value(projectResponse2.getScheduledEndTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].info").value(projectResponse2.getInfo()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].manager.id").value(projectResponse2.getManager().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].manager.username").value(projectResponse2.getManager().getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].status").value(projectResponse2.getStatus()));
        projectRepository.deleteById(projectResponse1.getId());
        projectRepository.deleteById(projectResponse2.getId());
    }

    @Test
    @DisplayName("專案清單_搜TYPE_成功")
    void projectList_findByType_ok() throws Exception {
        ProjectResponse projectResponse1 = new ProjectResponse(createProject(me, "1"));
        ProjectResponse projectResponse2 = new ProjectResponse(createProject(me, "2"));
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.PROJECT.LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .param("type", projectResponse2.getType())
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        testUtils.comparePage(resultActions, 10, 1, 1, 1);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].id").value(projectResponse2.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].name").value(projectResponse2.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].type").value(projectResponse2.getType()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createTime").value(projectResponse2.getCreateTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createBy.id").value(projectResponse2.getCreateBy().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createBy.username").value(projectResponse2.getCreateBy().getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].startTime").value(projectResponse2.getStartTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].endTime").value(projectResponse2.getEndTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].scheduledStartTime").value(projectResponse2.getScheduledStartTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].scheduledEndTime").value(projectResponse2.getScheduledEndTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].info").value(projectResponse2.getInfo()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].manager.id").value(projectResponse2.getManager().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].manager.username").value(projectResponse2.getManager().getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].status").value(projectResponse2.getStatus()));
        projectRepository.deleteById(projectResponse1.getId());
        projectRepository.deleteById(projectResponse2.getId());
    }

    @Test
    @DisplayName("專案清單_搜Manager_成功")
    void projectList_findByManager_ok() throws Exception {
        ProjectResponse projectResponse1 = new ProjectResponse(createProject(me, "1"));
        ProjectResponse projectResponse2 = new ProjectResponse(createProject(me, "2"));
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.PROJECT.LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .param("managerId", String.valueOf(me.getId()))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        testUtils.comparePage(resultActions, 10, 1, 2, 1);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].id").value(projectResponse1.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].name").value(projectResponse1.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].type").value(projectResponse1.getType()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createTime").value(projectResponse1.getCreateTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createBy.id").value(projectResponse1.getCreateBy().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createBy.username").value(projectResponse1.getCreateBy().getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].startTime").value(projectResponse1.getStartTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].endTime").value(projectResponse1.getEndTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].scheduledStartTime").value(projectResponse1.getScheduledStartTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].scheduledEndTime").value(projectResponse1.getScheduledEndTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].info").value(projectResponse1.getInfo()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].manager.id").value(projectResponse1.getManager().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].manager.username").value(projectResponse1.getManager().getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].status").value(projectResponse1.getStatus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].id").value(projectResponse2.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].name").value(projectResponse2.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].type").value(projectResponse2.getType()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].createTime").value(projectResponse2.getCreateTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].createBy.id").value(projectResponse2.getCreateBy().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].createBy.username").value(projectResponse2.getCreateBy().getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].startTime").value(projectResponse2.getStartTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].endTime").value(projectResponse2.getEndTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].scheduledStartTime").value(projectResponse2.getScheduledStartTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].scheduledEndTime").value(projectResponse2.getScheduledEndTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].info").value(projectResponse2.getInfo()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].manager.id").value(projectResponse2.getManager().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].manager.username").value(projectResponse2.getManager().getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].status").value(projectResponse2.getStatus()));
        requestBuilder = MockMvcRequestBuilders.get(Router.PROJECT.LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .param("managerId", String.valueOf(99))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        testUtils.comparePage(resultActions, 10, 0, 0, 1);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data").isEmpty());
        projectRepository.deleteById(projectResponse1.getId());
        projectRepository.deleteById(projectResponse2.getId());
    }

    @Test
    @DisplayName("新增專案_成功")
    void addProject_ok() throws Exception {
        ProjectRequest request = new ProjectRequest();
        request.setName("測試專案");
        request.setType("1");
        request.setScheduledStartTime(DateTool.now());
        request.setScheduledEndTime(DateTool.now());
        request.setInfo("測試專案內容");
        request.setManagerId(me.getId());
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(Router.PROJECT.ADD)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(request))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
        List<ProjectModel> all = projectRepository.findAll();
        Optional<ProjectModel> first = all.stream().filter(p -> p.getName().equals(request.getName())).findFirst();
        Assertions.assertTrue(first.isPresent());
        ProjectModel model = first.get();
        Assertions.assertEquals(request.getName(), model.getName());
        Assertions.assertEquals(request.getType(), model.getType());
        Assertions.assertEquals(request.getScheduledStartTime(), model.getScheduledStartTime());
        Assertions.assertEquals(request.getScheduledEndTime(), model.getScheduledEndTime());
        Assertions.assertEquals(request.getInfo(), model.getInfo());
        Assertions.assertEquals(request.getManagerId(), model.getManager().getId());
        projectRepository.deleteById(model.getId());
    }

    @Test
    @DisplayName("更新專案_成功")
    void updateProject_ok() throws Exception {
        ProjectModel project = createProject(me, "1");
        ProjectRequest request = new ProjectRequest();
        request.setId(project.getId());
        request.setName(project.getName() + "test");
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.PROJECT.UPDATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(request))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
        Optional<ProjectModel> first = projectRepository.findById(project.getId());
        Assertions.assertTrue(first.isPresent());
        ProjectModel model = first.get();
        Assertions.assertEquals(request.getName(), model.getName());
        Assertions.assertEquals(project.getType(), model.getType());
        Assertions.assertEquals(project.getScheduledStartTime(), model.getScheduledStartTime());
        Assertions.assertEquals(project.getScheduledEndTime(), model.getScheduledEndTime());
        Assertions.assertEquals(project.getInfo(), model.getInfo());
        Assertions.assertEquals(project.getManager().getId(), model.getManager().getId());
        projectRepository.deleteById(model.getId());
    }

    @Test
    @DisplayName("啟動專案_成功")
    void startProject_ok() throws Exception {
        ProjectModel project = createProject(me, "1");
        Assertions.assertEquals(StatusConstant.PENDING_NO, project.getStatus());
        IdRequest request = new IdRequest();
        request.setId(project.getId());
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.PROJECT.START)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(request))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
        entityManager.flush();
        entityManager.clear();
        Optional<ProjectModel> first = projectRepository.findById(project.getId());
        Assertions.assertTrue(first.isPresent());
        ProjectModel model = first.get();
        Assertions.assertEquals(StatusConstant.APPROVED_NO, model.getStatus());
        projectRepository.deleteById(model.getId());
    }

    @Test
    @DisplayName("啟動專案_未知Id_錯誤")
    void startProject_unknownId_ok() throws Exception {
        IdRequest request = new IdRequest();
        request.setId(99L);
        ResponseEntity<ApiResponse> response = ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "UPDATE FAILED, ID[" + 99 + "]");
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.PROJECT.START)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(request))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("專案結案_成功")
    void doneProject_ok() throws Exception {
        ProjectModel project = createProject(me, "1");
        project.setStatus(StatusConstant.APPROVED_NO);
        project = projectRepository.save(project);
        Assertions.assertEquals(StatusConstant.APPROVED_NO, project.getStatus());
        IdRequest request = new IdRequest();
        request.setId(project.getId());
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.PROJECT.DONE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(request))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
        entityManager.flush();
        entityManager.clear();
        Optional<ProjectModel> first = projectRepository.findById(project.getId());
        Assertions.assertTrue(first.isPresent());
        ProjectModel model = first.get();
        Assertions.assertEquals(StatusConstant.CLOSED_NO, model.getStatus());
        projectRepository.deleteById(model.getId());
    }

    @Test
    @DisplayName("專案結案_未執行不可結案_失敗")
    void doneProject_unApproved_error() throws Exception {
        ProjectModel project = createProject(me, "1");
        Assertions.assertEquals(StatusConstant.PENDING_NO, project.getStatus());
        IdRequest request = new IdRequest();
        request.setId(project.getId());
        ResponseEntity<ApiResponse> response = ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "UPDATE FAILED, ID[" + project.getId() + "]");
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.PROJECT.DONE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(request))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
        entityManager.flush();
        entityManager.clear();
        Optional<ProjectModel> first = projectRepository.findById(project.getId());
        Assertions.assertTrue(first.isPresent());
        ProjectModel model = first.get();
        Assertions.assertEquals(StatusConstant.PENDING_NO, model.getStatus());
        projectRepository.deleteById(model.getId());
    }

    private ProjectModel createProject(ClientModel model, String type){
        ProjectModel projectModel = new ProjectModel();
        projectModel.setName("專案名_" + model.getUsername());
        projectModel.setType(type);
        projectModel.setCreateBy(model);
        projectModel.setStartTime(DateTool.now());
        projectModel.setEndTime(DateTool.now());
        projectModel.setScheduledStartTime(DateTool.now());
        projectModel.setScheduledEndTime(DateTool.now());
        projectModel.setInfo("專案說明_" + model.getUsername());
        projectModel.setManager(model);
        return projectRepository.save(projectModel);
    }
}