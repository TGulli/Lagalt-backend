package com.noroff.lagalt.user.model;

public class PartialUser {

    /**
     * POJO used in the public part of our API, and if a user has
     * toggled "hidden mode" in their settings.
     */

    private String username;
    private String name;
    private String bio;

    public PartialUser(String username, String name, String bio) {
        this.username = username;
        this.name = name;
        this.bio = bio;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
