package com.noroff.lagalt.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.EXPECTATION_FAILED)
public class GoogleLoginException extends Exception{

    public GoogleLoginException(String msg) {
        super(msg);
    }
}
