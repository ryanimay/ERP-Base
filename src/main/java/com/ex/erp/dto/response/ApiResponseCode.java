package com.ex.erp.dto.response;

public enum ApiResponseCode {
    //保留自訂義的拓展空間


    ;
    private final int code;
    private final String message;

    ApiResponseCode(int code, String message) {
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
