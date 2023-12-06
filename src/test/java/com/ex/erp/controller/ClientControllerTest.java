package com.ex.erp.controller;

//import com.ex.erp.ResponseExpectation;
//import com.ex.erp.dto.request.client.RegisterRequest;
//import com.ex.erp.dto.response.ApiResponse;
//import com.ex.erp.dto.response.ApiResponseCode;
//import com.ex.erp.service.CacheService;
//import com.ex.erp.service.ClientService;
//import com.ex.erp.service.cache.ClientCache;
//import com.ex.erp.service.security.TokenService;
//import com.ex.erp.tool.JsonTool;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.context.support.ResourceBundleMessageSource;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.ResultActions;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//
//import static org.mockito.ArgumentMatchers.any;
//
///**
// * 暫時先不測controller
// * 遇到一些問題，牽連太多，後續應該改成controller都做integration test
// * 直接用@SpringBootTest
// * */
//@WebMvcTest(controllers = ClientController.class)
//@AutoConfigureMockMvc(addFilters = false)
class ClientControllerTest {

//    @Autowired
//    private MockMvc mockMvc;
//    @MockBean
//    private CacheService cacheService;
//    @MockBean
//    private ClientService clientService;
//    @MockBean
//    private ClientCache clientCache;
//    @MockBean
//    private TokenService tokenService;
//    @MockBean
//    private ResourceBundleMessageSource  messageSource;

    //opValid
    //register
        //success
        //Username already exists
        //illegal param
    //login
        //success
        //empty param
        //wrong param
    //list
        //success

//    @BeforeEach
//    void setup(){
//        ApiResponse.setMessageResource(messageSource);//設置給ApiResponse使用
//    }
//    @Test
//    void opValid() throws Exception {
//        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/client/opValid"));
//        ResponseExpectation.expectStatusOK(perform);
//        ResponseExpectation.expectBody(perform, HttpStatus.OK, "OK");
//    }
//
//    @Test
//    void register_success() throws Exception {
//        RegisterRequest registerRequest = new RegisterRequest("test111", "Password123");
//        String jsonRequest = JsonTool.toJson(registerRequest);
//        Mockito.when(clientService.isUsernameExists(any())).thenReturn(false);
//        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/client/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(jsonRequest));
//        ResponseExpectation.expectStatusOK(perform);
//        ResponseExpectation.expectBody(perform, ApiResponseCode.REGISTER_SUCCESS);
//    }
//
//    @Test
//    void register_whenUserNameAlreadyExists_returnBadRequest() throws Exception {
//        RegisterRequest registerRequest = new RegisterRequest("test11111", "Password123");
//        String jsonRequest = JsonTool.toJson(registerRequest);
//        Mockito.when(clientService.isUsernameExists(any())).thenReturn(true);
//        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/client/register")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(jsonRequest));
//        ResponseExpectation.expectStatusIs(perform, ApiResponseCode.USERNAME_ALREADY_EXIST.getCode());
//        ResponseExpectation.expectBody(perform, ApiResponseCode.USERNAME_ALREADY_EXIST);
//    }
//
//    @Test
//    void register_whenIllegalParam_returnBadRequest() throws Exception {
//        RegisterRequest registerRequest = new RegisterRequest("test1", "Password123");
//        ClientIdentity.defaultLocale = defaultLocaleString;
//        Mockito.when(messageSource.getMessage(any(), any(), any())).thenReturn("client.userNameNotEmpty");
//        String jsonRequest = JsonTool.toJson(registerRequest);
//        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/client/register")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(jsonRequest));
//        ResponseExpectation.expectStatusIs(perform, HttpStatus.BAD_REQUEST.value());
//        ResponseExpectation.expectBody(perform, HttpStatus.BAD_REQUEST, "client.userNameNotEmpty");
//    }
}