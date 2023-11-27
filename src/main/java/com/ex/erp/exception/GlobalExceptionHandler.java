package com.ex.erp.exception;

import com.ex.erp.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ApiResponse<Object>> lockedExceptionHandler(Exception e){
        return ApiResponse.error(HttpStatus.LOCKED, e.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> badCredentialsExceptionHandler(Exception e){
        return ApiResponse.error(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> globalHandler(Exception e){
        return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Exception Type: " + e.getClass().getName() + "\nError:" + e.getMessage());
    }
}
