package com.noroff.lagalt.project.model;

import com.noroff.lagalt.projecttags.model.ProjectTag;

import java.util.List;

public class PartialProjectWithTags {
    private PartialProject partialProject;
    private List<ProjectTag> projectTags;

    public PartialProjectWithTags(PartialProject partialProject, List<ProjectTag> projectTags) {
        this.partialProject = partialProject;
        this.projectTags = projectTags;
    }

    public PartialProject getPartialProject() {
        return partialProject;
    }

    public void setPartialProject(PartialProject partialProject) {
        this.partialProject = partialProject;
    }

    public List<ProjectTag> getProjectTags() {
        return projectTags;
    }

    public void setProjectTags(List<ProjectTag> projectTags) {
        this.projectTags = projectTags;
    }
}
