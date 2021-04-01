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
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
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
import java.time.Duration;
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

    private Bucket internalBucket;
    private Bucket googleBucket;
    private Bucket facebookBucket;

    public LoginController(){
        Bandwidth bandwidth = Bandwidth.classic(10, Refill.intervally(10, Duration.ofSeconds(10)));
        this.internalBucket = Bucket4j.builder().addLimit(bandwidth).build();
        this.googleBucket = Bucket4j.builder().addLimit(bandwidth).build();
        this.facebookBucket = Bucket4j.builder().addLimit(bandwidth).build();

    }



    @PostMapping("/login/internal")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequest authenticationRequest){
        // Todo new data for register, and new Exception based on email
        if(internalBucket.tryConsume(1)){
            try {
                // Validate username & password
                authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

                // Generates user
                UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
                String token = jwtTokenUtil.generateToken(userDetails);

                // Gets the correct User Object
                Optional<User> returnedUser = userRepository.findByUsername(userDetails.getUsername()); //.orElseThrow(() -> new NoItemFoundException("USER NOT MATCHING TOKEN"));
                if (returnedUser.isEmpty()){
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User not matching token.");
                }
                if(!returnedUser.get().getVerified()){
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not verified.");
                }
                return ResponseEntity.ok(new LoginGranted(returnedUser.get(), new JwtResponse(token)));

            } catch (UsernameNotFoundException e){
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User not matching token.");
            } catch (NullPointerException e){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Data in LoginRequest object is null.");
            }
        }
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }

    // Todo add email?
    private void authenticate(String username, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        } catch (DisabledException | BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Authenticate failed.");
        }
    }


    @PostMapping("/login/facebook/{accessToken}")
    public ResponseEntity<LoginGranted> createUserWithToken(@PathVariable(value = "accessToken") String accessToken) {
        if(facebookBucket.tryConsume(1)) {
            try {
                User createdUser = FacebookTokenVerifier.verify(accessToken);
                Optional<User> fetchedUser = userRepository.findByUsernameOrEmail(createdUser.getUsername(), createdUser.getEmail());
                User addUser;

                if (fetchedUser.isPresent()) {
                    addUser = fetchedUser.get();
                } else {
                    addUser = userRepository.save(createdUser);
                }

                //Token generation
                UserDetails userDetails = userDetailsService.loadUserByUsername(addUser.getUsername());
                String generatedToken = jwtTokenUtil.generateToken(userDetails);

                //Return actual user
                return ResponseEntity.ok(new LoginGranted(addUser, new JwtResponse(generatedToken)));


            } catch (NullPointerException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not login with Facebook.");
            }
        }
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }

    //Gewgle lawgin
    @PostMapping("/login/google/{token}")
    public ResponseEntity<?> oauthLogin(@PathVariable(value = "token") String token) {
        if(googleBucket.tryConsume(1)) {
            try {
                User created = GoogleTokenVerifier.verifiyGoogleToken(token);
                Optional<User> fetchedUser = userRepository.findByEmail(created.getEmail());
                User addUser;

                if (fetchedUser.isPresent()) {
                    addUser = fetchedUser.get();
                } else {
                    addUser = userRepository.save(created);
                }

                UserDetails userDetails = userDetailsService.loadUserByUsername(addUser.getUsername());
                String generatedToken = jwtTokenUtil.generateToken(userDetails);

                return ResponseEntity.ok(new LoginGranted(addUser, new JwtResponse(generatedToken)));

            } catch (IOException | GeneralSecurityException | VerifyException | NullPointerException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not login with Google.");
            }
        }
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }
}
