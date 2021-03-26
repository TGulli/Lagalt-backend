package com.noroff.lagalt.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class AuthenticateException extends Exception{

    public AuthenticateException(String msg) {
        super(msg);
    }
}
