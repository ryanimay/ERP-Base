package com.ex.erp.dto.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 *自定義API返回信息
 */
@Getter
public enum ApiResponseCode {
    REGISTER_SUCCESS(HttpStatus.OK, "response.registerSuccess"),
    REFRESH_CACHE_SUCCESS(HttpStatus.OK, "response.refreshCacheSuccess"),
    USERNAME_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "response.usernameAlreadyExists"),
    EMAIL_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "response.emailAlreadyExists");
    private final HttpStatus status;
    private final String customMessage;

    ApiResponseCode(HttpStatus status, String customMessage) {
        this.status = status;
        this.customMessage = customMessage;
    }

    public int getCode(){
        return status.value();
    }

    public String getMessage(){
        return status.getReasonPhrase();
    }
}
