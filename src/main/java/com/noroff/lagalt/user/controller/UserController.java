package com.noroff.lagalt.user.controller;


import com.noroff.lagalt.security.JwtTokenUtil;
import com.noroff.lagalt.user.model.User;
import com.noroff.lagalt.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Book;
import java.util.List;

@RestController
@RequestMapping("api/v1")
@CrossOrigin(origins = "*")
public class UserController {

    /**
     * User controller, exposing all user related endpoints
     */

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;


    @Operation(summary = "Get all users", security = { @SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Got all users",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = User.class)))}) })
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAll();
    }

    @Operation(summary = "Get a user by its id", security = { @SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the user",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class)) }),
            @ApiResponse(responseCode = "400", description = "No user found by id/ Illegal request by user",
                    content = @Content)})
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable(value = "id") Long id, @RequestHeader(value = "Authorization") String authHeader) {
        return userService.getById(id, authHeader);
    }

    @Operation(summary = "Delete a user by its id", security = { @SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class)) }),
            @ApiResponse(responseCode = "400", description = "No real user sent the request/No user found to delete/Not your user to delete",
                    content = @Content)})
    @DeleteMapping("/users/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable(value = "id") Long id, @RequestHeader(value = "Authorization") String authHeader) {
        return userService.deleteUser(id, authHeader);
    }

    @Operation(summary = "Edit a user by its id", security = { @SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User edited",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class)) }),
            @ApiResponse(responseCode = "400", description = "No real user sent the request/No user found to edit/Not valid input to update user",
                    content = @Content)})
    @PutMapping("/users/{id}")
    public ResponseEntity<User> edit(@RequestBody User user, @PathVariable(value = "id") Long id, @RequestHeader(value = "Authorization") String authHeader) {
        return userService.editUser(user, id, authHeader);
    }


}
