package com.noroff.lagalt.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Date;

@ResponseStatus(value = HttpStatus.NO_CONTENT)
public class UserNullException extends Exception{

    public UserNullException(String msg) {
        super(msg);
    }

    @ExceptionHandler(UserNullException.class)
    public static ResponseEntity<?> catchException(String msg) {
        return new ResponseEntity<>(new ExceptionDetails(new Date(), msg), HttpStatus.NO_CONTENT);
    }
}
