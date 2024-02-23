package com.erp.base.controller;

import com.erp.base.config.TestUtils;
import com.erp.base.config.redis.TestRedisConfiguration;
import com.erp.base.enums.response.ApiResponseCode;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.entity.LogModel;
import com.erp.base.repository.LogRepository;
import com.erp.base.tool.DateTool;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest(classes = TestRedisConfiguration.class)
@TestPropertySource(locations = {
        "classpath:application-redis-test.properties",
        "classpath:application-quartz-test.properties"
})
@AutoConfigureMockMvc
@Transactional
@DirtiesContext
class LogControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TestUtils testUtils;
    @Autowired
    private LogRepository logRepository;
    private long sec = 0;
    private static final String DEFAULT_USER_NAME = "test";

    @Test
    @DisplayName("日誌清單_AOP插入成功_成功")
    @WithUserDetails(DEFAULT_USER_NAME)
    void logList_aopTest_ok() throws Exception {
        logRepository.deleteAll();
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.REFRESH_CACHE_SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.CACHE.REFRESH)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
        List<LogModel> all = logRepository.findAll();
        Assertions.assertEquals(1, all.size());
        LogModel logModel = all.get(0);
        Assertions.assertEquals(HttpStatus.OK.getReasonPhrase(), logModel.getResult());
        Assertions.assertEquals(true, logModel.getStatus());
        Assertions.assertEquals("test", logModel.getUserName());
        Assertions.assertEquals(Router.CACHE.REFRESH, logModel.getUrl());
        logRepository.deleteById(logModel.getId());
    }

    @Test
    @DisplayName("日誌清單_測試AOP找不到用戶_錯誤")
    @WithMockUser(authorities = "CACHE_FRESH")
    void logList_userNotFound_error() throws Exception {
        logRepository.deleteAll();
        ResponseEntity<ApiResponse> response = ApiResponse.error(ApiResponseCode.USER_NOT_FOUND);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.CACHE.REFRESH)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("日誌清單_全搜_成功")
    @WithUserDetails(DEFAULT_USER_NAME)
    void logList_findAll_ok() throws Exception {
        logRepository.deleteAll();
        LogModel log1 = createLog(true, Router.CACHE.REFRESH, HttpStatus.OK.getReasonPhrase());
        sec++;
        LogModel log2 = createLog(true, Router.CLIENT.UPDATE, HttpStatus.OK.getReasonPhrase());
        sec++;
        LogModel log3 = createLog(false, Router.PERMISSION.BAN, ApiResponseCode.UNKNOWN_ERROR.getMessage());
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.LOG.LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        testUtils.comparePage(resultActions, 15, 1, 3, 1);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].id").value(log1.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].status").value(log1.getStatus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].user").value(log1.getUserName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].url").value(log1.getUrl()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].ip").value(log1.getIp()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].params").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].time").value(log1.getTime().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].result").value(log1.getResult()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].id").value(log2.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].status").value(log2.getStatus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].user").value(log2.getUserName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].url").value(log2.getUrl()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].ip").value(log2.getIp()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].params").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].time").value(log2.getTime().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].result").value(log2.getResult()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[2].id").value(log3.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[2].status").value(log3.getStatus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[2].user").value(log3.getUserName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[2].url").value(log3.getUrl()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[2].ip").value(log3.getIp()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[2].params").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[2].time").value(log3.getTime().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[2].result").value(log3.getResult()));
        logRepository.deleteById(log1.getId());
        logRepository.deleteById(log2.getId());
        logRepository.deleteById(log3.getId());
    }

    @Test
    @DisplayName("日誌清單_狀態搜尋_成功")
    @WithUserDetails(DEFAULT_USER_NAME)
    void logList_findByStatus_ok() throws Exception {
        logRepository.deleteAll();
        LogModel log1 = createLog(true, Router.CACHE.REFRESH, HttpStatus.OK.getReasonPhrase());
        sec++;
        LogModel log2 = createLog(true, Router.CLIENT.UPDATE, HttpStatus.OK.getReasonPhrase());
        sec++;
        LogModel log3 = createLog(false, Router.PERMISSION.BAN, ApiResponseCode.UNKNOWN_ERROR.getMessage());
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.LOG.LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .param("status", "false")
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        testUtils.comparePage(resultActions, 15, 1, 1, 1);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].id").value(log3.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].status").value(log3.getStatus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].user").value(log3.getUserName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].url").value(log3.getUrl()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].ip").value(log3.getIp()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].params").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].time").value(log3.getTime().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].result").value(log3.getResult()));
        logRepository.deleteById(log1.getId());
        logRepository.deleteById(log2.getId());
        logRepository.deleteById(log3.getId());
    }

    @Test
    @DisplayName("日誌清單_結束時間搜尋_時間排序_倒敘_成功")
    @WithUserDetails(DEFAULT_USER_NAME)
    void logList_findByEndTime_ok() throws Exception {
        logRepository.deleteAll();
        LogModel log1 = createLog(true, Router.CACHE.REFRESH, HttpStatus.OK.getReasonPhrase());
        sec++;
        LogModel log2 = createLog(true, Router.CLIENT.UPDATE, HttpStatus.OK.getReasonPhrase());
        sec++;
        LogModel log3 = createLog(false, Router.PERMISSION.BAN, ApiResponseCode.UNKNOWN_ERROR.getMessage());
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.LOG.LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .param("endTime", log2.getTime().toString())
                .param("sort", "2")
                .param("sortBy", "time")
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        testUtils.comparePage(resultActions, 15, 1, 2, 1);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].id").value(log2.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].status").value(log2.getStatus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].user").value(log2.getUserName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].url").value(log2.getUrl()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].ip").value(log2.getIp()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].params").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].time").value(log2.getTime().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].result").value(log2.getResult()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].id").value(log1.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].status").value(log1.getStatus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].user").value(log1.getUserName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].url").value(log1.getUrl()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].ip").value(log1.getIp()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].params").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].time").value(log1.getTime().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].result").value(log1.getResult()));
        logRepository.deleteById(log1.getId());
        logRepository.deleteById(log2.getId());
        logRepository.deleteById(log3.getId());
    }

    @Test
    @DisplayName("日誌清單_路徑搜尋_成功")
    @WithUserDetails(DEFAULT_USER_NAME)
    void logList_findByUrl_ok() throws Exception {
        logRepository.deleteAll();
        LogModel log1 = createLog(true, Router.CACHE.REFRESH, HttpStatus.OK.getReasonPhrase());
        sec++;
        LogModel log2 = createLog(true, Router.CLIENT.UPDATE, HttpStatus.OK.getReasonPhrase());
        sec++;
        LogModel log3 = createLog(false, Router.PERMISSION.BAN, ApiResponseCode.UNKNOWN_ERROR.getMessage());
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.LOG.LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .param("url", Router.PERMISSION.BAN)
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        testUtils.comparePage(resultActions, 15, 1, 1, 1);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].id").value(log3.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].status").value(log3.getStatus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].user").value(log3.getUserName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].url").value(log3.getUrl()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].ip").value(log3.getIp()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].params").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].time").value(log3.getTime().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].result").value(log3.getResult()));
        logRepository.deleteById(log1.getId());
        logRepository.deleteById(log2.getId());
        logRepository.deleteById(log3.getId());
    }

    private LogModel createLog(boolean status, String url, String result){
        LogModel entity = new LogModel();
        entity.setStatus(status);
        entity.setUserName(DEFAULT_USER_NAME);
        entity.setUrl(url);
        entity.setResult(result);
        entity.setIp("127.0.0.1");
        entity.setTime(DateTool.now().plusSeconds(sec));
        return logRepository.save(entity);
    }
}