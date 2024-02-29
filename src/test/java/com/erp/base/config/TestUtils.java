package com.erp.base.config;

import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.service.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
@Component
public class TestUtils {
    @Autowired
    private TokenService tokenService;

    public void performAndExpect(MockMvc mockMvc, RequestBuilder requestBuilder, ResponseEntity<ApiResponse> response) throws Exception {
        ApiResponse responseBody = response.getBody();
        assert responseBody != null;
        performAndExpectCodeAndMessage(mockMvc, requestBuilder, response)
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(responseBody.getData()));
    }

    public ResultActions performAndExpectCodeAndMessage(MockMvc mockMvc, RequestBuilder requestBuilder, ResponseEntity<ApiResponse> response) throws Exception {
        ApiResponse responseBody = response.getBody();
        assert responseBody != null;
        return mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(responseBody.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(responseBody.getMessage()));
    }

    //測試用，只建立短時間的accessToken
    public String createTestToken(String username){
        return TokenService.TOKEN_PREFIX + tokenService.createToken(TokenService.ACCESS_TOKEN, username, 30);
    }

    public void comparePage(ResultActions resultActions, int pageSize, int totalPage, int totalElements, int PageNum) throws Exception {
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.pageSize").value(pageSize))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.totalPage").value(totalPage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.totalElements").value(totalElements))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.pageNum").value(PageNum));
    }
}
