package com.noroff.lagalt.project.model;

import com.noroff.lagalt.projecttags.model.ProjectTag;

import java.util.List;

public class PartialProject {
    private String name;
    private String description;
    private Progress progress;
    private String owner_username;

    public PartialProject(String name, String description, Progress progress, String owner_username) {
        this.name = name;
        this.description = description;
        this.progress = progress;
        this.owner_username = owner_username;
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

    public String getOwner_username() {
        return owner_username;
    }

    public void setOwner_username(String owner_username) {
        this.owner_username = owner_username;
    }
}
