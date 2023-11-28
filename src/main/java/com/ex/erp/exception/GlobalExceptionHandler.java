package com.ex.erp.exception;

import com.ex.erp.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException ex){
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();

        // Handle field errors and extract messages
        List<String> errorMessages = new ArrayList<>();
        for (FieldError fieldError : fieldErrors) {
            errorMessages.add(fieldError.getDefaultMessage());
        }

        // Construct your response or log the errors
        String responseMessage = String.join("; ", errorMessages);
        return ApiResponse.error(HttpStatus.BAD_REQUEST, responseMessage);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> globalHandler(Exception e){
        return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Exception Type: " + e.getClass().getName() + "\nError:" + e.getMessage());
    }
}
