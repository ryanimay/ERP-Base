package com.ex.erp.dto.response;

import com.ex.erp.dto.security.ClientIdentity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {

    private static MessageSource messageSource;
    private int code;
    private String message;
    private Object data;

    public ApiResponse(ApiResponseCode responseCode, Object data) {
        this.code = responseCode.getCode();
        this.message = responseCode.getMessage();
        this.data = data;
    }

    public ApiResponse(ApiResponseCode responseCode) {
        this.code = responseCode.getCode();
        this.message = responseCode.getMessage();
        this.data = messageSource.getMessage(responseCode.getCustomMessage(), null, ClientIdentity.getLocale());
    }

    public ApiResponse(HttpStatus httpStatus) {
        this.code = httpStatus.value();
        this.message = httpStatus.getReasonPhrase();
    }

    public ApiResponse(HttpStatus httpStatus, Object data) {
        this.code = httpStatus.value();
        this.message = httpStatus.getReasonPhrase();
        this.data = data;
    }

    public static ResponseEntity<ApiResponse> success(Object data){
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK, data));
    }

    public static ResponseEntity<ApiResponse> success(ApiResponseCode responseCode){
        return ResponseEntity.ok(new ApiResponse(responseCode));
    }

    public static ResponseEntity<ApiResponse> success(ApiResponseCode responseCode, Object data){
        return ResponseEntity.ok(new ApiResponse(responseCode, data));
    }

    public static ResponseEntity<ApiResponse> success(HttpHeaders headers, Object data) {
        return ResponseEntity.ok().headers(headers).body(new ApiResponse(HttpStatus.OK, data));
    }

    public static ResponseEntity<ApiResponse> error(HttpStatus status, Object data){
        return ResponseEntity.status(status).body(new ApiResponse(status, data));
    }

    public static ResponseEntity<ApiResponse> error(ApiResponseCode responseCode){
        return ResponseEntity.status(responseCode.getStatus()).body(new ApiResponse(responseCode));
    }

    public static void setMessageResource(MessageSource source) {
        messageSource = source;
    }
}
