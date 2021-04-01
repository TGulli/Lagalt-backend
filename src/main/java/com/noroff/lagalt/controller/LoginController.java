package com.noroff.lagalt.controller;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
            System.out.println("Loginrequest " + authenticationRequest);
            authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

            System.out.println("Kom gjennom authenticate");

            // Generates user
            UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
            String token = jwtTokenUtil.generateToken(userDetails);

            System.out.println("Token laget " + token);

            // Gets the correct User Object
            Optional<User> returnedUser = userRepository.findByUsername(userDetails.getUsername()); //.orElseThrow(() -> new NoItemFoundException("USER NOT MATCHING TOKEN"));
            if (returnedUser.isEmpty()){
                System.out.println("returnuser is empty (findby username fungerte ikke)");
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User not matching token.");
            }
            if(!returnedUser.get().getVerified()){
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not verified.");
            }
            return ResponseEntity.ok(new LoginGranted(returnedUser.get(), new JwtResponse(token)));

        } catch (UsernameNotFoundException e){
            System.out.println("Username not found exeption " + e);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User not matching token.");
        } catch (NullPointerException e){
            System.out.println("nullpointer exeption: " + e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Data in LoginRequest object is null.");
        }
    }

    // Todo add email?
    private void authenticate(String username, String password) {
        System.out.println("kom inn i authenticate");
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException | BadCredentialsException e) {
            System.out.println("Badcredentials exeption " + e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Authenticate failed.");
        } catch (RuntimeException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User blocked.");
        }
    }


    @PostMapping("/login/facebook/{accessToken}")
    public ResponseEntity<LoginGranted> createUserWithToken(@PathVariable(value = "accessToken") String accessToken) {
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


        } catch (NullPointerException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not login with Facebook.");
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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not login with Google.");
        }
    }
}
