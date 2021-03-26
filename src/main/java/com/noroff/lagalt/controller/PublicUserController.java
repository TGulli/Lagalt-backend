package com.noroff.lagalt.controller;

import com.noroff.lagalt.exceptions.NoItemFoundException;
import com.noroff.lagalt.user.model.PartialProjection;
import com.noroff.lagalt.user.model.PartialUser;
import com.noroff.lagalt.user.model.User;
import com.noroff.lagalt.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/public")
@CrossOrigin(origins = "*")
public class PublicUserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable(value = "id") long id) throws NoItemFoundException{
        //PartialProjection x = userRepository.findPartialById(id);
        //System.out.println(x);
        //System.out.println(x.getUsername());

        User user = userRepository.findById(id).orElseThrow( () -> new NoItemFoundException("User not found"));

        if (user != null){
            return ResponseEntity.ok(user);
        }


        return null;
    }

}
