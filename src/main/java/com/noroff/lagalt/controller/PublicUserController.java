package com.noroff.lagalt.controller;

import com.noroff.lagalt.user.model.PartialProjection;
import com.noroff.lagalt.user.model.PartialUser;
import com.noroff.lagalt.user.model.User;
import com.noroff.lagalt.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/public")
@CrossOrigin(origins = "*")
public class PublicUserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable(value = "id") long id){
        //PartialProjection x = userRepository.findPartialById(id);
        //System.out.println(x);
        //System.out.println(x.getUsername());

        User user = userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No User"));

        if (user != null){
            return ResponseEntity.ok(user);
        }


        return null;
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
