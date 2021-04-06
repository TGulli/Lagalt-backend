package com.noroff.lagalt.project.controller;

import com.noroff.lagalt.project.model.Project;
import com.noroff.lagalt.project.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1")
@CrossOrigin(origins = "*")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    // Gets all projects
    @Operation(summary = "Get all projects")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Got all projects",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Project.class)))}) })
    @GetMapping("/public/projects")
    public ResponseEntity<List<Project>> getAllProjects(){
        return projectService.getAll();
    }

    // Gets all projects by category
    @Operation(summary = "Get all projects by category", security = { @SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Got all projects by category",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Project.class)))}) })
            @GetMapping("/projects/category/{category}")
    public ResponseEntity<List<Project>> getAllFromCategory(@PathVariable(value="category") String category){
        return projectService.getAllFromCategory(category);
    }

    // Gets a project by id
    @Operation(summary = "Get a project by its id", security = { @SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the project",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Project.class)) }),
            @ApiResponse(responseCode = "400", description = "No project found by id",
                    content = @Content)})
    @GetMapping("/projects/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable (value="id") long id, @RequestHeader(value = "Authorization") String authHeader) {
        return projectService.getById(id, authHeader);
    }

    // Saves a project to the database
    @Operation(summary = "Create a project", security = { @SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created the project",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Project.class)) }),
            @ApiResponse(responseCode = "400", description = "Project already exist/Not valid input to create project",
                    content = @Content)})
    @PostMapping("/projects")
    public ResponseEntity<Project> addProject(@RequestBody Project project){
        return projectService.create(project);
    }

    // Get projects for a given page
    @Operation(summary = "Get projects for page-view")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Got projects for page-view",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class))}) })
    @GetMapping("/projects/show/{page}")
    public ResponseEntity<Page<Project>> showProject(@PathVariable(value = "page") int page, @RequestHeader(value = "Authorization") String authHeader){
        return projectService.showDisplayProjects(page, authHeader);
    }

    // Get projects with the name containing the search string for a given page
    @GetMapping("/projects/search/{searchstring}/p/{page}")
    public ResponseEntity<Page<Project>> searchProject(@PathVariable(value = "searchstring") String searchstring,
                                                       @PathVariable(value = "page") int page){
        return projectService.searchProjects(page, searchstring);
    }

    // Get projects filtered by category for a given page
    @GetMapping("/projects/filter/{filtertag}/p/{page}")
    public ResponseEntity<Page<Project>> filterProject(@PathVariable(value = "filtertag") String filtertag,
                                                       @PathVariable(value = "page") int page){
        return projectService.filterProjects(page, filtertag);
    }

    // Get projects with the name containing the search string, and filtered based on category for a given page
    @GetMapping("/projects/search/{searchstring}/filter/{filtertag}/p/{page}")
    public ResponseEntity<Page<Project>> searchAndfilterProjects(@PathVariable(value = "searchstring") String searchstring,
                                                                 @PathVariable(value = "filtertag") String filtertag,
                                                                 @PathVariable(value = "page") int page){
        return projectService.searchAndfilterProjects(page, filtertag, searchstring);
    }

    // Updates a project by its id
    @Operation(summary = "Update a project by its id", security = { @SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project edited",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Project.class)) }),
            @ApiResponse(responseCode = "400", description = "Not a valid project in the body/No project found to edit/Not valid input to update prject/Illegal user tried to update project",
                    content = @Content)})
    @PutMapping("/projects/{id}")
    public ResponseEntity<Project> editProject(@PathVariable(value="id") Long id, @RequestBody Project project, @RequestHeader(value = "Authorization") String authHeader) {
        return projectService.editProject(id, project, authHeader);
    }

    // Deletes a project with a id
    @Operation(summary = "Delete a project by its id", security = { @SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project deleted",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Project.class)) }),
            @ApiResponse(responseCode = "400", description = "Illegal user tried to delete project/No project found to delete",
                    content = @Content)})
    @DeleteMapping("/projects/{id}")
    public HttpStatus deleteProject(@PathVariable(value="id") Long id, @RequestHeader(value = "Authorization") String authHeader){

        return projectService.deleteProject(id, authHeader);
    }
}
