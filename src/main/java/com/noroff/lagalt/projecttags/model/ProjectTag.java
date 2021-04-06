package com.noroff.lagalt.projecttags.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.noroff.lagalt.project.model.Project;

import javax.persistence.*;

@Entity
@Table(name = "ProjectTag")
public class ProjectTag {

    /**
     * Project Tag model
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String tag;


    @ManyToOne()
    @JoinColumn(name = "project_id")
    private Project project;

    public ProjectTag() {

    }

    @JsonGetter("project")
    public String project() {
        return project != null ? project.getName() : null;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public ProjectTag(Long id, String tag) {
        this.id = id;
        this.tag = tag;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
