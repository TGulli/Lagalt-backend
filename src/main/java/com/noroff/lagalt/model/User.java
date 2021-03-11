package com.noroff.lagalt.model;

import com.noroff.lagalt.project.model.Project;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "secret")
    private String secret;

    @ManyToMany(mappedBy = "owners")
    private List<Project> ownedProjects;

    public User() { }

    public User(long id, String name, String secret) {
        this.id = id;
        this.name = name;
        this.secret = secret;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
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
}
