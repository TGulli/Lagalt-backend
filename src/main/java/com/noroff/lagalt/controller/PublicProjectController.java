package com.noroff.lagalt.controller;

import com.noroff.lagalt.project.model.PartialProject;
import com.noroff.lagalt.project.model.PartialProjectWithTags;
import com.noroff.lagalt.project.repository.ProjectRepository;
import com.noroff.lagalt.projecttags.model.ProjectTag;
import com.noroff.lagalt.projecttags.repository.ProjectTagRepository;
import com.noroff.lagalt.user.model.PartialUser;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
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

    @GetMapping("/projects/{id}")
    public ResponseEntity<PartialProjectWithTags> getPartialProjectById(@PathVariable(value = "id") long id){
        if(bucket.tryConsume(1)) {
            if(!projectRepository.existsById(id)){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            PartialProject p = projectRepository.getPublicProjectById(id);
            List<ProjectTag> pt = projectTagRepository.findProjectTagsByProjectId(id);

            PartialProjectWithTags ppt = new PartialProjectWithTags(p, pt);

            return ResponseEntity.ok(ppt);
        }
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }
}
