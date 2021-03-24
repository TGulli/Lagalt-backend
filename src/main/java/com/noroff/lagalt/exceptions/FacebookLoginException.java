package com.noroff.lagalt.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.EXPECTATION_FAILED)
public class FacebookLoginException extends Exception{

    public FacebookLoginException(String msg) {
        super(msg);
    }
}
