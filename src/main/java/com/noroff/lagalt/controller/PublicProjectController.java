package com.noroff.lagalt.controller;

import com.noroff.lagalt.project.model.PartialProjectWithTags;
import com.noroff.lagalt.project.model.Project;
import com.noroff.lagalt.project.repository.ProjectRepository;
import com.noroff.lagalt.projecttags.repository.ProjectTagRepository;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("api/v1/public")
@CrossOrigin(origins = "*")
public class PublicProjectController {

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    ProjectTagRepository projectTagRepository;

    private Bucket bucket;

    // Limits the use of request to 20 per 10 seconds, so it is not possible to spam requests.
    public PublicProjectController(){
        Bandwidth bandwidth = Bandwidth.classic(20, Refill.intervally(20, Duration.ofSeconds(10)));
        this.bucket = Bucket4j.builder().addLimit(bandwidth).build();
    }

    // Gets project details based on the project ID from parameter
    @Operation(summary = "Get a partial project by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the project",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class)) }),
            @ApiResponse(responseCode = "429", description = "Too many requests",
                    content = @Content)})
    @GetMapping("/projects/{id}")
    public ResponseEntity<PartialProjectWithTags> getPartialProjectById(@PathVariable(value = "id") long id){
        if(bucket.tryConsume(1)) { // If not blocked
            if(!projectRepository.existsById(id)){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            //Finds the project, and makes a PartialProject with the public data to send.

            return ResponseEntity.ok(new PartialProjectWithTags(
                    projectRepository.getPublicProjectById(id),
                    projectTagRepository.findProjectTagsByProjectId(id)));
        }
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }

    // Returns projects as a list based on page number.
    @Operation(summary = "Get projects for public page-view")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Got projects for public page-view",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class))}) })
    @GetMapping("/projects/show/{page}")
    public ResponseEntity<Page<Project>> showProject(@PathVariable(value = "page") int page){
        return ResponseEntity.ok(projectRepository.findAll(PageRequest.of(page, 5)));
    }

    // Returns projects, where the name containing the given string as a list based on page number
    @Operation(summary = "Get projects matching search for public page-view")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Got projects matching search for public page-view",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class))}) })
    @GetMapping("/projects/search/{searchstring}/p/{page}")
    public ResponseEntity<Page<Project>> searchProject(@PathVariable(value = "searchstring") String searchstring,
                                                       @PathVariable(value = "page") int page){
        return ResponseEntity.ok(projectRepository.findByNameContainingIgnoreCase(searchstring, PageRequest.of(page, 5)));
    }

    // Returns projects with the given category as a list based on page number
    @Operation(summary = "Get projects matching filter for public page-view")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Got projects matching filter for public page-view",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class))}) })
    @GetMapping("/projects/filter/{filtertag}/p/{page}")
    public ResponseEntity<Page<Project>> filterProject(@PathVariable(value = "filtertag") String filtertag,
                                                       @PathVariable(value = "page") int page){
        return ResponseEntity.ok(projectRepository.findByCategoryIgnoreCase(filtertag, PageRequest.of(page, 5)));
    }

    // Returns projects with the given category as a list, and where the name containing the given string based on page number.
    @Operation(summary = "Get projects matching search string and filter for public page-view")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Got projects matching search and filter for public page-view",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class))}) })
    @GetMapping("/projects/search/{searchstring}/filter/{filtertag}/p/{page}")
    public ResponseEntity<Page<Project>> searchAndfilterProjects(@PathVariable(value = "searchstring") String searchstring,
                                                                 @PathVariable(value = "filtertag") String filtertag,
                                                                 @PathVariable(value = "page") int page){
        return ResponseEntity.ok(projectRepository.findByNameContainingIgnoreCaseAndCategoryIgnoreCase(searchstring, filtertag, PageRequest.of(page, 5)));
    }

}
