package com.noroff.lagalt.user.controller;


import com.noroff.lagalt.security.JwtTokenUtil;
import com.noroff.lagalt.user.model.User;
import com.noroff.lagalt.user.service.UserService;
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

    @Autowired
    private JwtTokenUtil jwtTokenUtil;



    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAll();
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable(value = "id") Long id, @RequestHeader(value = "Authorization") String authHeader) {
        return userService.getById(id, authHeader);
    }

    @GetMapping("/users/update/{id}")
    public ResponseEntity<User> getUpdateUser(@PathVariable(value = "id") Long id) {
        return userService.getUpdateUserById(id);
    }

    @DeleteMapping("/users/{id}")
    public HttpStatus deleteUser(@PathVariable(value = "id") Long id) {
        return userService.deleteUser(id);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> edit(@RequestBody User user, @PathVariable(value = "id") Long id) {
        return userService.editUser(user, id);
    }


}
