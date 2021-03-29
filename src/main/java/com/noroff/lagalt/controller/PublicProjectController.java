package com.noroff.lagalt.controller;

import com.noroff.lagalt.project.model.PartialProject;
import com.noroff.lagalt.project.model.PartialProjectWithTags;
import com.noroff.lagalt.project.model.Project;
import com.noroff.lagalt.project.repository.ProjectRepository;
import com.noroff.lagalt.project.service.ProjectService;
import com.noroff.lagalt.projecttags.model.ProjectTag;
import com.noroff.lagalt.projecttags.repository.ProjectTagRepository;
import com.noroff.lagalt.user.model.PartialUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/public")
@CrossOrigin(origins = "*")
public class PublicProjectController {

    @Autowired
    ProjectTagRepository projectTagRepository;

    @Autowired
    ProjectRepository projectRepository;

    @GetMapping("/projects/{id}")
    public ResponseEntity<PartialProjectWithTags> getPartialProjectById(@PathVariable(value = "id") long id){
        PartialProject p = projectRepository.getPublicProjectById(id);
        List<ProjectTag> pt = projectTagRepository.findProjectTagsByProjectId(id);

        PartialProjectWithTags ppt = new PartialProjectWithTags(p, pt);

        return ResponseEntity.ok(ppt);
    }

    @GetMapping("/projects/show/{page}")
    public ResponseEntity<Page<Project>> showProject(@PathVariable(value = "page") int page){
        Pageable p = PageRequest.of(page, 5);
        Page<Project> givenPage = projectRepository.findAll(p);
        return ResponseEntity.ok(givenPage);
    }
}
