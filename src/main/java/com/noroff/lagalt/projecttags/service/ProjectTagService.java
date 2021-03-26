package com.noroff.lagalt.projecttags.service;


import com.noroff.lagalt.project.model.Project;
import com.noroff.lagalt.projecttags.model.ProjectTag;
import com.noroff.lagalt.projecttags.repository.ProjectTagRepository;
import com.noroff.lagalt.usertags.model.UserTag;
import com.noroff.lagalt.usertags.repository.UserTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProjectTagService {

    @Autowired
    private ProjectTagRepository projectTagRepository;

    public ResponseEntity<ProjectTag> create(ProjectTag projectTag) {
        if (projectTag == null || projectTag.getTag() == null || projectTag.getTag().equals("")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tag is not set");
        }
        return ResponseEntity.ok(projectTagRepository.save(projectTag));
    }

    public ResponseEntity<List<ProjectTag>> getAll() {
        return ResponseEntity.ok(projectTagRepository.findAll());
    }
}

