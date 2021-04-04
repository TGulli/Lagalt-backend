package com.noroff.lagalt.security.dto;

import java.util.List;

import java.io.Serializable;

/**
 * DTO for storing the token response
 */

public class JwtResponse implements Serializable {

    private final String jwttoken;

    public JwtResponse(String jwttoken) {
        this.jwttoken = jwttoken;
    }

    public String getToken() {
        return this.jwttoken;
    }
}
