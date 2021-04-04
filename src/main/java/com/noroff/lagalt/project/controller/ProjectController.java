package com.noroff.lagalt.project.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.noroff.lagalt.user.model.User;
import com.noroff.lagalt.project.model.Project;
import com.noroff.lagalt.project.service.ProjectService;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1")
@CrossOrigin(origins = "*")
public class ProjectController {

    @Autowired
    private ProjectService projectService;


    //public
    @GetMapping("/public/projects")
    public ResponseEntity<List<Project>> getAllProjects(){
        return projectService.getAll();
    }

    //Kanskje slett?
    @GetMapping("/projects/category/{category}")
    public ResponseEntity<List<Project>> getAllFromCategory(@PathVariable(value="category") String category){
        return projectService.getAllFromCategory(category);
    }

    @GetMapping("/projects/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable (value="id") long id, @RequestHeader(value = "Authorization") String authHeader) {
        return projectService.getById(id, authHeader);
    }

    @PostMapping("/projects")
    public ResponseEntity<Project> addProject(@RequestBody Project project){
        return projectService.create(project);
    }

    //public
    @GetMapping("/projects/show/{page}")
    public ResponseEntity<Page<Project>> showProject(@PathVariable(value = "page") int page, @RequestHeader(value = "Authorization") String authHeader){
        return projectService.showDisplayProjects(page, authHeader);
    }

    @PutMapping("/projects/{id}")
    public ResponseEntity<Project> editProject(@PathVariable(value="id") Long id, @RequestBody Project project, @RequestHeader(value = "Authorization") String authHeader) {
        return projectService.editProject(id, project, authHeader);
    }

    @DeleteMapping("/projects/{id}")
    public HttpStatus deleteProject(@PathVariable(value="id") Long id, @RequestHeader(value = "Authorization") String authHeader){

        return projectService.deleteProject(id, authHeader);
    }
}
