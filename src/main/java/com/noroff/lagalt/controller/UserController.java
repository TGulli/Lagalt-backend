package com.noroff.lagalt.controller;

import com.noroff.lagalt.exceptions.NoItemFoundException;
import com.noroff.lagalt.model.User;
import com.noroff.lagalt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public List<User> getAllUsers() {
        return userService.getAll();
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<User> getUserById(@PathVariable (value="id") long id) throws NoItemFoundException {
        return userService.getById(id);
    }

    @PostMapping("/add")
    public ResponseEntity<User> createUser(@RequestBody User user){
        return userService.create(user);
    }

    @PostMapping("/find")
    public ResponseEntity<User> findByNameAndSecret(@RequestBody User user) {
        return userService.findByNameAndSecret(user);
    }


}
