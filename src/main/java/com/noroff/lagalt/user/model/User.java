package com.noroff.lagalt.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.noroff.lagalt.message.model.Message;
import com.noroff.lagalt.project.model.Project;
import com.noroff.lagalt.user.model.LoginMethod;
import com.noroff.lagalt.projectcollaborators.models.ProjectCollaborators;
import com.noroff.lagalt.usertags.model.UserTag;

import javax.persistence.*;
import java.util.List;




@Entity
@Table(name = "Users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
})
public class User {

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

    @Column(name = "description")
    private String description;

    @Column(name = "hidden")
    private Boolean hidden;

    @Column(name = "locale")
    private String locale;

    @Column(name = "bio")
    private String bio;

    @Column(name = "loginMethod")
    private LoginMethod loginMethod;

    @ManyToMany(mappedBy = "owners")
    private List<Project> ownedProjects;

    @OneToMany(mappedBy = "user")
    private List<ProjectCollaborators> collaboratorIn;

    @OneToMany(mappedBy = "user")
    private List<UserTag> userTags;

    @OneToMany(mappedBy = "user")
    private List<Message> messages;

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

    //JsonGetter
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


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public List<ProjectCollaborators> getCollaboratorIn() {
        return collaboratorIn;
    }

    public void setCollaboratorIn(List<ProjectCollaborators> collaboratorIn) {
        this.collaboratorIn = collaboratorIn;
    }

}
