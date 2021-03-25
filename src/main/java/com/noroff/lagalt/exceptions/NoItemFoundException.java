package com.noroff.lagalt.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Date;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NoItemFoundException extends Exception{
    public NoItemFoundException() {
    }

    public NoItemFoundException(String msg) {
        super(msg);
    }

    @ExceptionHandler(NoItemFoundException.class)
    public static ResponseEntity<?> catchException(String msg) {
        return new ResponseEntity<>(new ExceptionDetails(new Date(), msg), HttpStatus.NOT_FOUND);
    }
}
