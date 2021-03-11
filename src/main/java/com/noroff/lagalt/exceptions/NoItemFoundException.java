package com.noroff.lagalt.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NoItemFoundException extends Exception{

    public NoItemFoundException(String msg) {
        super(msg);
    }
}
