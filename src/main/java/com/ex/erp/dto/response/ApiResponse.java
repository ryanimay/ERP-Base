package com.ex.erp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private int code;
    private String message;
    private T Data;

    public ApiResponse(ApiResponseCode responseCode, T data) {
        this.code = responseCode.getCode();
        this.message = responseCode.getMessage();
        Data = data;
    }

    public ApiResponse(HttpStatus httpStatus) {
        this.code = httpStatus.value();
        this.message = httpStatus.getReasonPhrase();
    }

    public ApiResponse(HttpStatus httpStatus, T data) {
        this.code = httpStatus.value();
        this.message = httpStatus.getReasonPhrase();
        Data = data;
    }

    public static ResponseEntity<ApiResponse<Object>> success(Object data){
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK, data));
    }

    public static ResponseEntity<ApiResponse<Object>> success(HttpHeaders headers, Object data) {
        return ResponseEntity.ok().headers(headers).body(new ApiResponse<>(HttpStatus.OK, data));
    }

    public static ResponseEntity<ApiResponse<Object>> error(HttpStatus status, Object data){
        return ResponseEntity.status(status).body(new ApiResponse<>(status, data));
    }
}
