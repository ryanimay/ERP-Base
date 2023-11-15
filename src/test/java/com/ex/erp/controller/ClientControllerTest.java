package com.ex.erp.controller;

import com.ex.erp.model.ClientModel;
import com.ex.erp.service.ClientService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ClientController.class)
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientService clientService;

    //login
    //success
    //wrong pwd

//    .param("password", "test123"))
//    .andExpect(MockMvcResultMatchers.status().isOk())
//    .andExpect(request -> Assertions.assertTrue(request.getResolvedException() instanceof NullPointerException))
//    .andExpect(MockMvcResultMatchers.jsonPath("$.properties").value(expectValue));
    @Test
    void login_success() throws Exception {
        Mockito.when(clientService.login()).thenReturn(new ClientModel("test123", "test123"));

        mockMvc.perform(MockMvcRequestBuilders.post("/client/login")
                .param("username", "test123")
                .param("password", "test123"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.properties").value(new ClientModel("test123", "test123")));
    }

    @Test
    void login_wrongPwd() {

    }
}