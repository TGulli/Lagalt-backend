package com.noroff.lagalt.controller;

import com.noroff.lagalt.user.model.PartialUser;
import com.noroff.lagalt.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/public")
@CrossOrigin(origins = "*")
public class PublicUserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/users/{id}")
    public ResponseEntity<PartialUser> getUserById(@PathVariable(value = "id") long id){
        PartialUser p = userRepository.findPartialById(id);

        // Validate user

        return ResponseEntity.ok(p);
    }

    @GetMapping("/users/email/{email}")
    public ResponseEntity<Boolean> existingEmail(@PathVariable(value = "email") String email) {
        return ResponseEntity.ok(userRepository.existsByEmail(email));
    }

    @GetMapping("/users/username/{username}")
    public ResponseEntity<Boolean> existingUsername(@PathVariable(value = "username") String username) {
        return ResponseEntity.ok(userRepository.existsByUsername(username));
    }

}
