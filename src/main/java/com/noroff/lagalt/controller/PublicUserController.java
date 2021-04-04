package com.noroff.lagalt.controller;

import com.noroff.lagalt.user.model.PartialUser;
import com.noroff.lagalt.user.model.User;
import com.noroff.lagalt.user.repository.UserRepository;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;

@RestController
@RequestMapping("api/v1/public")
@CrossOrigin(origins = "*")
public class PublicUserController {

    @Autowired
    private UserRepository userRepository;

    private Bucket bucket;

    public PublicUserController(){
        Bandwidth bandwidth = Bandwidth.classic(20, Refill.intervally(20, Duration.ofSeconds(10)));
        this.bucket = Bucket4j.builder().addLimit(bandwidth).build();
    }


    @Operation(summary = "Get a partial user by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the user",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class)) }),
            @ApiResponse(responseCode = "429", description = "Too many requests",
                    content = @Content)})
    @GetMapping("/users/{id}")
    public ResponseEntity<PartialUser> getUserById(@PathVariable(value = "id") long id){
        if(!userRepository.existsById(id)) {
          return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        PartialUser p = userRepository.findPartialById(id);
        if(bucket.tryConsume(1)){ return ResponseEntity.ok(p);}
        // Validate user

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }


}
