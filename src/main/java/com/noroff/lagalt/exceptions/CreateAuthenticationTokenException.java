package com.noroff.lagalt.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
public class CreateAuthenticationTokenException extends Exception{

    public CreateAuthenticationTokenException(String msg) {
        super(msg);
    }
}
