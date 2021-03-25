package com.noroff.lagalt.user.controller;

import com.noroff.lagalt.exceptions.NoItemFoundException;
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

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAll();
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable(value = "id") long id) throws NoItemFoundException {
        return userService.getById(id);
    }

    @DeleteMapping("/users/{id}")
    public HttpStatus deleteUser(@PathVariable(value = "id") long id) throws NoItemFoundException {
        return userService.deleteUser(id);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> edit(@RequestBody User user, @PathVariable(value = "id") long id) throws NoItemFoundException{
        return userService.editUser(user, id);
    }

}
