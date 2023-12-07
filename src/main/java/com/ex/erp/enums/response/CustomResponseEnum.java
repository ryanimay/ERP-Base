package com.ex.erp.enums.response;

public enum CustomResponseEnum {
    MESSAGE_ERROR(1000, "unknown mail error")
    ;
    private final int code;
    private final String message;

    CustomResponseEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
