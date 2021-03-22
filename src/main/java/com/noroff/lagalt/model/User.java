package com.noroff.lagalt.model;

import com.noroff.lagalt.project.model.Project;
import com.noroff.lagalt.projectcollaborators.models.ProjectCollaborators;

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
    private long id;

    @Column(name = "username")
    private String username;

    @Column(name = "secret")
    private String secret;

    @Column(name = "email")
    private String email;

    @Column(name = "hidden")
    private Boolean hidden;

    //BIO

    @ManyToMany(mappedBy = "owners")
    private List<Project> ownedProjects;

    @OneToMany(mappedBy = "user")
    private List<ProjectCollaborators> collaboratorIn;


    public User() { }

    public User(long id, String username, String secret, String email, Boolean hidden) {
        this.id = id;
        this.username = username;
        this.secret = secret;
        this.email = email;
        this.hidden = hidden;
    }

    //JsonGetter
    public List<Project> getOwnedProjects() {
        return ownedProjects;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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
