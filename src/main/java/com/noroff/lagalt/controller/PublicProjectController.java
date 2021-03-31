package com.noroff.lagalt.controller;

import com.noroff.lagalt.project.model.PartialProject;
import com.noroff.lagalt.project.model.PartialProjectWithTags;
import com.noroff.lagalt.project.repository.ProjectRepository;
import com.noroff.lagalt.projecttags.model.ProjectTag;
import com.noroff.lagalt.projecttags.repository.ProjectTagRepository;
import com.noroff.lagalt.user.model.PartialUser;
import com.noroff.lagalt.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/public")
@CrossOrigin(origins = "*")
public class PublicProjectController {

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    ProjectTagRepository projectTagRepository;

    @GetMapping("/projects/{id}")
    public ResponseEntity<PartialProjectWithTags> getPartialProjectById(@PathVariable(value = "id") long id){
        PartialProject p = projectRepository.getPublicProjectById(id);
        List<ProjectTag> pt = projectTagRepository.findProjectTagsByProjectId(id);

        PartialProjectWithTags ppt = new PartialProjectWithTags(p, pt);

        return ResponseEntity.ok(ppt);
    }
}
