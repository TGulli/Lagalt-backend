package com.noroff.lagalt.controller;

import com.noroff.lagalt.security.twofa.model.ConfirmationToken;
import com.noroff.lagalt.security.twofa.repository.ConfirmationTokenRepository;
import com.noroff.lagalt.security.twofa.service.EmailSenderService;
import com.noroff.lagalt.user.model.LoginMethod;
import com.noroff.lagalt.user.model.User;
import com.noroff.lagalt.user.repository.UserRepository;
import com.noroff.lagalt.user.service.UserService;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.Optional;


@RestController
@RequestMapping("api/v1/public")
@CrossOrigin(origins = "*")
public class RegisterController {

    @Autowired
    private UserService userService;

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    private UserRepository userRepository;

    private Bucket createUserbucket;
    private Bucket confirmUserbucket;

    // Limits the use of request to 3 per 10 seconds, so it is not possible to spam requests.
    public RegisterController(){
        Bandwidth bandwidth = Bandwidth.classic(3, Refill.intervally(3, Duration.ofSeconds(10)));
        this.createUserbucket = Bucket4j.builder().addLimit(bandwidth).build();
        this.confirmUserbucket = Bucket4j.builder().addLimit(bandwidth).build();
    }


    // Adds a new user to the database, where the password is stored encoded
    @Operation(summary = "Create a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Created the user",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class)) }),
            @ApiResponse(responseCode = "400", description = "Not a valid user object in the body/Email or username already in use in an existing user/Not valid input to create user",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "User already exists",
                    content = @Content),
            @ApiResponse(responseCode = "429", description = "Too many requests",
                    content = @Content)})
    @PostMapping("/register")
    public ResponseEntity<User> createUser(@RequestBody User user) {

        if(createUserbucket.tryConsume(1)) { // If not blocked

            // If data is missing, or the username/ email is already defined in the database, it throws an ResponseStatusException
            if (user == null || user.getEmail() == null || user.getUsername() == null || user.getName() == null || user.getSecret() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User, user.email, user.name, user.secret or user.username is null.");
            }

            if(userRepository.existsByEmail(user.getEmail())){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "E-postadressen er allerde i bruk");
            }

            if(userRepository.existsByUsername(user.getUsername())){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Brukernavnet er allerde i bruk");
            }

            String encodedPassword = new BCryptPasswordEncoder().encode(user.getSecret());
            user.setLoginMethod(LoginMethod.internal);
            user.setSecret(encodedPassword);
            user.setHidden(false);
            user.setVerified(false);
            return userService.create(user);
        }
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }

    // Confirms the account when a user has send a verification request. Sets the user from token to verified.
    @Operation(summary = "Confirm user account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Confirmed user account",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class)) }),
            @ApiResponse(responseCode = "401", description = "Invalid confirmation link",
                    content = @Content),
            @ApiResponse(responseCode = "429", description = "Too many requests",
                    content = @Content)})
    @GetMapping("/confirm-account")
    public ResponseEntity<String> confirmUserAccount(@RequestParam("token") String confirmationToken) {
        if(confirmUserbucket.tryConsume(1)) { // If not blocked
            ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);
            Optional<User> userOptional = userRepository.findByEmail(token.getUser().getEmail());

            if (userOptional.isPresent()) {

                User user = userOptional.get();
                user.setVerified(true);
                userRepository.save(user);
                return new ResponseEntity<>("Brukeren er opprettet. Du kan n?? logge inn\n <a href=\"https://lagalt2021.herokuapp.com/login\">Trykk her</a>", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Ugyldig lenke", HttpStatus.UNAUTHORIZED);
            }
        }
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }
}
