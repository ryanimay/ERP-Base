package com.erp.base.controller;

import com.erp.base.config.TestUtils;
import com.erp.base.config.redis.TestRedisConfiguration;
import com.erp.base.enums.response.ApiResponseCode;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.dto.response.ClientResponseModel;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.repository.ClientRepository;
import com.erp.base.service.CacheService;
import com.erp.base.service.MailService;
import com.erp.base.service.security.TokenService;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import redis.embedded.RedisServer;

import java.util.List;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;

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
    private TestUtils testUtils;
    @Autowired
    private RedisServer redisServer;
    @Autowired
    private ClientRepository repository;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private CacheService cacheService;
    @MockBean
    private MailService mailService;
    @PersistenceContext
    private EntityManager entityManager;
    private static ClientModel testModel;
    private static ClientModel testModel1;
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
        testModel.setEmail("testMail@gmail.com");
        testModel1 = new ClientModel();
        testModel1.setUsername("test1");
        testModel1.setPassword("test1");
        testModel1.setEmail("testMail1@gmail.com");
    }

    @BeforeEach
    void beforeEach() {
        cacheService.refreshAllCache();
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
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("測試註冊_用戶名為空_錯誤")
    void register_requestUserNameBlank_error() throws Exception {
        ResponseEntity<ApiResponse> response = ApiResponse.error(HttpStatus.BAD_REQUEST, "用戶名不得為空");
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(Router.CLIENT.REGISTER)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}");
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("測試註冊_用戶名已存在_錯誤")
    void register_userNameExists_error() throws Exception {
        ClientModel save = repository.save(testModel);
        ResponseEntity<ApiResponse> response = ApiResponse.error(ApiResponseCode.USERNAME_ALREADY_EXIST);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(Router.CLIENT.REGISTER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(testJson);
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
        repository.deleteById(save.getId());
    }

    @Test
    @DisplayName("測試註冊_成功")
    void register_ok() throws Exception {
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.REGISTER_SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(Router.CLIENT.REGISTER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(testJson);
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
        ClientModel model = repository.findByUsername("test");
        Assertions.assertNotNull(model);
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
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
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
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
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
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
        repository.deleteById(model.getId());
    }

    @Test
    @DisplayName("測試登入_不存在用戶_錯誤")
    void login_unknownUser_error() throws Exception {
        ResponseEntity<ApiResponse> response = ApiResponse.error(ApiResponseCode.INVALID_LOGIN);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(Router.CLIENT.LOGIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(testJson);
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
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
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
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

    @Test
    @DisplayName("重設密碼_用戶名為空_錯誤")
    void resetPassword_requestUserNameBlank_error() throws Exception {
        ResponseEntity<ApiResponse> response = ApiResponse.error(HttpStatus.BAD_REQUEST, "用戶名不得為空");
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.CLIENT.RESET_PASSWORD)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "email": "testtest@gmail.com"
                        }
                        """);
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("重設密碼_輸入用戶mail為空_錯誤")
    void resetPassword_requestEmailBlank_error() throws Exception {
        ResponseEntity<ApiResponse> response = ApiResponse.error(HttpStatus.BAD_REQUEST, "Email不得為空");
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.CLIENT.RESET_PASSWORD)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "username": "test"
                        }
                        """);
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("重設密碼_輸入用戶mail格式錯誤_錯誤")
    void resetPassword_invalidEmailFormat_error() throws Exception {
        ResponseEntity<ApiResponse> response = ApiResponse.error(HttpStatus.BAD_REQUEST, "Email格式錯誤");
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.CLIENT.RESET_PASSWORD)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "username": "test",
                        "email": "testMail"
                        }
                        """);
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("重設密碼_用戶不存在_錯誤")
    void resetPassword_userNotFound_error() throws Exception {
        ResponseEntity<ApiResponse> response = ApiResponse.error(ApiResponseCode.UNKNOWN_USER_OR_EMAIL);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.CLIENT.RESET_PASSWORD)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "username": "test",
                        "email": "testMail@gmail.com"
                        }
                        """);
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("重設密碼_email不存在_錯誤")
    void resetPassword_emailNotFound_error() throws Exception {
        ClientModel save = repository.save(testModel);
        ResponseEntity<ApiResponse> response = ApiResponse.error(ApiResponseCode.UNKNOWN_USER_OR_EMAIL);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.CLIENT.RESET_PASSWORD)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "username": "test",
                        "email": "test@gmail.com"
                        }
                        """);
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
        repository.deleteById(save.getId());
    }

    @Test
    @DisplayName("重設密碼_發送email異常_錯誤")
    void resetPassword_sendEmailException_error() throws Exception {
        Mockito.doThrow(MessagingException.class).when(mailService).sendMail(any(), any(), any(), any());
        ClientModel save = repository.save(testModel);
        ResponseEntity<ApiResponse> response = ApiResponse.error(ApiResponseCode.MESSAGING_ERROR);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.CLIENT.RESET_PASSWORD)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "username": "test",
                        "email": "testMail@gmail.com"
                        }
                        """);
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
        repository.deleteById(save.getId());
    }

    @Test
    @DisplayName("重設密碼_成功")
    void resetPassword_ok() throws Exception {
        ClientModel save = repository.save(testModel);
        ClientModel cacheClient = cacheService.getClient(testModel.getUsername());
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.RESET_PASSWORD_SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.CLIENT.RESET_PASSWORD)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "username": "test",
                        "email": "testMail@gmail.com"
                        }
                        """);
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
        //驗證資料庫資料
        entityManager.clear();//事務內清除內建緩存
        Assertions.assertNotEquals(save.getPassword(), repository.findByUsername(save.getUsername()).getPassword());
        //驗證緩存刷新
        Assertions.assertNotEquals(cacheClient.getPassword(), cacheService.getClient(testModel.getUsername()).getPassword());
        repository.deleteById(save.getId());
    }

    @Test
    @DisplayName("測試API權限_無JWT_錯誤")
    @WithMockUser(authorities = "CLIENT_LIST")
    void testApiPermission_noJwt_error() throws Exception {
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.INVALID_SIGNATURE);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.CLIENT.LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .content(testJson);
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("測試API權限_JWT錯誤_錯誤")
    @WithMockUser(authorities = "CLIENT_LIST")
    void testApiPermission_jwtError_error() throws Exception {
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.INVALID_SIGNATURE);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.CLIENT.LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .content(testJson);
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("測試API權限_無權限_錯誤")
    void testApiPermission_noAuth_error() throws Exception {
        ClientModel save = repository.save(testModel);
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.ACCESS_DENIED);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.CLIENT.LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .content(testJson)
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(save.getUsername()));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
        repository.deleteById(save.getId());
    }

    @Test
    @DisplayName("用戶清單_預設全搜_成功")
    @WithMockUser(authorities = "CLIENT_LIST")
    void clientList_findAll_ok() throws Exception {
        ClientResponseModel save = new ClientResponseModel(repository.save(testModel));
        ClientResponseModel save1 = new ClientResponseModel(repository.save(testModel1));
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.REGISTER_SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.CLIENT.LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(save.getUsername()));
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        testUtils.comparePage(resultActions, 15, 1, 2, 1);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].id").value(save.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].username").value(save.getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].roleId").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].email").value(save.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].lastLoginTime").value(save.getLastLoginTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createTime").value(save.getCreateTime().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createBy").value(save.getCreateBy()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].mustUpdatePassword").value(save.isMustUpdatePassword()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].attendStatus").value(save.getAttendStatus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].department").value(save.getDepartment()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].active").value(save.isActive()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].lock").value(save.isLock()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].id").value(save1.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].username").value(save1.getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].roleId").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].email").value(save1.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].lastLoginTime").value(save1.getLastLoginTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].createTime").value(save1.getCreateTime().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].createBy").value(save1.getCreateBy()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].mustUpdatePassword").value(save1.isMustUpdatePassword()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].attendStatus").value(save1.getAttendStatus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].department").value(save1.getDepartment()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].active").value(save1.isActive()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].lock").value(save1.isLock()));
        List<Long> roles = save.getRoleId();
        if (!roles.isEmpty()) {
            resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].roleId", Matchers.arrayContainingInAnyOrder(roles.toArray())));
        }
        List<Long> roles1 = save1.getRoleId();
        if (!roles1.isEmpty()) {
            resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].roleId", Matchers.arrayContainingInAnyOrder(roles1.toArray())));
        }
        repository.deleteAll();
    }

    @Test
    @DisplayName("用戶清單_id搜尋_成功")
    @WithMockUser(authorities = "CLIENT_LIST")
    void clientList_findById_ok() throws Exception {
        ClientModel save = repository.save(testModel);
        ClientResponseModel save1 = new ClientResponseModel(repository.save(testModel1));
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.REGISTER_SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.CLIENT.LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .param("type", "1")
                .param("id", String.valueOf(save1.getId()))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(save.getUsername()));
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        testUtils.comparePage(resultActions, 15, 1, 1, 1);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].id").value(save1.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].username").value(save1.getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].roleId").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].email").value(save1.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].lastLoginTime").value(save1.getLastLoginTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createTime").value(save1.getCreateTime().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createBy").value(save1.getCreateBy()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].mustUpdatePassword").value(save1.isMustUpdatePassword()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].attendStatus").value(save1.getAttendStatus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].department").value(save1.getDepartment()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].active").value(save1.isActive()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].lock").value(save1.isLock()));
        List<Long> roles1 = save1.getRoleId();
        if (!roles1.isEmpty()) {
            resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].roleId", Matchers.arrayContainingInAnyOrder(roles1.toArray())));
        }
        repository.deleteAll();
    }

    @Test
    @DisplayName("用戶清單_名稱搜尋_成功")
    @WithMockUser(authorities = "CLIENT_LIST")
    void clientList_findByName_ok() throws Exception {
        ClientModel save = repository.save(testModel);
        ClientResponseModel save1 = new ClientResponseModel(repository.save(testModel1));
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.REGISTER_SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.CLIENT.LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .param("type", "2")
                .param("name", save1.getUsername())
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(save.getUsername()));
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        testUtils.comparePage(resultActions, 15, 1, 1, 1);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].id").value(save1.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].username").value(save1.getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].roleId").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].email").value(save1.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].lastLoginTime").value(save1.getLastLoginTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createTime").value(save1.getCreateTime().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createBy").value(save1.getCreateBy()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].mustUpdatePassword").value(save1.isMustUpdatePassword()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].attendStatus").value(save1.getAttendStatus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].department").value(save1.getDepartment()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].active").value(save1.isActive()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].lock").value(save1.isLock()));
        List<Long> roles1 = save1.getRoleId();
        if (!roles1.isEmpty()) {
            resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].roleId", Matchers.arrayContainingInAnyOrder(roles1.toArray())));
        }
        repository.deleteAll();
    }

    @Test
    @DisplayName("測試分頁_搜一筆_成功")
    @WithMockUser(authorities = "CLIENT_LIST")
    void clientList_findFirst_ok() throws Exception {
        ClientResponseModel save = new ClientResponseModel(repository.save(testModel));
        repository.save(testModel1);
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.REGISTER_SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.CLIENT.LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .param("pageSize", "1")
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(save.getUsername()));
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        testUtils.comparePage(resultActions, 1, 2, 2, 1);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].id").value(save.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].username").value(save.getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].roleId").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].email").value(save.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].lastLoginTime").value(save.getLastLoginTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createTime").value(save.getCreateTime().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createBy").value(save.getCreateBy()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].mustUpdatePassword").value(save.isMustUpdatePassword()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].attendStatus").value(save.getAttendStatus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].department").value(save.getDepartment()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].active").value(save.isActive()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].lock").value(save.isLock()));
        List<Long> roles = save.getRoleId();
        if (!roles.isEmpty()) {
            resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].roleId", Matchers.arrayContainingInAnyOrder(roles.toArray())));
        }
        repository.deleteAll();
    }

    @Test
    @DisplayName("測試分頁_搜一筆倒序_成功")
    @WithMockUser(authorities = "CLIENT_LIST")
    void clientList_findFirstDesc_ok() throws Exception {
        repository.save(testModel);
        ClientResponseModel save = new ClientResponseModel(repository.save(testModel1));
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.REGISTER_SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.CLIENT.LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .param("pageSize", "1")
                .param("sort", "2")
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(save.getUsername()));
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        testUtils.comparePage(resultActions, 1, 2, 2, 1);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].id").value(save.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].username").value(save.getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].roleId").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].email").value(save.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].lastLoginTime").value(save.getLastLoginTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createTime").value(save.getCreateTime().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createBy").value(save.getCreateBy()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].mustUpdatePassword").value(save.isMustUpdatePassword()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].attendStatus").value(save.getAttendStatus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].department").value(save.getDepartment()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].active").value(save.isActive()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].lock").value(save.isLock()));
        List<Long> roles = save.getRoleId();
        if (!roles.isEmpty()) {
            resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].roleId", Matchers.arrayContainingInAnyOrder(roles.toArray())));
        }
        repository.deleteAll();
    }

    @Test
    @DisplayName("測試分頁_第二頁_成功")
    @WithMockUser(authorities = "CLIENT_LIST")
    void clientList_secPage_ok() throws Exception {
        repository.save(testModel);
        ClientResponseModel save = new ClientResponseModel(repository.save(testModel1));
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.REGISTER_SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.CLIENT.LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .param("pageSize", "1")
                .param("pageNum", "2")
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(save.getUsername()));
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        testUtils.comparePage(resultActions, 1, 2, 2, 2);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].id").value(save.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].username").value(save.getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].roleId").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].email").value(save.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].lastLoginTime").value(save.getLastLoginTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createTime").value(save.getCreateTime().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createBy").value(save.getCreateBy()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].mustUpdatePassword").value(save.isMustUpdatePassword()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].attendStatus").value(save.getAttendStatus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].department").value(save.getDepartment()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].active").value(save.isActive()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].lock").value(save.isLock()));
        List<Long> roles = save.getRoleId();
        if (!roles.isEmpty()) {
            resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].roleId", Matchers.arrayContainingInAnyOrder(roles.toArray())));
        }
        repository.deleteAll();
    }
}