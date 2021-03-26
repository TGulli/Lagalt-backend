package com.noroff.lagalt.projectcollaborators.controller;

import com.noroff.lagalt.projectcollaborators.models.ProjectCollaborators;
import com.noroff.lagalt.projectcollaborators.service.ProjectCollaboratorsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1")
@CrossOrigin(origins = "*")
public class ProjectCollaboratorsController {

    @Autowired
    private ProjectCollaboratorsService projectCollaboratorsService;

    @GetMapping("/project/collaborators")
    public ResponseEntity<List<ProjectCollaborators>> getAllProjectCollaborators(){
        return projectCollaboratorsService.getAll();
    }

    @GetMapping("/project/collaborators/{id}")
    public ResponseEntity<?> getProjectCollaboratorsById(@PathVariable (value="id") long id) {
        return projectCollaboratorsService.getById(id);
    }

    @PostMapping("/project/collaborators")
    public ResponseEntity<?> addProjectCollaborator(@RequestBody ProjectCollaborators projectCollaborators){
        return projectCollaboratorsService.create(projectCollaborators);
    }

    @PutMapping("/project/collaborators/{id}")
    public ResponseEntity<?> update(@PathVariable long id, @RequestBody ProjectCollaborators collaborators){
        return projectCollaboratorsService.update(id, collaborators);
    }


}
