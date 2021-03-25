package com.noroff.lagalt.projecttags.service;


import com.noroff.lagalt.projecttags.model.ProjectTag;
import com.noroff.lagalt.projecttags.repository.ProjectTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectTagService {

    @Autowired
    private ProjectTagRepository projectTagRepository;

    public ResponseEntity<ProjectTag> create(ProjectTag projectTag) {
        ProjectTag createdTag = projectTagRepository.save(projectTag);
        return ResponseEntity.ok(createdTag);
    }

    public ResponseEntity<List<ProjectTag>> getAll() {
        List<ProjectTag> projectTags = projectTagRepository.findAll();
        return ResponseEntity.ok(projectTags);
    }
}

