package com.noroff.lagalt.usertags.service;

import com.noroff.lagalt.projecttags.repository.ProjectTagRepository;
import com.noroff.lagalt.usertags.model.UserTag;
import com.noroff.lagalt.usertags.repository.UserTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Service class for the UserTag repository
 */

@Service
public class UserTagService {

    @Autowired
    private UserTagRepository userTagRepository;

    @Autowired
    private ProjectTagRepository projectTagRepository;


    public ResponseEntity<UserTag> create(UserTag userTag) {
        return ResponseEntity.ok(userTagRepository.save(userTag));
    }

    public ResponseEntity<List<UserTag>> getAll() {
        return ResponseEntity.ok(userTagRepository.findAll());
    }

    // Method that creates a set from all unique tags in users and projects.
    // This is used to populate the autosuggest box, to give users a
    public ResponseEntity<Set<String>> getAllTags() {
        Set<String> allTags = new HashSet<>();
        allTags.addAll(userTagRepository.findUniqueTags());
        allTags.addAll(projectTagRepository.findUniqueTags());
        return ResponseEntity.ok(allTags);
    }
}
