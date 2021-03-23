package com.noroff.lagalt.security.dto;

import com.noroff.lagalt.model.User;

public class LoginGranted {

    private User user;
    private JwtResponse token;

    public LoginGranted(User user, JwtResponse token) {
        this.user = user;
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public JwtResponse getToken() {
        return token;
    }

    public void setToken(JwtResponse token) {
        this.token = token;
    }
}
