package com.noroff.lagalt.controller;

import com.noroff.lagalt.exceptions.CreateAuthenticationTokenException;
import com.noroff.lagalt.exceptions.UserExistException;
import com.noroff.lagalt.exceptions.UserNullException;
import com.noroff.lagalt.user.model.LoginMethod;
import com.noroff.lagalt.user.model.User;
import com.noroff.lagalt.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/v1/public")
@CrossOrigin(origins = "*")
public class RegisterController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        if (user == null || user.getEmail() == null || user.getUsername() == null || user.getSecret() == null){
            return UserNullException.catchException("User, user.getEmail, user.secret or user.username is null.");
        }
        try {
            String encodedPassword = new BCryptPasswordEncoder().encode(user.getSecret());
            user.setLoginMethod(LoginMethod.internal);
            user.setSecret(encodedPassword);
            user.setHidden(false);
            user.setEmail(user.getUsername()); // Todo add email
            user.setName(user.getUsername()); // Todo add name
            return userService.create(user);
        } catch (UserExistException e){
            return UserExistException.catchException(e.getMessage());
        } catch (UserNullException e){
            return UserNullException.catchException(e.getMessage());
        }
    }
}
