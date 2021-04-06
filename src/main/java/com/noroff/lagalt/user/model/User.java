package com.noroff.lagalt.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.api.client.util.DateTime;
import com.noroff.lagalt.message.model.Message;
import com.noroff.lagalt.project.model.Project;
import com.noroff.lagalt.user.model.LoginMethod;
import com.noroff.lagalt.projectcollaborators.models.ProjectCollaborators;
import com.noroff.lagalt.userhistory.model.UserHistory;
import com.noroff.lagalt.usertags.model.UserTag;
import com.sun.istack.NotNull;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * User model class as Database table.
 */



@Entity
@Table(name = "Users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
})
public class User {

    // ** FIELD VARIABLES **

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "name")
    private String name;

    @Column(name = "secret")
    private String secret;

    @Column(name = "email")
    private String email;

    @Column(name = "hidden")
    private Boolean hidden;

    @Column(name = "locale")
    private String locale;

    @Column(name = "bio")
    private String bio;

    @Column(name = "loginMethod")
    private LoginMethod loginMethod;

    @Column(name = "verified")
    private Boolean verified;

    // ** RELATIONSHIPS **

    // Cascadetype remove on all relations in order to delete all related content when a user self-deletes
    // One to many relationships with user being the dominant relation
    @OneToMany(mappedBy = "owner", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Project> ownedProjects;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ProjectCollaborators> collaboratorIn;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<UserTag> userTags;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Message> messages;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<UserHistory> records;

    // ** CONSTRUCTORS **

    public User() { }

    public User(long id, String username, String name, String secret, String email, Boolean hidden, String locale, String bio) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.secret = secret;
        this.email = email;
        this.hidden = hidden;
        this.locale = locale;
        this.bio = bio;
    }

    // ** GETTERS AND SETTERS **

    @JsonIgnore
    public List<UserHistory> getRecords() {
        return records;
    }

    public List<Project> getOwnedProjects() {
        return ownedProjects;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public String getLocale() {
        return locale;
    }

    public List<UserTag> getUserTags() {
        return userTags;
    }

    public void setLocale(String locale) {
        this.locale = locale;
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

    public void setOwnedProjects(List<Project> ownedProjects) {
        this.ownedProjects = ownedProjects;
    }

    public void setUserTags(List<UserTag> userTags) {
        this.userTags = userTags;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public LoginMethod getLoginMethod() {
        return loginMethod;
    }

    public void setLoginMethod(LoginMethod loginMethod) {
        this.loginMethod = loginMethod;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String name) {
        this.username = name;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Boolean isHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public List<ProjectCollaborators> getCollaboratorIn() {
        return collaboratorIn;
    }

    public void setCollaboratorIn(List<ProjectCollaborators> collaboratorIn) {
        this.collaboratorIn = collaboratorIn;
    }

}
