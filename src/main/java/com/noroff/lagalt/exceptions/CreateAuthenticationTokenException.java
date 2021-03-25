package com.noroff.lagalt.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;

public class CreateAuthenticationTokenException extends Exception{

    @ExceptionHandler(CreateAuthenticationTokenException.class)
    public static ResponseEntity<?> catchException(String msg) {
        return new ResponseEntity<>(new ExceptionDetails(new Date(), msg), HttpStatus.NOT_ACCEPTABLE);
    }
}
