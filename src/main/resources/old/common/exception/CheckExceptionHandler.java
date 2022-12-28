package com.dingo.common.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CheckExceptionHandler {

    @ExceptionHandler(CheckException.class)
    public String handleBusinessException(CheckException e) {
        return e.getmessage();
    }
}
