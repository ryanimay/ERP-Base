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
    USERNAME_NOT_EXIST(HttpStatus.BAD_REQUEST, "response.usernameNotExists"),
    EMAIL_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "response.emailAlreadyExists"),
    UNKNOWN_EMAIL(HttpStatus.BAD_REQUEST, "response.unknownEmail"),
    RESET_PASSWORD_SUCCESS(HttpStatus.OK, "response.resetPasswordSuccess"),
    RESET_PASSWORD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "response.resetPasswordError"),
    INVALID_LOGIN(HttpStatus.UNAUTHORIZED, "response.invalidUsernameOrPassword"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "response.accessDenied"),
    INVALID_SIGNATURE(HttpStatus.UNAUTHORIZED, "response.invalidSignature");
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
