package com.noroff.lagalt.projectcollaborators.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
    public ResponseEntity<ProjectCollaborators> getProjectCollaboratorsById(@PathVariable (value="id") long id) {
        return projectCollaboratorsService.getById(id);
    }

    @GetMapping("project/{id}/collaborators/")
    public ResponseEntity<List<ProjectCollaborators>> getProjectCollaboratorsByProjectId(@PathVariable(value = "id") Long id, @RequestHeader(value = "Authorization") String authHeader){
        return projectCollaboratorsService.getAllByProjectId(id, authHeader);
    }

    @PostMapping("/project/collaborators")
    public ResponseEntity<ProjectCollaborators> addProjectCollaborator(@RequestBody ProjectCollaborators projectCollaborators){
        return projectCollaboratorsService.create(projectCollaborators);
    }

    @PutMapping("/project/collaborators/{id}")
    public ResponseEntity<ProjectCollaborators> update(@PathVariable Long id, @RequestBody ProjectCollaborators projectCollaborator, @RequestHeader(value = "Authorization") String authHeader)  {
        return projectCollaboratorsService.update(id, projectCollaborator, authHeader);
    }


}
