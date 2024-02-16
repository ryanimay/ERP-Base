package com.erp.base.config;

import com.erp.base.model.dto.response.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class TestUtils {
    public static void performAndExpect(MockMvc mockMvc, RequestBuilder requestBuilder, ResponseEntity<ApiResponse> response) throws Exception {
        ApiResponse responseBody = response.getBody();
        assert responseBody != null;
        performAndExpectCodeAndMessage(mockMvc, requestBuilder, response)
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(responseBody.getData()));
    }

    public static ResultActions performAndExpectCodeAndMessage(MockMvc mockMvc, RequestBuilder requestBuilder, ResponseEntity<ApiResponse> response) throws Exception {
        ApiResponse responseBody = response.getBody();
        assert responseBody != null;
        return mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(responseBody.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(responseBody.getMessage()));
    }
}
