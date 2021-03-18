package com.noroff.lagalt.project.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.noroff.lagalt.exceptions.NoItemFoundException;
import com.noroff.lagalt.model.User;
import com.noroff.lagalt.project.model.Project;
import com.noroff.lagalt.project.service.ProjectService;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @GetMapping("/projects/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable (value="id") long id) throws NoItemFoundException {
        return projectService.getById(id);
    }

    @PostMapping("/projects")
    public ResponseEntity<Project> addProject(@RequestBody Project project){
        return projectService.create(project);
    }

    @GetMapping("/projects/show/{page}")
    public ResponseEntity<Page<Project>> showProject(@PathVariable(value = "page") int page){
        return projectService.showDisplayProjects(page);
    }

    @PutMapping("/projects/{id}")
    public ResponseEntity<Project> editProject(@PathVariable(value="id") Long id, @RequestBody ObjectNode json) throws JsonProcessingException, NoItemFoundException {
        JsonNode JsonUserId = json.get("user");
        Long userId = JsonUserId.get("id").asLong();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonProject = json.get("project");
        Project project = objectMapper.treeToValue(jsonProject, Project.class);

        return projectService.editProject(id, project, userId);
    }

}
