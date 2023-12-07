package com.ex.erp.dto.response;

import com.ex.erp.enums.response.ApiResponseCode;
import com.ex.erp.tool.JsonTool;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
/**
 * 給Filter用的異常封裝實體
 * */
public class FilterExceptionResponse {
    public static void error(HttpServletResponse response, ApiResponseCode code) throws IOException {
        response.setStatus(code.getCode());
        response.setContentType("application/json; charset=utf-8");
        ApiResponse apiResponse = new ApiResponse(code);
        String errorMessage = JsonTool.toJson(apiResponse);
        response.getWriter().write(errorMessage);
    }
}
