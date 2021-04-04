package com.noroff.lagalt.security.dto;

/**
 * POJO for keeping the response message after validating token
 */

public class MessageResponse {
    private String message;

    public MessageResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

