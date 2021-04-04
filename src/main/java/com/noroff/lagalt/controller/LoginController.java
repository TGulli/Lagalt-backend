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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
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

    public LoginController() {
        Bandwidth bandwidth = Bandwidth.classic(10, Refill.intervally(10, Duration.ofSeconds(10)));
        this.internalBucket = Bucket4j.builder().addLimit(bandwidth).build();
        this.googleBucket = Bucket4j.builder().addLimit(bandwidth).build();
        this.facebookBucket = Bucket4j.builder().addLimit(bandwidth).build();

    }

    private final static int MAXEGENERALLENGTH = 50;


    @Operation(summary = "Create an authentication token for user for internal login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Created the token for user",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginGranted.class)) }),
            @ApiResponse(responseCode = "400", description = "Not valid username or password/Not valid LoginRequest object in body/Wrong username or password/User blocked",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "User not matching token",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "User is not verified",
                    content = @Content),
            @ApiResponse(responseCode = "429", description = "Too many requests",
                    content = @Content)})
    @PostMapping("/login/internal")
    public ResponseEntity<LoginGranted> createAuthenticationToken(@RequestBody LoginRequest authenticationRequest) {
        if (internalBucket.tryConsume(1)) {
            try {
                System.out.println(authenticationRequest);

                if (authenticationRequest.getUsername().length() > MAXEGENERALLENGTH || authenticationRequest.getPassword().length() > MAXEGENERALLENGTH) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Brukernavn eller passord stemmer ikke.");
                }
                // Validates username & password
                authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
                // Generates user
                UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
                String token = jwtTokenUtil.generateToken(userDetails);

                // Gets the correct User Object
                User returnedUser = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Bruker matcher ikke token."));
                if (!returnedUser.getVerified()) {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bruker er ikke verifisert.");
                }
                return ResponseEntity.ok(new LoginGranted(returnedUser, new JwtResponse(token)));

            } catch (UsernameNotFoundException e) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bruker matcher ikke token.");
            } catch (NullPointerException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Data i LoginRequest objektet er ikke satt.");
            }
        }
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }

    private void authenticate(String username, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (AuthenticationException exception) {
            String response = "";
            response = exception.getMessage().equals("Bad credentials") ?  "Feil brukernavn/passord" : exception.getMessage();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, response);
        }
    }


    @Operation(summary = "Create an authentication token for user for login with Facebook")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Created the token for user",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginGranted.class)) }),
            @ApiResponse(responseCode = "400", description = "Could not login with Facebook",
                    content = @Content),
            @ApiResponse(responseCode = "429", description = "Too many requests",
                    content = @Content)})
    @PostMapping("/login/facebook/{accessToken}")
    public ResponseEntity<LoginGranted> createUserWithToken(@PathVariable(value = "accessToken") String accessToken) {
        if (facebookBucket.tryConsume(1)) {
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
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kunne ikke logge inn med Facebook.");
            }
        }
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }


    @Operation(summary = "Create an authentication token for user for login with Google")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Created the token for user",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginGranted.class)) }),
            @ApiResponse(responseCode = "400", description = "Could not login with Google",
                    content = @Content),
            @ApiResponse(responseCode = "429", description = "Too many requests",
                    content = @Content)})
    @PostMapping("/login/google/{token}")
    public ResponseEntity<?> oauthLogin(@PathVariable(value = "token") String token) {
        if (googleBucket.tryConsume(1)) {
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
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kunne ikke logge inn med Google.");
            }
        }
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }
}
