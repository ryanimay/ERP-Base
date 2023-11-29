package com.ex.erp;

import com.ex.erp.dto.response.ApiResponseCode;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
/**
 * 固定比較API返回參數的結構
 * */
public class ResponseExpectation {
    public static void expectStatusOK(ResultActions actions) throws Exception {
        actions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    public static void expectStatusIs(ResultActions actions, ApiResponseCode apiResponseCode) throws Exception {
        actions.andExpect(MockMvcResultMatchers.status().is(apiResponseCode.getCode()));
    }

    public static void expectBody(ResultActions actions, ApiResponseCode apiResponseCode) throws Exception {
        actions.andExpect(expectCode(apiResponseCode.getCode()))
                .andExpect(expectMessage(apiResponseCode.getMessage()))
                .andExpect(expectData(apiResponseCode.getCustomMessage()));
    }

    public static void expectBody(ResultActions actions, HttpStatus httpStatus, Object body) throws Exception {
        actions.andExpect(expectCode(httpStatus.value()))
                .andExpect(expectMessage(httpStatus.getReasonPhrase()))
                .andExpect(expectData(body));
    }

    private static ResultMatcher expectCode(int expectedCode) {
        return MockMvcResultMatchers.jsonPath("$.code").value(expectedCode);
    }

    private static ResultMatcher expectMessage(String expectedMessage) {
        return MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage);
    }

    private static ResultMatcher expectData(Object expectedData) {
        return MockMvcResultMatchers.jsonPath("$.data").value(expectedData);
    }
}