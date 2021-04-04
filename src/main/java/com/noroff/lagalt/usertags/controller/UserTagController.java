package com.noroff.lagalt.usertags.controller;

import com.noroff.lagalt.message.model.Message;
import com.noroff.lagalt.usertags.model.UserTag;
import com.noroff.lagalt.usertags.service.UserTagService;
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
import java.util.Set;

@RestController
@RequestMapping("api/v1")
@CrossOrigin(origins = "*")
public class UserTagController {

    @Autowired
    private UserTagService userTagService;


    @Operation(summary = "Get all userTags", security = { @SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Got all userTags",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = UserTag.class)))}) })
    @GetMapping("/usertags")
    public ResponseEntity<List<UserTag>> getUserTags() {
        return userTagService.getAll();
    }


    @Operation(summary = "Get all userTags and projectTags", security = { @SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Got all tags",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = String.class)))}) })
    @GetMapping("/alltags")
    public ResponseEntity<Set<String>> getAllTags() {
        return userTagService.getAllTags();
    }


    @Operation(summary = "Create a userTag", security = { @SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created a userTag",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserTag.class)) })})
    @PostMapping("/usertags")
    public ResponseEntity<UserTag> create(@RequestBody UserTag userTag){
        return userTagService.create(userTag);
    }

}
