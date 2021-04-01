package com.noroff.lagalt.projectcollaborators.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.noroff.lagalt.projectcollaborators.models.ProjectCollaborators;
import com.noroff.lagalt.projectcollaborators.service.ProjectCollaboratorsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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

    @PostMapping("/project/collaborators")
    public ResponseEntity<ProjectCollaborators> addProjectCollaborator(@RequestBody ProjectCollaborators projectCollaborators){
        return projectCollaboratorsService.create(projectCollaborators);
    }

    @PutMapping("/project/collaborators/{id}")
    public ResponseEntity<ProjectCollaborators> update(@PathVariable long id, @RequestBody ObjectNode json) {
        if (json == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "json objektet er null");
        }
        JsonNode JsonUserId = json.get("user");
        if (JsonUserId == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "json bruker objektet er null");
        }
        JsonNode userId = JsonUserId.get("id");
        if (userId == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "json sin id er null");
        }
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonProjectCollaborators = json.get("projectCollaborators");
        if (jsonProjectCollaborators == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "json prosjekt medlem objektet er null.");
        }
        try{
            ProjectCollaborators collaborators = objectMapper.treeToValue(jsonProjectCollaborators, ProjectCollaborators.class);
            return projectCollaboratorsService.update(id, collaborators, userId.asLong());
        } catch (JsonProcessingException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kunne ikke oppdatere prosjekt medlemmer.");
        }

    }


}
