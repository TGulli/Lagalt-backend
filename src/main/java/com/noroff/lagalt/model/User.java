package com.noroff.lagalt.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.noroff.lagalt.chat.model.ChatMessage;
import com.noroff.lagalt.message.model.Message;
import com.noroff.lagalt.project.model.Project;
import com.noroff.lagalt.projectcollaborators.models.ProjectCollaborators;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "secret")
    private String secret;

    @Column(name = "hidden")
    private Boolean hidden;

    @ManyToMany(mappedBy = "owners")
    private List<Project> ownedProjects;

    @OneToMany(mappedBy = "user")
    private List<ProjectCollaborators> collaboratorIn;

    @OneToMany(mappedBy = "user")
    private List<Message> messages;



    public User() { }

    public User(Long id, String name, String secret, Boolean hidden) {
        this.id = id;
        this.name = name;
        this.secret = secret;
        this.hidden = hidden;
    }

    //JsonGetter
    public List<Project> getOwnedProjects() {
        return ownedProjects;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Boolean getHidden() {
        return hidden;
    }

    public void setOwnedProjects(List<Project> ownedProjects) {
        this.ownedProjects = ownedProjects;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
