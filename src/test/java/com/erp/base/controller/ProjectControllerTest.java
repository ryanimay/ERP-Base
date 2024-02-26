package com.erp.base.controller;

import com.erp.base.config.TestUtils;
import com.erp.base.config.redis.TestRedisConfiguration;
import com.erp.base.enums.response.ApiResponseCode;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.dto.response.ProjectResponse;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.entity.DepartmentModel;
import com.erp.base.model.entity.ProjectModel;
import com.erp.base.model.entity.RoleModel;
import com.erp.base.repository.ProjectRepository;
import com.erp.base.tool.DateTool;
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
    @DisplayName("專案清單_全搜_成功")
    @WithUserDetails(DEFAULT_USER_NAME)
    void project_findAll_ok() throws Exception {
        ProjectResponse projectResponse1 = new ProjectResponse(createProject(me, "1"));
        ProjectResponse projectResponse2 = new ProjectResponse(createProject(me, "2"));
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.PROJECT.LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        testUtils.comparePage(resultActions, 15, 1, 2, 1);
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
    @WithUserDetails(DEFAULT_USER_NAME)
    void project_findByType_ok() throws Exception {
        ProjectResponse projectResponse1 = new ProjectResponse(createProject(me, "1"));
        ProjectResponse projectResponse2 = new ProjectResponse(createProject(me, "2"));
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.PROJECT.LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .param("type", projectResponse2.getType())
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        testUtils.comparePage(resultActions, 15, 1, 1, 1);
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
    @WithUserDetails(DEFAULT_USER_NAME)
    void project_findByManager_ok() throws Exception {
        ProjectResponse projectResponse1 = new ProjectResponse(createProject(me, "1"));
        ProjectResponse projectResponse2 = new ProjectResponse(createProject(me, "2"));
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.PROJECT.LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .param("managerId", String.valueOf(me.getId()))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        testUtils.comparePage(resultActions, 15, 1, 2, 1);
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
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        testUtils.comparePage(resultActions, 15, 0, 0, 1);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data").isEmpty());
        projectRepository.deleteById(projectResponse1.getId());
        projectRepository.deleteById(projectResponse2.getId());
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