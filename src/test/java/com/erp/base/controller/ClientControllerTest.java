package com.erp.base.controller;

import com.erp.base.config.TestUtils;
import com.erp.base.config.redis.TestRedisConfiguration;
import com.erp.base.enums.response.ApiResponseCode;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.dto.response.ClientResponseModel;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.repository.ClientRepository;
import com.erp.base.service.security.TokenService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import redis.embedded.RedisServer;

import java.util.Objects;

@SpringBootTest(classes = TestRedisConfiguration.class)
@TestPropertySource(locations = {
        "classpath:application-redis-test.properties",
        "classpath:application-quartz-test.properties"
})
@AutoConfigureMockMvc
@Transactional
class ClientControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RedisServer redisServer;
    @Autowired
    private ClientRepository repository;
    @Autowired
    private TokenService tokenService;
    private static ClientModel testModel;
    private static final String testJson = """
            {
            "username": "test",
            "password": "test",
            "createBy": 0
            }
            """;
    @Value("${spring.data.redis.port}")
    private int redisPort;

    @BeforeAll
    static void beforeAll() {
        testModel = new ClientModel();
        testModel.setUsername("test");
        testModel.setPassword("test");
    }

    @Test
    @DisplayName("測試redis連線_成功")
    void testRedisConnection() {
        Assertions.assertEquals(redisPort, redisServer.ports().get(0));
    }

    @Test
    @DisplayName("測試API_成功")
    void testApi_ok() throws Exception {
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.CLIENT.OP_VALID);
        TestUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("測試註冊_用戶名為空_錯誤")
    void register_requestUserNameBlank_error() throws Exception {
        ResponseEntity<ApiResponse> response = ApiResponse.error(HttpStatus.BAD_REQUEST, "用戶名不得為空");
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(Router.CLIENT.REGISTER)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}");
        TestUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("測試註冊_用戶名已存在_錯誤")
    void register_userNameExists_error() throws Exception {
        ClientModel save = repository.save(testModel);
        ResponseEntity<ApiResponse> response = ApiResponse.error(ApiResponseCode.USERNAME_ALREADY_EXIST);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(Router.CLIENT.REGISTER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(testJson);
        TestUtils.performAndExpect(mockMvc, requestBuilder, response);
        repository.deleteById(save.getId());
    }

    @Test
    @DisplayName("測試註冊_成功")
    void register_ok() throws Exception {
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.REGISTER_SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(Router.CLIENT.REGISTER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(testJson);
        TestUtils.performAndExpect(mockMvc, requestBuilder, response);
        ClientModel model = repository.findByUsername("test");
        Assertions.assertNotNull(model);
        System.out.println(model);
        repository.deleteById(model.getId());
    }

    @Test
    @DisplayName("測試登入_用戶名為空_錯誤")
    void login_requestUserNameBlank_error() throws Exception {
        ClientModel save = repository.save(testModel);
        ResponseEntity<ApiResponse> response = ApiResponse.error(HttpStatus.BAD_REQUEST, "用戶名不得為空");
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(Router.CLIENT.LOGIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"password": "test"}
                        """);
        TestUtils.performAndExpect(mockMvc, requestBuilder, response);
        repository.deleteById(save.getId());
    }

    @Test
    @DisplayName("測試登入_密碼為空_錯誤")
    void login_requestPasswordBlank_error() throws Exception {
        ClientModel save = repository.save(testModel);
        ResponseEntity<ApiResponse> response = ApiResponse.error(HttpStatus.BAD_REQUEST, "密碼不得為空");
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(Router.CLIENT.LOGIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"username": "test"}
                        """);
        TestUtils.performAndExpect(mockMvc, requestBuilder, response);
        repository.deleteById(save.getId());
    }

    @Test
    @DisplayName("測試登入_密碼錯誤_錯誤")
    void login_wrongPassword_error() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(Router.CLIENT.REGISTER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(testJson));
        ClientModel model = repository.findByUsername("test");
        ResponseEntity<ApiResponse> response = ApiResponse.error(ApiResponseCode.INVALID_LOGIN);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(Router.CLIENT.LOGIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"username": "test",
                        "password": "zzz"}
                        """);
        TestUtils.performAndExpect(mockMvc, requestBuilder, response);
        repository.deleteById(model.getId());
    }

    @Test
    @DisplayName("測試登入_不存在用戶_錯誤")
    void login_unknownUser_error() throws Exception {
        ResponseEntity<ApiResponse> response = ApiResponse.error(ApiResponseCode.INVALID_LOGIN);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(Router.CLIENT.LOGIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(testJson);
        TestUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("測試登入_成功")
    void login_ok() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(Router.CLIENT.REGISTER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(testJson));
        ClientModel model = repository.findByUsername("test");
        ResponseEntity<ApiResponse> response = ApiResponse.success(new ClientResponseModel(model));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(Router.CLIENT.LOGIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(testJson);
        ResultActions resultActions = TestUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.username").value("test"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.roleId[0]").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.email").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.lastLoginTime").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.createTime").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.createBy").value("System"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.mustUpdatePassword").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.attendStatus").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.department").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.active").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.lock").value(false))
                .andExpect(MockMvcResultMatchers.header().exists(HttpHeaders.AUTHORIZATION))
                .andExpect(MockMvcResultMatchers.header().exists(TokenService.REFRESH_TOKEN))
                .andDo(result -> {
                    String token = Objects.requireNonNull(result.getResponse().getHeader(HttpHeaders.AUTHORIZATION)).replace("Bearer ", "");
                    String refreshToken = result.getResponse().getHeader(TokenService.REFRESH_TOKEN);
                    Assertions.assertEquals("test", tokenService.parseToken(token).get(TokenService.TOKEN_PROPERTIES_USERNAME));
                    Assertions.assertEquals("test", tokenService.parseToken(refreshToken).get(TokenService.TOKEN_PROPERTIES_USERNAME));
                });
        repository.deleteById(model.getId());
    }
}