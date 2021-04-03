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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import javax.swing.text.html.Option;
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

    public RegisterController(){
        Bandwidth bandwidth = Bandwidth.classic(3, Refill.intervally(3, Duration.ofSeconds(10)));
        this.createUserbucket = Bucket4j.builder().addLimit(bandwidth).build();
        this.confirmUserbucket = Bucket4j.builder().addLimit(bandwidth).build();
    }


    @PostMapping("/register")
    public ResponseEntity<User> createUser(@RequestBody User user) {

        if(createUserbucket.tryConsume(1)) {

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
            user.setVerified(true);
            return userService.create(user);
        }
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }

    @GetMapping("/confirm-account")
    public ResponseEntity<String> confirmUserAccount(@RequestParam("token") String confirmationToken) {
        if(confirmUserbucket.tryConsume(1)) {
            ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);
            Optional<User> userOptional = userRepository.findByEmail(token.getUser().getEmail());
            String returnMessage;

            if (token != null && userOptional.isPresent()) {

                User user = userOptional.get();
                user.setVerified(true);
                userRepository.save(user);
                returnMessage = "Brukeren er opprettet. Du kan nå logge inn\n <a href=\"http://localhost:3000/login\">Trykk her</a>";
                return new ResponseEntity<>(returnMessage, HttpStatus.OK);
            } else {
                returnMessage = "Ugyldig lenke";
                return new ResponseEntity<>(returnMessage, HttpStatus.UNAUTHORIZED);
            }
        }
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();

    }


}
