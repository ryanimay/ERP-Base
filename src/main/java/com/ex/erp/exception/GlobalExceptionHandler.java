package com.ex.erp.exception;

import com.ex.erp.dto.response.ApiResponse;
import com.ex.erp.dto.response.ApiResponseCode;
import com.ex.erp.dto.security.ClientIdentity;
import com.ex.erp.tool.LogFactory;
import io.jsonwebtoken.security.SignatureException;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
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
        LOG.error(e);
        return ApiResponse.error(ApiResponseCode.CLIENT_LOCKED);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiResponse> disabledExceptionHandler(Exception e){
        LOG.error(e);
        return ApiResponse.error(ApiResponseCode.CLIENT_DISABLED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse> badCredentialsExceptionHandler(Exception e){
        LOG.error(e);
        return ApiResponse.error(ApiResponseCode.INVALID_LOGIN);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse> accessDeniedExceptionHandler(Exception e){
        LOG.error(e);
        return ApiResponse.error(ApiResponseCode.ACCESS_DENIED);
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ApiResponse> signatureExceptionHandler(Exception e){
        LOG.error(e);
        return ApiResponse.error(ApiResponseCode.INVALID_SIGNATURE);
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ApiResponse> messagingExceptionHandler(Exception e){
        LOG.error(e);
        return ApiResponse.error(ApiResponseCode.MESSAGING_ERROR);
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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> globalHandler(Exception e){
        LOG.error(e);
        return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown Error");
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
