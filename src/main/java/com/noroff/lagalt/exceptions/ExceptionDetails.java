package com.noroff.lagalt.exceptions;

import java.util.Date;

public class ExceptionDetails {
    private Date timestamp;
    private String message;

    public ExceptionDetails(Date timestamp, String message) {
        this.timestamp = timestamp;
        this.message = message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }
}
