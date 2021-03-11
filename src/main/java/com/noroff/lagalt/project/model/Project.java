package com.noroff.lagalt.project.model;
import com.noroff.lagalt.model.User;
import javax.persistence.*;
import java.util.ArrayList;

enum Progress{
    FOUNDING,
    IN_PROGRESS,
    STALLED,
    COMPLETED
}

@Entity
@Table(name = "Projects")
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

    
    public Project(){}

    public Project(long id, String name, String description, Progress progress, String image) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.progress = progress;
        this.image = image;
    }

    public long getId(){
        return id;
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
}
