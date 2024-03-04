package com.erp.base.exception;

import com.erp.base.enums.response.ApiResponseCode;
import com.erp.base.model.ClientIdentity;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.tool.LogFactory;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {
    LogFactory LOG = new LogFactory(GlobalExceptionHandler.class);
    private MessageSource messageSource;
    @Autowired
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ApiResponse> lockedExceptionHandler(Exception e){
        LOG.error(e.getMessage());
        return ApiResponse.error(ApiResponseCode.CLIENT_LOCKED);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiResponse> disabledExceptionHandler(Exception e){
        LOG.error(e.getMessage());
        return ApiResponse.error(ApiResponseCode.CLIENT_DISABLED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse> badCredentialsExceptionHandler(Exception e){
        LOG.error(e.getMessage());
        return ApiResponse.error(ApiResponseCode.INVALID_LOGIN);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse> accessDeniedExceptionHandler(Exception e){
        LOG.error(e.getMessage());
        return ApiResponse.error(ApiResponseCode.ACCESS_DENIED);
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ApiResponse> signatureExceptionHandler(Exception e){
        LOG.error(e.getMessage());
        return ApiResponse.error(ApiResponseCode.INVALID_SIGNATURE);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponse> usernameNotFoundExceptionHandler(Exception e){
        LOG.error(e.getMessage());
        return ApiResponse.error(ApiResponseCode.USER_NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e){
        BindingResult result = e.getBindingResult();
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

    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseEntity<ApiResponse> insufficientAuthenticationExceptionHandler(InsufficientAuthenticationException e){
        LOG.error(e.getMessage());
        return ApiResponse.error(ApiResponseCode.ACCESS_DENIED);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse> illegalStateExceptionHandler(IllegalStateException e){
        LOG.error(e.getMessage());
        return ApiResponse.error(ApiResponseCode.INVALID_INPUT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> globalHandler(Exception e){
        LOG.error(e);//未知錯誤全展開比較好排查
        return ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR);
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
