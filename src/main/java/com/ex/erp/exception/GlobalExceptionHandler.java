package com.ex.erp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(LockedException.class)
    public ResponseEntity<Object> lockedExceptionHandler(Exception e){
        return new ResponseEntity<>("Error:" + e.getMessage(), HttpStatus.LOCKED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> badCredentialsExceptionHandler(Exception e){
        return new ResponseEntity<>("Error:" + e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> globalHandler(Exception e){
        return new ResponseEntity<>("Exception Type: " + e.getClass().getName() + "\nError:" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
