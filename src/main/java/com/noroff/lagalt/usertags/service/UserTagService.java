package com.noroff.lagalt.usertags.service;

import com.noroff.lagalt.projecttags.repository.ProjectTagRepository;
import com.noroff.lagalt.usertags.model.UserTag;
import com.noroff.lagalt.usertags.repository.UserTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserTagService {

    @Autowired
    private UserTagRepository userTagRepository;

    @Autowired
    private ProjectTagRepository projectTagRepository;

    public ResponseEntity<UserTag> create(UserTag userTag) {
        UserTag createdTag = userTagRepository.save(userTag);
        return ResponseEntity.ok(createdTag);
    }


    public ResponseEntity<List<UserTag>> getAll() {
        List<UserTag> userTags = userTagRepository.findAll();
        return ResponseEntity.ok(userTags);
    }

    //Gets all!
    public ResponseEntity<List<String>> getAllTags() {
        List<String> allTags = new ArrayList<>();
        allTags.addAll(userTagRepository.findUniqueTags());
        allTags.addAll(projectTagRepository.findUniqueTags());
        return ResponseEntity.ok(allTags);
    }
}
