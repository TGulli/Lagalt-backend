package com.noroff.lagalt.projectcollaborators.models;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.noroff.lagalt.user.model.User;
import com.noroff.lagalt.project.model.Project;
import com.noroff.lagalt.projectcollaborators.models.Status;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

//

@Entity
@Table(name = "Project_collaborators")
public class ProjectCollaborators {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User user;

    /*@JsonGetter("user")
    public String user() {
        if(user != null){
            //return user;
            return "/api/v1/public/users/" + user.getId();
        }else{
            return null;
        }
    }*/

    @JsonGetter("user")
    public ProjectCollaborators.ReturnCollaborator user(){
        if(user != null) {
            return  new ProjectCollaborators.ReturnCollaborator(user.getId());
        }
        return null;
    }

    @ManyToOne()
    @JoinColumn(name = "project_id")
    private Project project;


    @JsonGetter("project")
    public ProjectCollaborators.ReturnProject project() {

        if(project != null) {
            return  new ProjectCollaborators.ReturnProject(project.getId(), project.getName());
        }
        return null;
        /*if(project != null){
            //return project;
            return "/api/v1/projects/" + project.getId();
        }else{
            return null;
        }*/
    }

    @Column
    private Status status;

    @Column
    private String motivation;

    public ProjectCollaborators() {
    }

    public ProjectCollaborators(Long id, User user, Project project, Status status, String motivation) {
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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


    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    class ReturnCollaborator {
        private Long id;



        public ReturnCollaborator(Long id) {
            this.id = id;

        }

    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    class ReturnProject{
        private Long id;
        private String name;


        public ReturnProject(Long id, String name) {
            this.id = id;
            this.name = name;

        }

    }
}


