package com.noroff.lagalt.usertags.controller;

import com.noroff.lagalt.usertags.model.UserTag;
import com.noroff.lagalt.usertags.service.UserTagService;
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

    @GetMapping("/usertags")
    public ResponseEntity<List<UserTag>> getUserTags() {
        return userTagService.getAll();
    }

    @GetMapping("/alltags")
    public ResponseEntity<Set<String>> getAllTags() {
        return userTagService.getAllTags();
    }

    @PostMapping("/usertags")
    public ResponseEntity<UserTag> create(@RequestBody UserTag userTag){
        return userTagService.create(userTag);
    }

}
