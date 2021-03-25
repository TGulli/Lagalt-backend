package com.noroff.lagalt.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Date;

public class GoogleLoginException extends Exception{

    @ExceptionHandler(GoogleLoginException.class)
    public static ResponseEntity<?> catchException(String msg) {
        return new ResponseEntity<>(new ExceptionDetails(new Date(), msg), HttpStatus.EXPECTATION_FAILED);
    }
}
