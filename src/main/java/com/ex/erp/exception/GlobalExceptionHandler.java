package com.ex.erp.exception;

import com.ex.erp.dto.response.ApiResponse;
import com.ex.erp.dto.security.ClientIdentity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    private MessageSource messageSource;
    @Autowired
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
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
        String errorMessage = "MethodArgumentNotValid";
        FieldError fieldError = fieldErrors.get(0);//只返回第一個錯誤
        String defaultMessage = fieldError.getDefaultMessage();
        if(defaultMessage != null) {
            Object[] arguments = resetArray(fieldError.getArguments());
            errorMessage = messageSource.getMessage(defaultMessage, arguments, ClientIdentity.getLocale());
        }
        return ApiResponse.error(HttpStatus.BAD_REQUEST, errorMessage);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> globalHandler(Exception e){
        return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Exception Type: " + e.getClass().getName() + "\nError:" + e.getMessage());
    }

    private Object[] resetArray(Object[] originalArguments){
        if(originalArguments == null) return null;

        int length = originalArguments.length;
        if (length > 1) {
            Object[] newArguments = new Object[length - 1];
            System.arraycopy(originalArguments, 1, newArguments, 0, length - 1);
            return newArguments;
        }
        return null;
    }
}
