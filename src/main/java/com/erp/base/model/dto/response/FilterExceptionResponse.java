package com.erp.base.model.dto.response;

import com.erp.base.tool.ObjectTool;
import com.erp.base.enums.response.ApiResponseCode;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;

import java.io.IOException;
/**
 * 給Filter用的異常封裝實體
 * */
public class FilterExceptionResponse {
    public static void error(HttpServletResponse response, ApiResponseCode code) throws IOException {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json; charset=utf-8");
        ApiResponse apiResponse = new ApiResponse(code);
        String errorMessage = ObjectTool.toJson(apiResponse);
        response.getWriter().write(errorMessage);
    }
}
