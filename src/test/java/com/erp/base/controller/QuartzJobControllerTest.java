package com.erp.base.controller;

import com.erp.base.config.TestUtils;
import com.erp.base.config.redis.TestRedisConfiguration;
import com.erp.base.enums.response.ApiResponseCode;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.dto.response.QuartzJobResponse;
import com.erp.base.model.entity.QuartzJobModel;
import com.erp.base.repository.QuartzJobRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.quartz.Scheduler;
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

@SpringBootTest(classes = TestRedisConfiguration.class)
@TestPropertySource(locations = {
        "classpath:application-redis-test.properties",
        "classpath:application-quartz-test.properties"
})
@AutoConfigureMockMvc
@Transactional
@DirtiesContext
class QuartzJobControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TestUtils testUtils;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private QuartzJobRepository quartzJobRepository;
    private static final String DEFAULT_USER_NAME = "test";

    @Test
    @DisplayName("排程清單_成功")
    @WithUserDetails(DEFAULT_USER_NAME)
    void quartzJobList_ok() throws Exception {
        QuartzJobResponse quartzJob1 = new QuartzJobResponse(createQuartzJob());
        QuartzJobResponse quartzJob2 = new QuartzJobResponse(createQuartzJob());
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.QUARTZ_JOB.LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
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

    private QuartzJobModel createQuartzJob() {
        QuartzJobModel quartzJobModel = new QuartzJobModel();
        quartzJobModel.setName("測試排程");
        quartzJobModel.setGroupName(Scheduler.DEFAULT_GROUP);
        quartzJobModel.setCron("*/60 * * * * ?");
        quartzJobModel.setInfo("測試排程內容");
        quartzJobModel.setClassPath("com.erp.base.config.quartz.job.TestJob");
        return quartzJobRepository.save(quartzJobModel);
    }
}