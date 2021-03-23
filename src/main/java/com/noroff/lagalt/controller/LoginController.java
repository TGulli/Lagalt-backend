package com.noroff.lagalt.controller;

import com.noroff.lagalt.exceptions.AuthenticateException;
import com.noroff.lagalt.exceptions.CreateAuthenticationTokenException;
import com.noroff.lagalt.exceptions.NoItemFoundException;
import com.noroff.lagalt.model.User;
import com.noroff.lagalt.repository.UserRepository;
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
    public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequest authenticationRequest) throws Exception {

        // Validate username & password
        try {

            authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

        // GEnerates user
            UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
            String token = jwtTokenUtil.generateToken(userDetails);

            // Gets the correct User Object
            User returnedUser = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new NoItemFoundException("USER NOT MATCHING TOKEN"));
            return ResponseEntity.ok(new LoginGranted(returnedUser, new JwtResponse(token)));

        } catch (UsernameNotFoundException | AuthenticateException | NoItemFoundException e){
            throw new CreateAuthenticationTokenException("Failed to create authenticate token.");
        }
    }

    private void authenticate(String username, String password) throws AuthenticateException {

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        } catch (DisabledException | BadCredentialsException e) {
            throw new AuthenticateException("Authenticate failed.");
        }
    }


    @PostMapping("/login/facebook/{accessToken}")
    public ResponseEntity<?> createUserWithToken(@PathVariable(value = "accessToken") String accessToken) throws NoItemFoundException{
        try {
            User createdUser = FacebookTokenVerifier.verify(accessToken);

            if (createdUser != null) {

                User fetchedUser = userRepository.findByEmail(createdUser.getEmail()).orElseThrow(() -> new NoItemFoundException("No character by email"));

                if (fetchedUser != null) {

                    //Token generation
                    UserDetails userDetails = userDetailsService.loadUserByUsername(fetchedUser.getUsername());
                    String generatedToken = jwtTokenUtil.generateToken(userDetails);

                    //Return actual user
                    User returnedUser = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new NoItemFoundException("USER NOT MATCHING TOKEN"));
                    return ResponseEntity.ok(new LoginGranted(returnedUser, new JwtResponse(generatedToken)));
                }
                else {

                    createdUser = userRepository.save(createdUser);

                    UserDetails userDetails = userDetailsService.loadUserByUsername(createdUser.getUsername());
                    String generatedToken = jwtTokenUtil.generateToken(userDetails);


                    return ResponseEntity.ok(new LoginGranted(createdUser, new JwtResponse(generatedToken)));
                }

            }
        } catch (IOException | InterruptedException e) {
            System.out.println(e);
        }
        return null;
    }

    //Gewgle lawgin
    @PostMapping("/login/google/{token}")
    public ResponseEntity<?> oauthLogin(@PathVariable(value = "token") String token) throws NoItemFoundException {

        try {
            User created = GoogleTokenVerifier.verifiyGoogleToken(token);

            if (created != null){

                Optional<User> fetchedUser = userRepository.findByEmail(created.getEmail());

                if (fetchedUser.isPresent()){

                    UserDetails userDetails = userDetailsService.loadUserByUsername(fetchedUser.get().getUsername());

                    String generatedToken = jwtTokenUtil.generateToken(userDetails);

                    User returnedUser = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new NoItemFoundException("USER NOT MATCHING TOKEN"));
                    System.out.println("OLD Gewgle User: " + returnedUser);
                    System.out.println("OLD Gewgle Token: " + generatedToken);
                    return ResponseEntity.ok(new LoginGranted(returnedUser, new JwtResponse(generatedToken)));
                }
                else {
                    System.out.println("wut");

                    created = userRepository.save(created);

                    UserDetails userDetails = userDetailsService.loadUserByUsername(created.getUsername());
                    String generatedToken = jwtTokenUtil.generateToken(userDetails);

                    System.out.println("NEW Gewgle User: " + created);
                    System.out.println("NEW Gewgle Token: " + generatedToken);

                    return ResponseEntity.ok(new LoginGranted(created, new JwtResponse(generatedToken)));
                }
            }
            else {
                // Fiks bad requests å slekk.
                return null;
            }
        } catch (IOException | GeneralSecurityException io){
            System.out.println(io);
            return null;
        }
    }
}
