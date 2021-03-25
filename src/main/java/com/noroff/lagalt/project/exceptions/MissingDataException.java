package com.noroff.lagalt.project.exceptions;

import com.noroff.lagalt.exceptions.ExceptionDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;

public class MissingDataException extends Exception{
    @ExceptionHandler(MissingDataException.class)
    public static ResponseEntity<?> catchException(String msg) {
        return new ResponseEntity<>(new ExceptionDetails(new Date(), msg), HttpStatus.NO_CONTENT);
    }
}
