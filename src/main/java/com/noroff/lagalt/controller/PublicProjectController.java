package com.noroff.lagalt.controller;

import com.noroff.lagalt.project.model.PartialProject;
import com.noroff.lagalt.project.repository.ProjectRepository;
import com.noroff.lagalt.user.model.PartialUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/public")
@CrossOrigin(origins = "*")
public class PublicProjectController {

    @Autowired
    ProjectRepository projectRepository;

    @GetMapping("/project/{id}")
    public ResponseEntity<PartialProject> getUserById(@PathVariable(value = "id") long id){
        PartialProject p = projectRepository.findPartialById(id);

        // Validate project?

        return ResponseEntity.ok(p);
    }
}
