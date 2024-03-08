package com.erp.base.model.dto.response;

import com.erp.base.model.ClientIdentity;
import com.erp.base.model.constant.response.ApiResponseCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {

    private static ThreadLocal<MessageSource> messageSourceThreadLocal = new ThreadLocal<>();
    private int code;
    private String message;
    private Object data;

    public ApiResponse(ApiResponseCode responseCode, Object data) {
        this.code = responseCode.getCode();
        this.message = responseCode.getMessage();
        this.data = data;
    }
    @SuppressWarnings("ConstantConditions")
    public ApiResponse(ApiResponseCode responseCode) {
        this.code = responseCode.getCode();
        this.message = responseCode.getMessage();
        String customMessage = responseCode.getCustomMessage();
        String formatMsg = getCurrentMessageSource().getMessage(customMessage, null, ClientIdentity.getLocale());
        this.data = formatMsg == null ? customMessage : formatMsg;
    }
    @SuppressWarnings("ConstantConditions")
    public ApiResponse(ApiResponseCode responseCode, Object[] args) {
        this.code = responseCode.getCode();
        this.message = responseCode.getMessage();
        String customMessage = responseCode.getCustomMessage();
        String formatMsg = getCurrentMessageSource().getMessage(customMessage, args, ClientIdentity.getLocale());
        this.data = formatMsg == null ? customMessage : formatMsg;
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
        return ResponseEntity.ok().body(new ApiResponse(status, data));
    }

    public static ResponseEntity<ApiResponse> error(ApiResponseCode responseCode){
        return ResponseEntity.ok().body(new ApiResponse(responseCode));
    }

    public static ResponseEntity<ApiResponse> error(ApiResponseCode responseCode, Object data){
        return ResponseEntity.ok().body(new ApiResponse(responseCode, data));
    }

    public static ResponseEntity<ApiResponse> errorMsgFormat(ApiResponseCode responseCode, Object... data){
        return ResponseEntity.ok().body(new ApiResponse(responseCode, data));
    }

    public static void setMessageResource(MessageSource source) {
        messageSourceThreadLocal.set(source);
    }

    private static MessageSource getCurrentMessageSource() {
        MessageSource messageSource = messageSourceThreadLocal.get();
        if(messageSource == null){
            ResourceBundleMessageSource  resource = new ResourceBundleMessageSource();
            resource.setBasename("message");
            resource.setDefaultLocale(ClientIdentity.defaultLocale);
            resource.setDefaultEncoding("UTF-8");
            messageSourceThreadLocal.set(resource);
            return resource;
        }
        return messageSource;
    }
}
