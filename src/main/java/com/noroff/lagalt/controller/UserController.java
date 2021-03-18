package com.noroff.lagalt.controller;

import com.noroff.lagalt.exceptions.NoItemFoundException;
import com.noroff.lagalt.model.Token;
import com.noroff.lagalt.model.User;
import com.noroff.lagalt.service.UserService;
import com.noroff.lagalt.utility.FacebookTokenVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/v1")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAll();
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable (value="id") long id) throws NoItemFoundException {
        return userService.getById(id);
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user){
        return userService.create(user);
    }

    @PostMapping("/users/name")//unique username?
    public ResponseEntity<User> findByNameAndSecret(@RequestBody User user) {
        return userService.findByNameAndSecret(user);
    }

    @DeleteMapping("/users/{id}")
    public HttpStatus deleteUser(@PathVariable (value="id") long id) throws NoItemFoundException{
        return userService.deleteUser(id);
    }

    @PostMapping("/users/{accessToken}")
    public ResponseEntity<User> createUserWithToken(@PathVariable (value = "accessToken") String accessToken) {
        try{
            User createdUSer = FacebookTokenVerifier.verify(accessToken);

            if (createdUSer != null){
                ResponseEntity<User> existingUser = findByNameAndSecret(createdUSer);
                if (existingUser != null){
                    return existingUser;
                }

                createdUSer.setHidden(false);
                return userService.create(createdUSer);
            }
        } catch (IOException | InterruptedException e){
            System.out.println(e);
        }
        return null;
    }

}
