package com.noroff.lagalt.project.controller;

import com.noroff.lagalt.project.model.Project;
import com.noroff.lagalt.project.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1")
@CrossOrigin(origins = "*")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @GetMapping("/projects")
    public ResponseEntity<List<Project>> getAllProjects(){
        return projectService.getAll();
    }

    @PostMapping("/projects")
    public ResponseEntity<Project> addProject(@RequestBody Project project){
        return projectService.create(project);
    }


}
