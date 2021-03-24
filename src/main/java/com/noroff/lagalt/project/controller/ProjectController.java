package com.noroff.lagalt.project.controller;

import com.noroff.lagalt.exceptions.NoItemFoundException;
import com.noroff.lagalt.project.model.Project;
import com.noroff.lagalt.project.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1")
@CrossOrigin(origins = "http://localhost:3000")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @GetMapping("/projects")
    public ResponseEntity<List<Project>> getAllProjects(){
        return projectService.getAll();
    }

    //Kanskje slett?
    @GetMapping("/projects/category/{category}")
    public ResponseEntity<List<Project>> getAllFromCategory(@PathVariable(value="category") String category){
        return projectService.getAllFromCategory(category);
    }

    @GetMapping("/projects/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable (value="id") long id) throws NoItemFoundException {
        return projectService.getById(id);
    }

    @PostMapping("/projects")
    public ResponseEntity<Project> addProject(@RequestBody Project project){
        return projectService.create(project);
    }

    @GetMapping("/public/projects/show/{page}")
    public ResponseEntity<Page<Project>> showProject(@PathVariable(value = "page") int page){
        return projectService.showDisplayProjects(page);
    }

    @PutMapping("/projects/{id}")
    public ResponseEntity<Project> editProject(@PathVariable(value="id") Long id, @RequestBody Project project) throws NoItemFoundException{
        return projectService.editProject(id, project);
    }

}
