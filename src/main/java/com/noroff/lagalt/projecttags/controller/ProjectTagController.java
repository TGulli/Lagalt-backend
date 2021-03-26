package com.noroff.lagalt.projecttags.controller;


import com.noroff.lagalt.projecttags.model.ProjectTag;
import com.noroff.lagalt.projecttags.service.ProjectTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1")
@CrossOrigin(origins = "*")
public class ProjectTagController {

    @Autowired
    private ProjectTagService projectTagService;

    @GetMapping("/projecttags")
    public ResponseEntity<List<ProjectTag>> getUserTags() {
        return projectTagService.getAll();
    }

    @PostMapping("/projecttags")
    public ResponseEntity<ProjectTag> create(@RequestBody ProjectTag projectTag){
        return projectTagService.create(projectTag);
    }
}
