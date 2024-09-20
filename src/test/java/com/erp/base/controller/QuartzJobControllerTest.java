package com.erp.base.controller;

import com.erp.base.testConfig.TestUtils;
import com.erp.base.testConfig.redis.TestRedisConfiguration;
import com.erp.base.model.constant.response.ApiResponseCode;
import com.erp.base.model.dto.request.IdRequest;
import com.erp.base.model.dto.request.quartzJob.QuartzJobRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.dto.response.QuartzJobResponse;
import com.erp.base.model.entity.AttendModel;
import com.erp.base.model.entity.QuartzJobModel;
import com.erp.base.repository.AttendRepository;
import com.erp.base.repository.QuartzJobRepository;
import com.erp.base.service.QuartzJobService;
import com.erp.base.tool.ObjectTool;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
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

@SpringBootTest(classes = TestRedisConfiguration.class)
@TestPropertySource(locations = {
        "classpath:application-redis-test.properties",
        "classpath:application-quartz-test.properties"
})
@AutoConfigureMockMvc
@DirtiesContext
class QuartzJobControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TestUtils testUtils;
    @Autowired
    private QuartzJobService quartzJobService;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private QuartzJobRepository quartzJobRepository;
    @Autowired
    private Scheduler scheduler;
    @Autowired
    private AttendRepository attendRepository;
    private static final long DEFAULT_UID = 1L;

    @Test
    @DisplayName("排程清單_成功")
    void quartzJobList_ok() throws Exception {
        quartzJobRepository.deleteAll();
        QuartzJobResponse quartzJob1 = new QuartzJobResponse(createQuartzJob());
        QuartzJobResponse quartzJob2 = new QuartzJobResponse(createQuartzJob());
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.QUARTZ_JOB.LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].id").value(quartzJob1.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].name").value(quartzJob1.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].group").value(quartzJob1.getGroup()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].cron").value(quartzJob1.getCron()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].param").value(quartzJob1.getParam()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].info").value(quartzJob1.getInfo()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].classPath").value(quartzJob1.getClassPath()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].status").value(quartzJob1.isStatus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].id").value(quartzJob2.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].name").value(quartzJob2.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].group").value(quartzJob2.getGroup()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].cron").value(quartzJob2.getCron()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].param").value(quartzJob2.getParam()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].info").value(quartzJob2.getInfo()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].classPath").value(quartzJob2.getClassPath()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].status").value(quartzJob2.isStatus()));
        quartzJobRepository.deleteById(quartzJob1.getId());
        quartzJobRepository.deleteById(quartzJob2.getId());
    }

    @Test
    @DisplayName("新增排程_找不到對應class_錯誤")
    void addQuartzJob_classNotFound_error() throws Exception {
        QuartzJobModel quartzJobModel = new QuartzJobModel();
        quartzJobModel.setName("測試排程");
        quartzJobModel.setGroupName(Scheduler.DEFAULT_GROUP);
        quartzJobModel.setCron("*/60 * * * * ?");
        quartzJobModel.setInfo("測試排程內容");
        quartzJobModel.setClassPath("zzz");
        ResponseEntity<ApiResponse> response = ApiResponse.errorMsgFormat(ApiResponseCode.CLASS_NOT_FOUND, "zzz");
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(Router.QUARTZ_JOB.ADD)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(quartzJobModel))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("新增排程_cron格式問題_錯誤")
    void addQuartzJob_unexpectedCron_error() throws Exception {
        QuartzJobModel quartzJobModel = new QuartzJobModel();
        quartzJobModel.setName("測試排程");
        quartzJobModel.setGroupName(Scheduler.DEFAULT_GROUP);
        quartzJobModel.setCron("");
        quartzJobModel.setInfo("測試排程內容");
        quartzJobModel.setClassPath("com.erp.base.config.quartz.job.TestJob");
        ResponseEntity<ApiResponse> response = ApiResponse.errorMsgFormat(ApiResponseCode.CRON_ERROR, quartzJobModel.getCron());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(Router.QUARTZ_JOB.ADD)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(quartzJobModel))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("新增排程_成功")
    @Transactional
    void addQuartzJob_ok() throws Exception {
        quartzJobRepository.deleteAll();
        QuartzJobModel quartzJobModel = new QuartzJobModel();
        quartzJobModel.setName("測試排程");
        quartzJobModel.setGroupName(Scheduler.DEFAULT_GROUP);
        quartzJobModel.setCron("*/30 * * * * ?");
        quartzJobModel.setInfo("測試排程內容");
        quartzJobModel.setClassPath("com.erp.base.config.quartz.job.TestJob");
        String name = quartzJobModel.getName();
        String groupName = quartzJobModel.getGroupName();
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(Router.QUARTZ_JOB.ADD)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(quartzJobModel))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
        List<QuartzJobModel> all = quartzJobRepository.findAll();
        Optional<QuartzJobModel> first = all.stream().filter(q -> q.getName().equals(name)).findFirst();
        Assertions.assertTrue(first.isPresent());
        QuartzJobModel model = first.get();
        Assertions.assertEquals(name, model.getName());
        Assertions.assertEquals(groupName, model.getGroupName());
        Assertions.assertEquals(quartzJobModel.getCron(), model.getCron());
        Assertions.assertEquals(quartzJobModel.getInfo(), model.getInfo());
        Assertions.assertEquals(quartzJobModel.getClassPath(), model.getClassPath());
        quartzJobRepository.deleteById(model.getId());

        List<? extends Trigger> triggers = scheduler.getTriggersOfJob(new JobKey(name, groupName));
        Assertions.assertEquals(1, triggers.size());
        Assertions.assertTrue(triggers.stream()
                .anyMatch(trigger ->
                        trigger.getKey().getName().equals(name) &&
                        trigger.getKey().getGroup().equals(groupName)
                )
        );
        quartzJobRepository.deleteFromTriggersByName(name);
        quartzJobRepository.deleteFromJobDetailsByName(name);
        quartzJobRepository.deleteFromCronTriggersByName(name);
        scheduler.clear();
    }

    @Test
    @DisplayName("更新排程_找不到對應class_錯誤")
    void updateQuartzJob_classNotFound_error() throws Exception {
        QuartzJobModel quartzJob = createQuartzJob();
        quartzJob.setClassPath("zzz");
        ResponseEntity<ApiResponse> response = ApiResponse.errorMsgFormat(ApiResponseCode.CLASS_NOT_FOUND, "zzz");
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.QUARTZ_JOB.UPDATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(quartzJob))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
        quartzJobRepository.deleteById(quartzJob.getId());
    }

    @Test
    @DisplayName("更新排程_cron格式問題_錯誤")
    void updateQuartzJob_unexpectedCron_error() throws Exception {
        QuartzJobModel quartzJob = createQuartzJob();
        quartzJob.setCron("");
        ResponseEntity<ApiResponse> response = ApiResponse.errorMsgFormat(ApiResponseCode.SCHEDULER_ERROR, "Unexpected end of expression.");
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.QUARTZ_JOB.UPDATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(quartzJob))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
        quartzJobRepository.deleteById(quartzJob.getId());
    }

    @Test
    @DisplayName("更新排程_未知Id_錯誤")
    void updateQuartzJob_unknownId_error() throws Exception {
        QuartzJobRequest request = new QuartzJobRequest();
        request.setId(99L);
        ResponseEntity<ApiResponse> response = ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "JobId Not Found.");
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.QUARTZ_JOB.UPDATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(request))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("更新排程_成功")
    @Transactional
    void updateQuartzJob_ok() throws Exception {
        QuartzJobModel quartzJob = createQuartzJob();
        CronTriggerFactoryBean trigger = quartzJobService.createTrigger(quartzJob);
        scheduler.scheduleJob((JobDetail) trigger.getJobDataMap().get("jobDetail"), trigger.getObject());
        String name = quartzJob.getName();
        String groupName = quartzJob.getGroupName();

        quartzJob.setCron("*/10 * * * * ?");
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.QUARTZ_JOB.UPDATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(quartzJob))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
        List<QuartzJobModel> all = quartzJobRepository.findAll();
        Optional<QuartzJobModel> first = all.stream().filter(q -> q.getName().equals(name)).findFirst();
        Assertions.assertTrue(first.isPresent());
        QuartzJobModel model = first.get();
        Assertions.assertEquals(name, model.getName());
        Assertions.assertEquals(groupName, model.getGroupName());
        Assertions.assertEquals(quartzJob.getCron(), model.getCron());
        Assertions.assertEquals(quartzJob.getInfo(), model.getInfo());
        Assertions.assertEquals(quartzJob.getClassPath(), model.getClassPath());
        quartzJobRepository.deleteById(model.getId());
        TriggerKey triggerKey = TriggerKey.triggerKey(name, groupName);
        CronTrigger updatedTrigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        Assertions.assertNotNull(updatedTrigger);
        Assertions.assertEquals("*/10 * * * * ?", updatedTrigger.getCronExpression());

        quartzJobRepository.deleteFromTriggersByName(name);
        quartzJobRepository.deleteFromJobDetailsByName(name);
        quartzJobRepository.deleteFromCronTriggersByName(name);
        scheduler.clear();
    }

    @Test
    @DisplayName("排程狀態切換_未知Id_錯誤")
    @Transactional
    void toggleQuartzJob_unknownId_error() throws Exception {
        IdRequest request = new IdRequest();
        request.setId(99L);
        ResponseEntity<ApiResponse> response = ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "JobId Not Found.");
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.QUARTZ_JOB.TOGGLE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(request))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("排程狀態切換_成功")
    @Transactional
    void toggleQuartzJob_ok() throws Exception {
        QuartzJobModel quartzJob = createQuartzJob();
        CronTriggerFactoryBean trigger = quartzJobService.createTrigger(quartzJob);
        scheduler.scheduleJob((JobDetail) trigger.getJobDataMap().get("jobDetail"), trigger.getObject());
        String name = quartzJob.getName();
        String groupName = quartzJob.getGroupName();
        JobKey jobKey = new JobKey(name, groupName);
        TriggerKey triggerKey = new TriggerKey(name, groupName);
        quartzJob.setStatus(false);
        quartzJobRepository.save(quartzJob);
        scheduler.pauseJob(jobKey);
        entityManager.flush();
        entityManager.clear();
        Assertions.assertFalse(quartzJob.isStatus());
        Assertions.assertEquals(Trigger.TriggerState.PAUSED, scheduler.getTriggerState(triggerKey));

        IdRequest request = new IdRequest();
        request.setId(quartzJob.getId());
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.QUARTZ_JOB.TOGGLE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(request))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
        entityManager.flush();
        entityManager.clear();
        Assertions.assertEquals(Trigger.TriggerState.NORMAL, scheduler.getTriggerState(triggerKey));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
        Assertions.assertEquals(Trigger.TriggerState.PAUSED, scheduler.getTriggerState(triggerKey));

        quartzJobRepository.deleteFromTriggersByName(name);
        quartzJobRepository.deleteFromJobDetailsByName(name);
        quartzJobRepository.deleteFromCronTriggersByName(name);
        quartzJobRepository.deleteById(quartzJob.getId());
        scheduler.clear();
    }

    @Test
    @DisplayName("刪除排程_成功")
    void deleteQuartzJob_ok() throws Exception {
        QuartzJobModel quartzJob = createQuartzJob();
        CronTriggerFactoryBean trigger = quartzJobService.createTrigger(quartzJob);
        scheduler.scheduleJob((JobDetail) trigger.getJobDataMap().get("jobDetail"), trigger.getObject());
        JobKey jobKey = new JobKey(quartzJob.getName(), quartzJob.getGroupName());
        Assertions.assertNotNull(scheduler.getJobDetail(jobKey));
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete(Router.QUARTZ_JOB.DELETE)
                .contentType(MediaType.APPLICATION_JSON)
                .param("id", String.valueOf(quartzJob.getId()))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
        Assertions.assertNull(scheduler.getJobDetail(jobKey));
        Optional<QuartzJobModel> byId = quartzJobRepository.findById(quartzJob.getId());
        Assertions.assertTrue(byId.isEmpty());
        quartzJobRepository.deleteById(quartzJob.getId());
    }

    @Test
    @DisplayName("單次觸發任務_測試每日重置_成功")
    void execQuartzJob_ok() throws Exception {
        List<AttendModel> all = attendRepository.findAll();
        Assertions.assertTrue(all.isEmpty());
        QuartzJobModel quartzJob = new QuartzJobModel();
        quartzJob.setName("測試排程");
        quartzJob.setGroupName(Scheduler.DEFAULT_GROUP);
        quartzJob.setCron("* * * */30 * ?");
        quartzJob.setInfo("測試排程內容");
        quartzJob.setClassPath("com.erp.base.config.quartz.job.AttendJob");
        quartzJob = quartzJobRepository.save(quartzJob);
        CronTriggerFactoryBean trigger = quartzJobService.createTrigger(quartzJob);
        scheduler.scheduleJob((JobDetail) trigger.getJobDataMap().get("jobDetail"), trigger.getObject());
        IdRequest idRequest = new IdRequest();
        idRequest.setId(quartzJob.getId());
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(Router.QUARTZ_JOB.EXEC)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(idRequest))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
        quartzJobRepository.deleteById(quartzJob.getId());
        scheduler.clear();
    }

    private QuartzJobModel createQuartzJob() {
        QuartzJobModel quartzJobModel = new QuartzJobModel();
        quartzJobModel.setName("測試排程");
        quartzJobModel.setGroupName(Scheduler.DEFAULT_GROUP);
        quartzJobModel.setCron("*/30 * * * * ?");
        quartzJobModel.setInfo("測試排程內容");
        quartzJobModel.setClassPath("com.erp.base.config.quartz.job.TestJob");
        return quartzJobRepository.save(quartzJobModel);
    }
}