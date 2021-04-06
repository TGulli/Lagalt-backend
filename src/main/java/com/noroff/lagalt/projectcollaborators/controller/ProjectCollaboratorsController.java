package com.noroff.lagalt.projectcollaborators.controller;

import com.noroff.lagalt.projectcollaborators.models.ProjectCollaborators;
import com.noroff.lagalt.projectcollaborators.service.ProjectCollaboratorsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

    // Gets all project collaborators
    @Operation(summary = "Get all projectCollaborators", security = { @SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Got all projectCollaborators",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ProjectCollaborators.class)))}) })
    @GetMapping("/project/collaborators")
    public ResponseEntity<List<ProjectCollaborators>> getAllProjectCollaborators(){
        return projectCollaboratorsService.getAll();
    }

    // Get a project collaborator by its id
    @Operation(summary = "Get a projectCollaborator by its id", security = { @SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the projectCollaborator",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProjectCollaborators.class)) }),
            @ApiResponse(responseCode = "409", description = "No projectCollaborator found by id",
                    content = @Content)})
    @GetMapping("/project/collaborators/{id}")
    public ResponseEntity<ProjectCollaborators> getProjectCollaboratorsById(@PathVariable (value="id") long id) {
        return projectCollaboratorsService.getById(id);
    }

    // Gets all project collaborators by project id
    @Operation(summary = "Get all projectCollaborators by project id", security = { @SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Got all projectCollaborators by project id",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ProjectCollaborators.class)))}),
            @ApiResponse(responseCode = "400", description = "Illegal user tried to read",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "No projectCollaborator found by id",
                    content = @Content)})
    @GetMapping("project/{id}/collaborators/")
    public ResponseEntity<List<ProjectCollaborators>> getProjectCollaboratorsByProjectId(@PathVariable(value = "id") Long id, @RequestHeader(value = "Authorization") String authHeader){
        return projectCollaboratorsService.getAllByProjectId(id, authHeader);
    }

    // Creates a new project collaborator
    @Operation(summary = "Create a projectCollaborator", security = { @SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created the projectCollaborator",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProjectCollaborators.class)) }),
            @ApiResponse(responseCode = "400", description = "Not a valid projectCollaborator in the body/The user in body does not exist/The project in body does not exist/Not valid input to create projectCollaborator/Can't apply to a project you already own/Can't apply to same project twice",
            content = @Content),
            @ApiResponse(responseCode = "409", description = "The projectcallaborator already exists in the project",
                    content = @Content)})
    @PostMapping("/project/collaborators")
    public ResponseEntity<ProjectCollaborators> addProjectCollaborator(@RequestBody ProjectCollaborators projectCollaborators){
        return projectCollaboratorsService.create(projectCollaborators);
    }

    // Updates a project collaborator by its id
    @Operation(summary = "Update a projectCollaborator by its id", security = { @SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated the projectCollaborator",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProjectCollaborators.class)) }),
            @ApiResponse(responseCode = "400", description = "Not a valid projectCollaborator in the body/Not valid input to update projectCollaborator/User not allowed to update projectCollaborator",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Token user is not a legal user",
                    content = @Content)})
    @PutMapping("/project/collaborators/{id}")
    public ResponseEntity<ProjectCollaborators> update(@PathVariable Long id, @RequestBody ProjectCollaborators projectCollaborator, @RequestHeader(value = "Authorization") String authHeader)  {
        return projectCollaboratorsService.update(id, projectCollaborator, authHeader);
    }


}
