package com.noroff.lagalt.controller;

import com.noroff.lagalt.project.model.PartialProject;
import com.noroff.lagalt.project.model.PartialProjectWithTags;
import com.noroff.lagalt.project.repository.ProjectRepository;
import com.noroff.lagalt.projecttags.model.ProjectTag;
import com.noroff.lagalt.projecttags.repository.ProjectTagRepository;
import com.noroff.lagalt.user.model.PartialUser;
import com.noroff.lagalt.user.model.User;
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

import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping("api/v1/public")
@CrossOrigin(origins = "*")
public class PublicProjectController {

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    ProjectTagRepository projectTagRepository;

    private Bucket bucket;


    public PublicProjectController(){
        Bandwidth bandwidth = Bandwidth.classic(20, Refill.intervally(20, Duration.ofSeconds(10)));
        this.bucket = Bucket4j.builder().addLimit(bandwidth).build();
    }

    @Operation(summary = "Get a partial project by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the project",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class)) }),
            @ApiResponse(responseCode = "429", description = "Too many requests",
                    content = @Content)})
    @GetMapping("/projects/{id}")
    public ResponseEntity<PartialProjectWithTags> getPartialProjectById(@PathVariable(value = "id") long id){
        if(bucket.tryConsume(1)) {
            PartialProject p = projectRepository.getPublicProjectById(id);
            List<ProjectTag> pt = projectTagRepository.findProjectTagsByProjectId(id);

            PartialProjectWithTags ppt = new PartialProjectWithTags(p, pt);

            return ResponseEntity.ok(ppt);
        }
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }
}
