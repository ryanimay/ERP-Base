package com.erp.base.controller;

import com.erp.base.model.dto.security.ClientIdentityDto;
import com.erp.base.testConfig.TestUtils;
import com.erp.base.testConfig.redis.TestRedisConfiguration;
import com.erp.base.model.constant.response.ApiResponseCode;
import com.erp.base.model.dto.request.client.ClientStatusRequest;
import com.erp.base.model.dto.request.client.UpdateClientInfoRequest;
import com.erp.base.model.dto.request.client.UpdatePasswordRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.dto.response.ClientNameObject;
import com.erp.base.model.dto.response.ClientResponseModel;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.entity.RoleModel;
import com.erp.base.repository.ClientRepository;
import com.erp.base.service.CacheService;
import com.erp.base.service.MailService;
import com.erp.base.service.security.TokenService;
import com.erp.base.tool.EncodeTool;
import com.erp.base.tool.ObjectTool;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
import redis.embedded.RedisServer;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(classes = TestRedisConfiguration.class)
@TestPropertySource(locations = {
        "classpath:application-redis-test.properties",
        "classpath:application-quartz-test.properties"
})
@AutoConfigureMockMvc
@Transactional
@DirtiesContext
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
    @SpyBean
    private EncodeTool encodeTool;
    @PersistenceContext
    private EntityManager entityManager;
    private static ClientModel testModel;
    private static final long DEFAULT_UID = 1L;
    private static final String testJson = """
            {
            
            "username": "testRegister",
            "password": "testRegister",
            "createBy": 0
            }
            """;
    @Value("${spring.data.redis.port}")
    private int redisPort;

    @BeforeAll
    static void beforeAll() {
        testModel = new ClientModel();
        testModel.setId(2L);
        testModel.setRoles(Set.of(new RoleModel(2)));
        testModel.setUsername("test1");
        testModel.setPassword("test1");
        testModel.setEmail("testMail1@gmail.com");
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
                .content(ObjectTool.toJson(testModel));
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
        ClientModel model = repository.findByUsername("testRegister");
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
        ResponseEntity<ApiResponse> response = ApiResponse.error(HttpStatus.BAD_REQUEST, "密碼不得為空");
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(Router.CLIENT.LOGIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"username": "test"}
                        """);
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("測試登入_密碼錯誤_錯誤")
    void login_wrongPassword_error() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(Router.CLIENT.REGISTER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(testJson));
        ClientModel model = repository.findByUsername("testRegister");
        ResponseEntity<ApiResponse> response = ApiResponse.error(ApiResponseCode.INVALID_LOGIN);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(Router.CLIENT.LOGIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"username": "testRegister",
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
    @DisplayName("測試登入_用戶遭鎖定_失敗")
    void login_userLock_error() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(Router.CLIENT.REGISTER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(testJson));
        ClientModel model = repository.findByUsername("testRegister");
        repository.lockClientByIdAndUsername(model.getId(), model.getUsername(), true);
        ResponseEntity<ApiResponse> response = ApiResponse.error(ApiResponseCode.CLIENT_LOCKED);
        entityManager.clear();
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(Router.CLIENT.LOGIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(testJson);
        testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        repository.deleteById(model.getId());
    }

    @Test
    @DisplayName("測試登入_用戶停用_失敗")
    void login_userStatusFalse_error() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(Router.CLIENT.REGISTER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(testJson));
        ClientModel model = repository.findByUsername("testRegister");
        repository.switchClientStatusByIdAndUsername(model.getId(), model.getUsername(), false);
        ResponseEntity<ApiResponse> response = ApiResponse.error(ApiResponseCode.CLIENT_DISABLED);
        entityManager.clear();
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(Router.CLIENT.LOGIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(testJson);
        testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        repository.deleteById(model.getId());
    }

    @Test
    @DisplayName("測試登入_不記住我_成功")
    void login_notRememberMe_ok() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(Router.CLIENT.REGISTER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(testJson));
        ClientModel model = repository.findByUsername("testRegister");
        ResponseEntity<ApiResponse> response = ApiResponse.success(new ClientResponseModel(model));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(Router.CLIENT.LOGIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(testJson);
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.username").value("testRegister"))
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
                .andExpect(MockMvcResultMatchers.header().doesNotExist(TokenService.REFRESH_TOKEN))
                .andDo(result -> {
                    String token = Objects.requireNonNull(result.getResponse().getHeader(HttpHeaders.AUTHORIZATION)).replace(TokenService.TOKEN_PREFIX, "");
                    Assertions.assertEquals((int) model.getId(), tokenService.parseToken(token).get(TokenService.TOKEN_PROPERTIES_UID));
                });
        repository.deleteById(model.getId());
    }

    @Test
    @DisplayName("測試登入_記住我_成功")
    void login_rememberMe_ok() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(Router.CLIENT.REGISTER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(testJson));
        ClientModel model = repository.findByUsername("testRegister");
        ResponseEntity<ApiResponse> response = ApiResponse.success(new ClientResponseModel(model));
        String testJson = """
            {
            "username": "testRegister",
            "password": "testRegister",
            "rememberMe": "true",
            "createBy": 0
            }
            """;
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(Router.CLIENT.LOGIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(testJson);
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.username").value("testRegister"))
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
                    String token = Objects.requireNonNull(result.getResponse().getHeader(HttpHeaders.AUTHORIZATION)).replace(TokenService.TOKEN_PREFIX, "");
                    String refreshToken = result.getResponse().getHeader(TokenService.REFRESH_TOKEN);
                    Assertions.assertEquals((int) model.getId(), tokenService.parseToken(token).get(TokenService.TOKEN_PROPERTIES_UID));
                    Assertions.assertEquals((int) model.getId(), tokenService.parseToken(refreshToken).get(TokenService.TOKEN_PROPERTIES_UID));
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
                        "username": "testRegister",
                        "email": "testMail@gmail.com"
                        }
                        """);
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("重設密碼_email不存在_錯誤")
    void resetPassword_emailNotFound_error() throws Exception {
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
    }

    @Test
    @DisplayName("重設密碼_發送email異常_錯誤")
    void resetPassword_sendEmailException_error() throws Exception {
        Mockito.doThrow(MessagingException.class).when(mailService).sendMail(any(), any(), any(), any());
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
    }

    @Test
    @DisplayName("重設密碼_成功")
    void resetPassword_ok() throws Exception {
        ClientModel save = repository.save(testModel);
        ClientIdentityDto cacheClient = cacheService.getClient(testModel.getId());
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.RESET_PASSWORD_SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.CLIENT.RESET_PASSWORD)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(testModel));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
        //驗證資料庫資料
        entityManager.clear();//事務內清除內建緩存
        Assertions.assertNotEquals(save.getPassword(), repository.findByUsername(save.getUsername()).getPassword());
        //驗證緩存刷新
        Assertions.assertNotEquals(cacheClient.getPassword(), cacheService.getClient(testModel.getId()).getPassword());
        repository.deleteById(save.getId());
    }

    @Test
    @DisplayName("測試API權限_無JWT_錯誤")
    void testApiPermission_noJwt_error() throws Exception {
        ResponseEntity<ApiResponse> response = ApiResponse.error(ApiResponseCode.ACCESS_DENIED);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.CLIENT.LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .content(testJson);
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("測試API權限_JWT錯誤_錯誤")
    void testApiPermission_jwtError_error() throws Exception {
        ResponseEntity<ApiResponse> response = ApiResponse.error(ApiResponseCode.ACCESS_DENIED);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.CLIENT.LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .content(testJson)
                .header(HttpHeaders.AUTHORIZATION, "testJWT");
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("測試API權限_無權限_錯誤")
    void testApiPermission_noAuth_error() throws Exception {
        editRole(1L);
        ResponseEntity<ApiResponse> response = ApiResponse.error(ApiResponseCode.ACCESS_DENIED);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.CLIENT.LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .content(testJson)
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
        editRole(2L);
    }

    @Test
    @DisplayName("用戶清單_預設全搜_成功")
    void clientList_findAll_ok() throws Exception {
        ClientResponseModel save = new ClientResponseModel(Objects.requireNonNull(repository.findById(DEFAULT_UID).orElse(null)));
        ClientResponseModel save1 = new ClientResponseModel(repository.save(testModel));
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.REGISTER_SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.CLIENT.LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(save.getId()));
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        testUtils.comparePage(resultActions, 10, 1, 2, 1);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].id").value(save.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].username").value(save.getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].roleId").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].email").value(save.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].lastLoginTime").value(save.getLastLoginTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createTime").value(save.getCreateTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createBy").value(save.getCreateBy()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].mustUpdatePassword").value(save.isMustUpdatePassword()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].attendStatus").value(save.getAttendStatus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].department.id").value(save.getDepartment().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].department.name").value(save.getDepartment().getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].active").value(save.isActive()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].lock").value(save.isLock()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].id").value(save1.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].username").value(save1.getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].roleId").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].email").value(save1.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].lastLoginTime").value(save1.getLastLoginTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].createTime").value(save1.getCreateTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].createBy").value(save1.getCreateBy()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].mustUpdatePassword").value(save1.isMustUpdatePassword()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].attendStatus").value(save1.getAttendStatus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].department").value(save1.getDepartment()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].active").value(save1.isActive()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].lock").value(save1.isLock()));
        repository.deleteById(save1.getId());
    }

    @Test
    @DisplayName("用戶清單_id搜尋_成功")
    void clientList_findById_ok() throws Exception {
        ClientResponseModel save = new ClientResponseModel(Objects.requireNonNull(repository.findById(DEFAULT_UID).orElse(null)));
        ClientModel save1 = repository.save(testModel);
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.REGISTER_SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.CLIENT.LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .param("type", "1")
                .param("id", String.valueOf(save.getId()))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(save.getId()));
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        testUtils.comparePage(resultActions, 10, 1, 1, 1);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].id").value(save.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].username").value(save.getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].roleId").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].email").value(save.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].lastLoginTime").value(save.getLastLoginTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createTime").value(save.getCreateTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createBy").value(save.getCreateBy()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].mustUpdatePassword").value(save.isMustUpdatePassword()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].attendStatus").value(save.getAttendStatus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].department.id").value(save.getDepartment().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].department.name").value(save.getDepartment().getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].active").value(save.isActive()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].lock").value(save.isLock()));
        repository.deleteById(save1.getId());
    }

    @Test
    @DisplayName("用戶清單_名稱搜尋_成功")
    void clientList_findByName_ok() throws Exception {
        ClientResponseModel save1 = new ClientResponseModel(repository.save(testModel));
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.REGISTER_SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.CLIENT.LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .param("type", "2")
                .param("name", save1.getUsername())
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        testUtils.comparePage(resultActions, 10, 1, 1, 1);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].id").value(save1.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].username").value(save1.getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].roleId").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].email").value(save1.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].lastLoginTime").value(save1.getLastLoginTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createTime").value(save1.getCreateTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createBy").value(save1.getCreateBy()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].mustUpdatePassword").value(save1.isMustUpdatePassword()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].attendStatus").value(save1.getAttendStatus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].department").value(save1.getDepartment()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].active").value(save1.isActive()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].lock").value(save1.isLock()));
        repository.deleteById(save1.getId());
    }

    @Test
    @DisplayName("測試分頁_搜一筆_成功")
    void clientList_findFirst_ok() throws Exception {
        ClientResponseModel save = new ClientResponseModel(Objects.requireNonNull(repository.findById(DEFAULT_UID).orElse(null)));
        ClientResponseModel save1 = new ClientResponseModel(repository.save(testModel));
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.REGISTER_SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.CLIENT.LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .param("pageSize", "1")
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(save.getId()));
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        testUtils.comparePage(resultActions, 1, 2, 2, 1);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].id").value(save.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].username").value(save.getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].roleId").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].email").value(save.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].lastLoginTime").value(save.getLastLoginTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createTime").value(save.getCreateTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createBy").value(save.getCreateBy()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].mustUpdatePassword").value(save.isMustUpdatePassword()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].attendStatus").value(save.getAttendStatus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].department.id").value(save.getDepartment().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].department.name").value(save.getDepartment().getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].active").value(save.isActive()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].lock").value(save.isLock()));
        repository.deleteById(save1.getId());
    }

    @Test
    @DisplayName("測試分頁_搜一筆倒序_成功")
    void clientList_findFirstDesc_ok() throws Exception {
        ClientResponseModel save1 = new ClientResponseModel(repository.save(testModel));
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.REGISTER_SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.CLIENT.LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .param("pageSize", "1")
                .param("sort", "2")
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        testUtils.comparePage(resultActions, 1, 2, 2, 1);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].id").value(save1.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].username").value(save1.getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].roleId").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].email").value(save1.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].lastLoginTime").value(save1.getLastLoginTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createTime").value(save1.getCreateTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createBy").value(save1.getCreateBy()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].mustUpdatePassword").value(save1.isMustUpdatePassword()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].attendStatus").value(save1.getAttendStatus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].department").value(save1.getDepartment()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].active").value(save1.isActive()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].lock").value(save1.isLock()));
        repository.deleteById(save1.getId());
    }

    @Test
    @DisplayName("測試分頁_第二頁_成功")
    void clientList_secPage_ok() throws Exception {
        ClientResponseModel save1 = new ClientResponseModel(repository.save(testModel));
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.REGISTER_SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.CLIENT.LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .param("pageSize", "1")
                .param("pageNum", "2")
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        testUtils.comparePage(resultActions, 1, 2, 2, 2);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].id").value(save1.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].username").value(save1.getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].roleId").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].email").value(save1.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].lastLoginTime").value(save1.getLastLoginTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createTime").value(save1.getCreateTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createBy").value(save1.getCreateBy()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].mustUpdatePassword").value(save1.isMustUpdatePassword()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].attendStatus").value(save1.getAttendStatus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].department").value(save1.getDepartment()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].active").value(save1.isActive()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].lock").value(save1.isLock()));
        repository.deleteById(save1.getId());
    }

    @Test
    @DisplayName("搜單一用戶_成功")
    void getClient_ok() throws Exception {
        ClientResponseModel save = new ClientResponseModel(Objects.requireNonNull(repository.findById(DEFAULT_UID).orElse(null)));
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.REGISTER_SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.CLIENT.GET_CLIENT)
                .contentType(MediaType.APPLICATION_JSON)
                .param("id", String.valueOf(save.getId()))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(save.getId()));
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(save.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.username").value(save.getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.roleId").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.email").value(save.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.lastLoginTime").value(save.getLastLoginTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.createTime").value(save.getCreateTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.createBy").value(save.getCreateBy()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.mustUpdatePassword").value(save.isMustUpdatePassword()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.attendStatus").value(save.getAttendStatus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.department.id").value(save.getDepartment().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.department.name").value(save.getDepartment().getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.active").value(save.isActive()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.lock").value(save.isLock()));
    }

    @Test
    @DisplayName("搜單一用戶_未輸入ID_錯誤")
    void getClient_noId_error() throws Exception {
        ResponseEntity<ApiResponse> response = ApiResponse.error(ApiResponseCode.INVALID_INPUT);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.CLIENT.GET_CLIENT)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("搜單一用戶_未知ID_錯誤")
    void getClient_unknownId_error() throws Exception {
        ResponseEntity<ApiResponse> response = ApiResponse.error(ApiResponseCode.USER_NOT_FOUND);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.CLIENT.GET_CLIENT)
                .contentType(MediaType.APPLICATION_JSON)
                .param("id", "99999")
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("編輯用戶_成功")
    void updateClient_ok() throws Exception {
        ClientResponseModel save = new ClientResponseModel(repository.save(testModel));
        UpdateClientInfoRequest request = new UpdateClientInfoRequest(
                save.getId(),
                save.getUsername(),
                "test" + save.getEmail(),
                List.of(2L, 3L),
                null
        );
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS, save);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.CLIENT.UPDATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(request))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(save.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.username").value(request.getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.roleId").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.email").value(request.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.lastLoginTime").value(save.getLastLoginTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.createTime").value(save.getCreateTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.createBy").value(save.getCreateBy()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.mustUpdatePassword").value(save.isMustUpdatePassword()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.attendStatus").value(save.getAttendStatus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.department").value(save.getDepartment()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.active").value(save.isActive()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.lock").value(save.isLock()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.roleId", Matchers.containsInAnyOrder(2, 3)));//更新用戶時不帶departmentID就不做更動，預設部門只會在註冊生效
        repository.deleteById(save.getId());
    }

    @Test
    @DisplayName("編輯用戶_找不到用戶_錯誤")
    void updateClient_userNotFound_error() throws Exception {
        UpdateClientInfoRequest request = new UpdateClientInfoRequest(
                9999L,
                null,
                null,
                null,
                null
        );
        ResponseEntity<ApiResponse> response = ApiResponse.error(ApiResponseCode.USER_NOT_FOUND);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.CLIENT.UPDATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(request))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("編輯用戶_無效email_錯誤")
    void updateClient_invalidEmail_error() throws Exception {
        UpdateClientInfoRequest request = new UpdateClientInfoRequest(
                1L,
                "test",
                "test",
                List.of(),
                1L
        );
        ResponseEntity<ApiResponse> response = ApiResponse.error(HttpStatus.BAD_REQUEST, "Email格式錯誤");
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.CLIENT.UPDATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(request))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("編輯用戶_id為空_錯誤")
    void updateClient_noId_error() throws Exception {
        UpdateClientInfoRequest request = new UpdateClientInfoRequest(
                null,
                null,
                null,
                null,
                null
        );
        ResponseEntity<ApiResponse> response = ApiResponse.error(HttpStatus.BAD_REQUEST, "用戶ID不得為空");
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.CLIENT.UPDATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(request))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("更改密碼_id為空_錯誤")
    void updatePassword_noId_error() throws Exception {
        UpdatePasswordRequest request = new UpdatePasswordRequest(
                null,
                "123",
                "Aa123123"
        );
        ResponseEntity<ApiResponse> response = ApiResponse.error(HttpStatus.BAD_REQUEST, "用戶ID不得為空");
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.CLIENT.UPDATE_PASSWORD)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(request))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("更改密碼_密碼為空_錯誤")
    void updatePassword_noPassword_error() throws Exception {
        UpdatePasswordRequest request = new UpdatePasswordRequest(
                1L,
                null,
                "Aa123123"
        );
        ResponseEntity<ApiResponse> response = ApiResponse.error(HttpStatus.BAD_REQUEST, "密碼不得為空");
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.CLIENT.UPDATE_PASSWORD)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(request))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("更改密碼_密碼格式長度錯誤_錯誤")
    void updatePassword_inValidPasswordSize_error() throws Exception {
        UpdatePasswordRequest request = new UpdatePasswordRequest(
                1L,
                "123",
                "Aa123"
        );
        ResponseEntity<ApiResponse> response = ApiResponse.error(HttpStatus.BAD_REQUEST, "用戶密碼長度不得小於8, 不得大於20");
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.CLIENT.UPDATE_PASSWORD)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(request))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("更改密碼_密碼格式須包含小寫_錯誤")
    void updatePassword_inValidPasswordLowercase_error() throws Exception {
        UpdatePasswordRequest request = new UpdatePasswordRequest(
                1L,
                "123",
                "A1234567"
        );
        ResponseEntity<ApiResponse> response = ApiResponse.error(HttpStatus.BAD_REQUEST, "密碼必須包含小寫字母");
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.CLIENT.UPDATE_PASSWORD)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(request))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("更改密碼_密碼格式須包含大寫_錯誤")
    void updatePassword_inValidPasswordUppercase_error() throws Exception {
        UpdatePasswordRequest request = new UpdatePasswordRequest(
                1L,
                "123",
                "a1234567"
        );
        ResponseEntity<ApiResponse> response = ApiResponse.error(HttpStatus.BAD_REQUEST, "密碼必須包含大寫字母");
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.CLIENT.UPDATE_PASSWORD)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(request))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("更改密碼_密碼格式須包含數字_錯誤")
    void updatePassword_inValidPasswordNumber_error() throws Exception {
        UpdatePasswordRequest request = new UpdatePasswordRequest(
                1L,
                "123",
                "Aaqweqwe"
        );
        ResponseEntity<ApiResponse> response = ApiResponse.error(HttpStatus.BAD_REQUEST, "密碼必須包含數字");
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.CLIENT.UPDATE_PASSWORD)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(request))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("更改密碼_密碼格式不得包含特殊字符_錯誤")
    void updatePassword_inValidPasswordSpecialCharacters_error() throws Exception {
        UpdatePasswordRequest request = new UpdatePasswordRequest(
                1L,
                "123",
                "Aa1231 23@"
        );
        ResponseEntity<ApiResponse> response = ApiResponse.error(HttpStatus.BAD_REQUEST, "密碼不得包含空格或特殊字符");
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.CLIENT.UPDATE_PASSWORD)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(request))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("更改密碼_非本人不得更改_錯誤")
    void updatePassword_identityError_error() throws Exception {
        UpdatePasswordRequest request = new UpdatePasswordRequest(
                999L,
                "123",
                "Aa123123"
        );
        ResponseEntity<ApiResponse> response = ApiResponse.error(ApiResponseCode.IDENTITY_ERROR);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.CLIENT.UPDATE_PASSWORD)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(request))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("更改密碼_舊密碼錯誤_錯誤")
    void updatePassword_inValidOldPassword_error() throws Exception {
        UpdatePasswordRequest request = new UpdatePasswordRequest(
                1L,
                "123",
                "Aa123123"
        );
        ResponseEntity<ApiResponse> response = ApiResponse.error(ApiResponseCode.INVALID_LOGIN);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.CLIENT.UPDATE_PASSWORD)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(request))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("更改密碼_成功")
    void updatePassword_ok() throws Exception {
        Mockito.doReturn(true).when(encodeTool).match(Mockito.any(), Mockito.any());
        UpdatePasswordRequest request = new UpdatePasswordRequest(
                1L,
                "test",
                "Aa123123"
        );
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.UPDATE_PASSWORD_SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.CLIENT.UPDATE_PASSWORD)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(request))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
        //驗證資料庫資料
        entityManager.clear();//事務內清除內建緩存
        Optional<ClientModel> byId = repository.findById(1L);
        Assertions.assertTrue(byId.isPresent());
        Assertions.assertTrue(encodeTool.match("Aa123123", byId.get().getPassword()));
        Mockito.reset(encodeTool);
    }

    @Test
    @DisplayName("用戶鎖定_更新失敗_錯誤")
    void lockUser_invalidInput_error() throws Exception {
        ClientStatusRequest request = new ClientStatusRequest(
                2L,
                "test",
                false
        );
        ResponseEntity<ApiResponse> response = ApiResponse.error(ApiResponseCode.UPDATE_ERROR);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.CLIENT.CLIENT_LOCK)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(request))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("用戶鎖定_成功")
    void lockUser_ok() throws Exception {
        ClientStatusRequest request = new ClientStatusRequest(
                1L,
                "test",
                true
        );
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.CLIENT.CLIENT_LOCK)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(request))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
        //驗證資料庫資料
        entityManager.clear();//事務內清除內建緩存
        Optional<ClientModel> byId = repository.findById(1L);
        Assertions.assertTrue(byId.isPresent());
        ClientModel clientModel = byId.get();
        Assertions.assertTrue(clientModel.isLock());
        clientModel.setLock(false);
        repository.save(clientModel);
    }

    @Test
    @DisplayName("用戶停用_更新失敗_錯誤")
    void userStatus_invalidInput_error() throws Exception {
        ClientStatusRequest request = new ClientStatusRequest(
                2L,
                "test",
                true
        );
        ResponseEntity<ApiResponse> response = ApiResponse.error(ApiResponseCode.UPDATE_ERROR);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.CLIENT.CLIENT_STATUS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(request))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("用戶停用_成功")
    void userStatus_ok() throws Exception {
        ClientStatusRequest request = new ClientStatusRequest(
                1L,
                "test",
                false
        );
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.CLIENT.CLIENT_STATUS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(request))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
        //驗證資料庫資料
        entityManager.clear();//事務內清除內建緩存
        Optional<ClientModel> byId = repository.findById(1L);
        Assertions.assertTrue(byId.isPresent());
        ClientModel clientModel = byId.get();
        Assertions.assertFalse(clientModel.isActive());
        clientModel.setActive(true);
        repository.save(clientModel);
    }

    @Test
    @DisplayName("用戶名稱清單_成功")
    void clientNameList_ok() throws Exception {
        Optional<ClientModel> byId = repository.findById(1L);
        Assertions.assertTrue(byId.isPresent());
        ClientNameObject clientNameObject = new ClientNameObject(byId.get());
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.CLIENT.NAME_LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].id").value(clientNameObject.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].username").value(clientNameObject.getUsername()));
    }

    @Test
    @DisplayName("用戶登出_成功")
    void userLogout_bothToken_ok() throws Exception {
        String accessToken = testUtils.createTestToken(DEFAULT_UID);
        String refreshToken = "refreshToken";
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(Router.CLIENT.LOGOUT)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(TokenService.REFRESH_TOKEN, refreshToken);
        testUtils.performAndExpect(mockMvc, requestBuilder, response);

        Assertions.assertTrue(cacheService.existsTokenBlackList(accessToken));
        Assertions.assertTrue(cacheService.existsTokenBlackList(refreshToken));
        cacheService.refreshAllCache();
    }

    @Test
    @DisplayName("用戶登出_成功")
    void userLogout_accessToken_ok() throws Exception {
        String accessToken = testUtils.createTestToken(DEFAULT_UID);
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(Router.CLIENT.LOGOUT)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, accessToken);
        testUtils.performAndExpect(mockMvc, requestBuilder, response);

        Assertions.assertTrue(cacheService.existsTokenBlackList(accessToken));
        cacheService.refreshAllCache();
    }
    //更改role用於測試權限
    private void editRole(long roleId){
        Optional<ClientModel> byId = repository.findById(1L);
        Assertions.assertTrue(byId.isPresent());
        ClientModel model = byId.get();
        Set<RoleModel> set = new HashSet<>();
        set.add(new RoleModel(roleId));
        model.setRoles(set);
        repository.save(model);
        cacheService.refreshAllCache();
    }
}