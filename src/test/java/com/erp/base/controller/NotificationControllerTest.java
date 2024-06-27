package com.erp.base.controller;

import com.erp.base.model.constant.NotificationEnum;
import com.erp.base.model.constant.response.ApiResponseCode;
import com.erp.base.model.dto.request.IdRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.entity.NotificationModel;
import com.erp.base.repository.NotificationRepository;
import com.erp.base.testConfig.TestUtils;
import com.erp.base.testConfig.redis.TestRedisConfiguration;
import com.erp.base.tool.ObjectTool;
import org.junit.jupiter.api.Assertions;
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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashSet;
import java.util.Optional;

@SpringBootTest(classes = TestRedisConfiguration.class)
@TestPropertySource(locations = {
        "classpath:application-redis-test.properties",
        "classpath:application-quartz-test.properties"
})
@AutoConfigureMockMvc
@DirtiesContext
class NotificationControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TestUtils testUtils;
    @Autowired
    private NotificationRepository notificationRepository;
    private static final long DEFAULT_UID = 1L;

    @Test
    @DisplayName("更新通知狀態_未知ID_錯誤")
    void status_unknownId_error() throws Exception {
        IdRequest request = new IdRequest();
        request.setId(999L);
        ResponseEntity<ApiResponse> response = ApiResponse.error(ApiResponseCode.INVALID_INPUT);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(Router.NOTIFICATION.STATUS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(request))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("更新通知狀態_成功")
    void status_ok() throws Exception {
        NotificationModel notification = NotificationModel.builder()
                .info(NotificationEnum.UPDATE_USER.getInfo("test"))
                .router(NotificationEnum.UPDATE_USER.getRouterName())
                .status(false)
                .global(NotificationEnum.UPDATE_USER.getGlobal())
                .createBy(1)
                .clients(new HashSet<>())
                .build();
        notificationRepository.save(notification);
        IdRequest request = new IdRequest();
        request.setId(notification.getId());
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(Router.NOTIFICATION.STATUS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(request))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
        Optional<NotificationModel> byId = notificationRepository.findById(notification.getId());
        Assertions.assertTrue(byId.isPresent());
        NotificationModel notificationModel = byId.get();
        Assertions.assertTrue(notificationModel.isStatus());
        Assertions.assertEquals(notification.getCreateBy(), notificationModel.getCreateBy());
        Assertions.assertEquals(notification.getInfo(), notificationModel.getInfo());
        Assertions.assertEquals(notification.getRouter(), notificationModel.getRouter());
        Assertions.assertEquals(notification.getCreateTime(), notificationModel.getCreateTime());
    }
}