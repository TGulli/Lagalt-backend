package com.noroff.lagalt.project.model;

import com.noroff.lagalt.projecttags.model.ProjectTag;
import com.noroff.lagalt.user.model.User;

import java.util.List;

public class PartialProject {
    private String name;
    private String description;
    private String category;
    private Progress progress;
    private String owner_username;

    public PartialProject(String name, String description, String category, Progress progress, String owner_username) {
        this.name = name;
        this.description = description;
        this.category = category;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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
