package com.noroff.lagalt.project.model;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.noroff.lagalt.projecttags.model.ProjectTag;
import com.noroff.lagalt.user.model.User;
import com.noroff.lagalt.projectcollaborators.models.ProjectCollaborators;
import com.noroff.lagalt.usertags.model.UserTag;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

enum Progress{
    FOUNDING,
    IN_PROGRESS,
    STALLED,
    COMPLETED
}

@Entity
@Table(name = "Projects", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")})
public class Project {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "progress")
    private Progress progress;

    @Column(name = "image")
    private String image;

    @Column(name = "category")
    private String category;


    @OneToMany(mappedBy = "project")
    private List<ProjectTag> projectTags;

    //GITHUB REPO/EXTRA DETAILS

    @ManyToMany()
    @JoinTable(
            name = "project_users",
            joinColumns = {@JoinColumn(name = "projects_id")},
            inverseJoinColumns = {@JoinColumn(name = "users_id")}
    )
    private List<User> owners;

    @OneToMany(mappedBy = "project")
    List<ProjectCollaborators> collaborators;
    
    public Project(){}

    public Project(long id, String name, String description, Progress progress, String image, String category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.progress = progress;
        this.image = image;
        this.category = category;
    }

    /*
    public List<String> getOwnerNames(){
        if(owners != null) {
            return owners.stream()
                    .map(User::getName)
                    .collect(Collectors.toList());
        }
        return null;
    }

     */

    @JsonGetter("owners")
    public List<ReturnUser> getOwnerNames(){
        if(owners != null) {
            return owners.stream().map(temp -> new ReturnUser(temp.getId(), temp.getUsername())).collect(Collectors.toList());
        }
        return null;
    }

    public List<ProjectTag> getProjectTags() {
        return projectTags;
    }

    public List<ProjectCollaborators> getCollaborators() {
        return collaborators;
    }

    public String getCategory() {
        return category;
    }

    public List<User> getOwners() {
        return owners;
    }

    public long getId(){
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Progress getProgress() {
        return progress;
    }

    public void setProgress(Progress progress) {
        this.progress = progress;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    class ReturnUser {
        private long id;
        private String name;

        public ReturnUser(long id, String name) {
            this.id = id;
            this.name = name;
        }

    }


}
