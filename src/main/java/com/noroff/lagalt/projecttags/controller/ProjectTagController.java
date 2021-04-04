package com.noroff.lagalt.projecttags.controller;


import com.noroff.lagalt.projecttags.model.ProjectTag;
import com.noroff.lagalt.projecttags.service.ProjectTagService;
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
public class ProjectTagController {

    @Autowired
    private ProjectTagService projectTagService;

    @Operation(summary = "Get all projectTags", security = { @SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Got all projectTags",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ProjectTag.class)))}) })
    @GetMapping("/projecttags")
    public ResponseEntity<List<ProjectTag>> getUserTags() {
        return projectTagService.getAll();
    }


    @Operation(summary = "Create a projectTag", security = { @SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created a projectTag",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProjectTag.class))}),
            @ApiResponse(responseCode = "400", description = "Not a valid projectTag in the body",
                    content = @Content)})
    @PostMapping("/projecttags")
    public ResponseEntity<ProjectTag> create(@RequestBody ProjectTag projectTag){
        return projectTagService.create(projectTag);
    }
}
