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
    public ResponseEntity<List<ProjectCollaborators>> getProjectCollaboratorsByProjectId(@PathVariable(value = "id") long id ){
        return projectCollaboratorsService.getAllByProjectId(id);
    }



    @PostMapping("/project/collaborators")
    public ResponseEntity<ProjectCollaborators> addProjectCollaborator(@RequestBody ProjectCollaborators projectCollaborators){
        return projectCollaboratorsService.create(projectCollaborators);
    }

    @PutMapping("/project/collaborators/{id}")
    public ResponseEntity<ProjectCollaborators> update(@PathVariable Long id, @RequestBody ObjectNode json) throws JsonProcessingException {
        JsonNode JsonUserId = json.get("user");
        Long userId = JsonUserId.get("id").asLong();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonProjectCollaborators = json.get("projectCollaborators");
        ProjectCollaborators collaborators = objectMapper.treeToValue(jsonProjectCollaborators, ProjectCollaborators.class);
        return projectCollaboratorsService.update(id, collaborators, userId);
    }


}
