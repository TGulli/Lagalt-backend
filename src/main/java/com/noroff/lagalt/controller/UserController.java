package com.noroff.lagalt.controller;

import com.noroff.lagalt.exceptions.NoItemFoundException;
import com.noroff.lagalt.exceptions.UserAlreadyExist;
import com.noroff.lagalt.model.User;
import com.noroff.lagalt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<User> createUser(@RequestBody User user) throws UserAlreadyExist {
        return userService.create(user);
    }

    @PostMapping("/users/name")//unique username?
    public ResponseEntity<User> findByEmailAndSecret(@RequestBody User user) {
        return userService.findByEmailAndSecret(user);
    }

    @DeleteMapping("/delete/{id}")
    public HttpStatus deleteUser(@PathVariable (value="id") long id) throws NoItemFoundException{
        return userService.deleteUser(id);
    }


}
