package com.erp.base.controller;

import com.erp.base.testConfig.TestUtils;
import com.erp.base.testConfig.redis.TestRedisConfiguration;
import com.erp.base.enums.StatusConstant;
import com.erp.base.enums.response.ApiResponseCode;
import com.erp.base.model.dto.request.job.JobRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.dto.response.JobResponse;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.entity.JobModel;
import com.erp.base.repository.ClientRepository;
import com.erp.base.repository.JobRepository;
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
class JobControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TestUtils testUtils;
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private ClientRepository clientRepository;
    @PersistenceContext
    private EntityManager entityManager;
    private static final String DEFAULT_USER_NAME = "test";
    private long testJobId;
    private long userJobId;
    private long trackingJobId;
    private static ClientModel me;

    @BeforeAll
    static void beforeAll(){
        me = new ClientModel(1L);
        me.setUsername(DEFAULT_USER_NAME);
    }

    @Test
    @DisplayName("任務卡清單_成功")
    void jobList_ok() throws Exception {
        ClientModel jobClient = clientRepository.save(createClientModel());
        testJobId = jobClient.getId();
        JobResponse userJobResponse = new JobResponse(jobRepository.save(createUserJob(me)));
        userJobId = userJobResponse.getId();
        JobResponse trackingJobResponse = new JobResponse(jobRepository.save(createTrackingJob(jobClient, me)));
        entityManager.flush();
        entityManager.clear();
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.JOB.LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.tracking[0].id").value(trackingJobResponse.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.tracking[0].info").value(trackingJobResponse.getInfo()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.tracking[0].username").value(trackingJobResponse.getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.tracking[0].startTime").value(trackingJobResponse.getStartTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.tracking[0].endTime").value(trackingJobResponse.getEndTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.tracking[0].createdTime").value(trackingJobResponse.getCreatedTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.tracking[0].createBy").value(trackingJobResponse.getCreateBy()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.tracking[0].status").value(trackingJobResponse.getStatus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.tracking[0].order").value(trackingJobResponse.getOrder()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.tracking[0].trackingSet[0].id").value(me.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.tracking[0].trackingSet[0].username").value(me.getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.Pending[0].id").value(userJobResponse.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.Pending[0].info").value(userJobResponse.getInfo()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.Pending[0].username").value(userJobResponse.getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.Pending[0].startTime").value(userJobResponse.getStartTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.Pending[0].endTime").value(userJobResponse.getEndTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.Pending[0].createdTime").value(userJobResponse.getCreatedTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.Pending[0].createBy").value(userJobResponse.getCreateBy()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.Pending[0].status").value(userJobResponse.getStatus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.Pending[0].order").value(userJobResponse.getOrder()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.Pending[0].trackingSet").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.Pending[0].trackingSet").isEmpty());
        clearData();
    }

    @Test
    @DisplayName("新增任務卡_成功")
    void addJob_ok() throws Exception {
        ClientModel jobClient = clientRepository.save(createClientModel());
        testJobId = jobClient.getId();
        JobRequest jobRequest = new JobRequest();
        jobRequest.setInfo("測試任務卡內容1");
        jobRequest.setUserId(1L);
        jobRequest.setStartTime(DateTool.now());
        jobRequest.setEndTime(DateTool.now());
        jobRequest.setIdSet(Set.of(jobClient.getId()));
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(Router.JOB.ADD)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(jobRequest))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
        List<JobModel> list = jobRepository.findByUserOrTracking(me);
        Optional<JobModel> model = list.stream().filter(m -> m.getInfo().equals("測試任務卡內容1")).findFirst();
        Assertions.assertTrue(model.isPresent());
        JobModel jobModel = model.get();
        Assertions.assertEquals("測試任務卡內容1", jobModel.getInfo());
        Assertions.assertEquals(1L, jobModel.getUser().getId());
        Assertions.assertEquals(jobRequest.getStartTime(), jobModel.getStartTime());
        Assertions.assertEquals(jobRequest.getEndTime(), jobModel.getEndTime());
        clearData();
    }

    @Test
    @DisplayName("編輯任務卡_未知ID_錯誤")
    void updateJob_unknownId_error() throws Exception {
        JobRequest jobRequest = new JobRequest();
        jobRequest.setId(99L);
        jobRequest.setInfo("錯誤測試");
        jobRequest.setUserId(1L);
        ResponseEntity<ApiResponse> response = ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "Id Not Found.");
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.JOB.UPDATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(jobRequest))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("編輯任務卡_成功")
    void updateJob_ok() throws Exception {
        JobModel userJob = jobRepository.save(createUserJob(me));
        userJobId = userJob.getId();
        JobRequest jobRequest = new JobRequest();
        jobRequest.setId(userJobId);
        jobRequest.setInfo(userJob.getInfo() + "test");
        jobRequest.setUserId(me.getId());
        jobRequest.setStartTime(DateTool.now());
        jobRequest.setEndTime(DateTool.now());
        jobRequest.setStatus(2);
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.JOB.UPDATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(jobRequest))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        entityManager.flush();
        entityManager.clear();
        Optional<JobModel> byId = jobRepository.findById(userJobId);
        Assertions.assertTrue(byId.isPresent());
        JobResponse jobModel = new JobResponse(byId.get());
        Assertions.assertEquals(jobModel.getId(), userJobId);
        Assertions.assertEquals(jobModel.getInfo(), jobRequest.getInfo());
        Assertions.assertEquals(jobModel.getUsername(), me.getUsername());
        Assertions.assertEquals(jobModel.getStartTime(), DateTool.format(jobRequest.getStartTime()));
        Assertions.assertEquals(jobModel.getEndTime(), DateTool.format(jobRequest.getEndTime()));
        Assertions.assertEquals(jobModel.getCreatedTime(), DateTool.format(userJob.getCreatedTime()));
        Assertions.assertEquals(jobModel.getCreateBy(), DEFAULT_USER_NAME);
        Assertions.assertEquals(jobModel.getStatus(), StatusConstant.get(2));
        clearData();
    }

    @Test
    @DisplayName("刪除任務卡_成功")
    void deleteJob_ok() throws Exception {
        JobModel userJob = jobRepository.save(createUserJob(me));
        userJobId = userJob.getId();
        Assertions.assertNotNull(userJob);
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete(Router.JOB.REMOVE)
                .contentType(MediaType.APPLICATION_JSON)
                .param("id", String.valueOf(userJobId))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        entityManager.flush();
        entityManager.clear();
        Optional<JobModel> byId = jobRepository.findById(userJobId);
        Assertions.assertTrue(byId.isEmpty());
    }

    private ClientModel createClientModel(){
        ClientModel jobClient = new ClientModel();
        jobClient.setUsername("testJob");
        jobClient.setPassword("testJob");
        return jobClient;
    }

    private JobModel createUserJob(ClientModel me){
        JobModel userJob = new JobModel();
        userJob.setInfo("測試本人任務");
        userJob.setUser(me);
        userJob.setOrder(1);
        userJob.setCreateBy(me);
        return userJob;
    }

    private JobModel createTrackingJob(ClientModel jobClient, ClientModel me){
        JobModel trackingJob = new JobModel();
        trackingJob.setInfo("測試追蹤任務");
        trackingJob.setUser(jobClient);
        trackingJob.setTrackingList(Set.of(me));
        trackingJob.setOrder(2);
        trackingJob.setCreateBy(me);
        trackingJobId = trackingJob.getId();
        return trackingJob;
    }

    private void clearData(){
        clientRepository.deleteById(testJobId);
        jobRepository.deleteById(userJobId);
        jobRepository.deleteById(trackingJobId);
    }
}