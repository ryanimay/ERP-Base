package com.erp.base.model.constant.response;

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
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "response.userNotFound"),
    EMAIL_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "response.emailAlreadyExists"),
    UNKNOWN_USER_OR_EMAIL(HttpStatus.BAD_REQUEST, "response.unknownUserOrEmail"),
    RESET_PASSWORD_SUCCESS(HttpStatus.OK, "response.resetPasswordSuccess"),
    UPDATE_PASSWORD_SUCCESS(HttpStatus.OK, "response.updatePasswordSuccess"),
    RESET_PASSWORD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "response.resetPasswordError"),
    INVALID_LOGIN(HttpStatus.UNAUTHORIZED, "response.invalidUsernameOrPassword"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "response.accessDenied"),
    INVALID_SIGNATURE(HttpStatus.UNAUTHORIZED, "response.invalidSignature"),
    MESSAGING_ERROR(CustomResponseEnum.MESSAGE_ERROR, "response.messageError"),
    CLIENT_LOCKED(HttpStatus.FORBIDDEN, "response.clientLocked"),
    CLIENT_DISABLED(HttpStatus.FORBIDDEN, "response.disabled"),
    NAME_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "response.nameAlreadyExists"),
    SUCCESS(HttpStatus.OK, "response.success"),
    UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "response.unknownError"),
    SECURITY_ERROR(HttpStatus.BAD_REQUEST, "response.securityError"),
    IDENTITY_ERROR(HttpStatus.FORBIDDEN, "response.identityError"),
    SIGN_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "response.signFailed"),
    ROLE_IN_USE(HttpStatus.BAD_REQUEST, "response.roleInUse"),
    DEPARTMENT_IN_USE(HttpStatus.BAD_REQUEST, "response.departmentInUse"),
    CLASS_NOT_FOUND(HttpStatus.BAD_REQUEST, "response.classNotFound"),
    SCHEDULER_ERROR(HttpStatus.BAD_REQUEST, "response.schedulerError"),
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "response.invalidInput"),
    UPDATE_ERROR(HttpStatus.BAD_REQUEST, "response.updateError"),
    CACHE_KEY_ERROR(HttpStatus.BAD_REQUEST, "response.cacheKeyError"),
    JOB_NAME_EXISTS(HttpStatus.BAD_REQUEST, "response.jobNameExists"),
    CRON_ERROR(HttpStatus.BAD_REQUEST, "response.cronError");

    private final int code;
    private final String message;
    private final HttpStatus status;
    private final String customMessage;

    ApiResponseCode(HttpStatus status, String customMessage) {
        this.code = status.value();
        this.message = status.getReasonPhrase();
        this.status = status;
        this.customMessage = customMessage;
    }

    ApiResponseCode(CustomResponseEnum customResponseEnum, String customMessage) {
        this.code = customResponseEnum.getCode();
        this.message = customResponseEnum.getMessage();
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;//自定義錯誤的httpStatus都是500
        this.customMessage = customMessage;
    }

    public int getCode(){
        return this.code;
    }

    public String getMessage(){
        return this.message;
    }
}
