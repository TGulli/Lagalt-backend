package com.noroff.lagalt.message.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.noroff.lagalt.user.model.User;
import com.noroff.lagalt.project.model.Project;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Message")
public class Message {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    @Column
    private String content;

    @Column
    private String timestamp;

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

    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User user;

    @JsonGetter("user")
    public String user() {
        if(user != null){
            //return user;
            return "/api/v1/users/" + user.getId();
        }else{
            return null;
        }
    }

    public Message() {
    }

    public Message(Long id, String content, String timestamp, Project project, User user) {
        this.id = id;
        this.content = content;
        this.timestamp = timestamp;
        this.project = project;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
