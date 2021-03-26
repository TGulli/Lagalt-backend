package com.noroff.lagalt.controller;

import com.noroff.lagalt.exceptions.*;
import com.google.common.base.VerifyException;
import com.noroff.lagalt.user.model.User;
import com.noroff.lagalt.user.repository.UserRepository;
import com.noroff.lagalt.security.JwtTokenUtil;
import com.noroff.lagalt.security.UserDetailsServiceImpl;
import com.noroff.lagalt.security.dto.JwtResponse;
import com.noroff.lagalt.security.dto.LoginGranted;
import com.noroff.lagalt.security.dto.LoginRequest;
import com.noroff.lagalt.utility.FacebookTokenVerifier;
import com.noroff.lagalt.utility.GoogleTokenVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/public")
@CrossOrigin(origins = "*")
public class LoginController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private UserRepository userRepository;


    @PostMapping("/login/internal")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequest authenticationRequest){
        // Todo new data for register, and new Exception based on email
        try {
            // Validate username & password
            authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

            // Generates user
            UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
            String token = jwtTokenUtil.generateToken(userDetails);

            // Gets the correct User Object
            User returnedUser = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new NoItemFoundException("USER NOT MATCHING TOKEN"));
            return ResponseEntity.ok(new LoginGranted(returnedUser, new JwtResponse(token)));

        } catch (NullPointerException | UsernameNotFoundException | AuthenticateException | NoItemFoundException e){
            return CreateAuthenticationTokenException.catchException("Failed to create authenticate token.");
        }
    }

    // Todo add email?
    private void authenticate(String username, String password) throws AuthenticateException {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        } catch (NullPointerException | DisabledException | BadCredentialsException e) {
            throw new AuthenticateException("Authenticate failed.");
        }
    }


    @PostMapping("/login/facebook/{accessToken}")
    public ResponseEntity<?> createUserWithToken(@PathVariable(value = "accessToken") String accessToken) {
        try {
            User createdUser = FacebookTokenVerifier.verify(accessToken);
            Optional<User> fetchedUser = userRepository.findByUsernameOrEmail(createdUser.getUsername(), createdUser.getEmail());
            User addUser;

            if (fetchedUser.isPresent()) {
                addUser = fetchedUser.get();
            }
            else {
                addUser = userRepository.save(createdUser);
            }

            //Token generation
            UserDetails userDetails = userDetailsService.loadUserByUsername(addUser.getUsername());
            String generatedToken = jwtTokenUtil.generateToken(userDetails);

            //Return actual user
            return ResponseEntity.ok(new LoginGranted(addUser, new JwtResponse(generatedToken)));

        } catch (NullPointerException | VerifyException e){
            return FacebookLoginException.catchException("Could not login with Facebook.");
        }
    }

    //Gewgle lawgin
    @PostMapping("/login/google/{token}")
    public ResponseEntity<?> oauthLogin(@PathVariable(value = "token") String token) {

        try {
            User created = GoogleTokenVerifier.verifiyGoogleToken(token);
            Optional<User> fetchedUser = userRepository.findByEmail(created.getEmail());
            User addUser;

            if (fetchedUser.isPresent()) {
                addUser = fetchedUser.get();
            }
            else {
                addUser = userRepository.save(created);
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(addUser.getUsername());
            String generatedToken = jwtTokenUtil.generateToken(userDetails);

            return ResponseEntity.ok(new LoginGranted(addUser, new JwtResponse(generatedToken)));

        } catch (IOException | GeneralSecurityException | VerifyException | NullPointerException e){
            return GoogleLoginException.catchException("Could not login with Google.");
        }
    }
}
