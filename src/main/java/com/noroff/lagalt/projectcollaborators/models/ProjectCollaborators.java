package com.noroff.lagalt.projectcollaborators.models;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.noroff.lagalt.user.model.User;
import com.noroff.lagalt.project.model.Project;

import javax.persistence.*;
import java.util.stream.Collectors;

//
enum Status {
    PENDING,
    APPROVED,
    DECLINED
}

@Entity
@Table(name = "Project_collaborators")
public class ProjectCollaborators {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private long id;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User user;

    @JsonGetter("user")
    public String user() {
        if(user != null){
            //return user;
            return "/api/v1/public/users/" + user.getId();
        }else{
            return null;
        }
    }


    @ManyToOne()
    @JoinColumn(name = "project_id")
    private Project project;


    @JsonGetter("project")
    public String project() {
        if(project != null){
            //return project;
            return "/api/v1/projects/" + project.getId();
        }else{
            return null;
        }
    }

    @Column
    private Status status;

    @Column
    private String motivation;

    public ProjectCollaborators() {
    }

    public ProjectCollaborators(long id, User user, Project project, Status status, String motivation) {
        this.id = id;
        this.user = user;
        this.project = project;
        this.status = status;
        this.motivation = motivation;
    }

    public String getMotivation() {
        return motivation;
    }

    public void setMotivation(String motivation) {
        this.motivation = motivation;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
