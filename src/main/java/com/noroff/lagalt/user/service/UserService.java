package com.noroff.lagalt.user.service;

import com.noroff.lagalt.security.JwtTokenUtil;
import com.noroff.lagalt.security.twofa.model.ConfirmationToken;
import com.noroff.lagalt.security.twofa.repository.ConfirmationTokenRepository;
import com.noroff.lagalt.security.twofa.service.EmailSenderService;
import com.noroff.lagalt.user.model.User;
import com.noroff.lagalt.project.repository.ProjectRepository;
import com.noroff.lagalt.user.repository.UserRepository;
import com.noroff.lagalt.usertags.model.UserTag;
import com.noroff.lagalt.usertags.repository.UserTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    //** FORMAT VARIABLES **

    private final static int MAXEMAILLENGTH = 350;
    private final static int MAXEBIOLENGTH = 1000;
    private final static int MAXEGENERALLENGTH = 50;
    private final static int LIMITADDTAGS = 1000;

    //** REPOSITORIES/SERVICES **

    @Autowired
    public UserRepository userRepository;

    @Autowired
    public ProjectRepository projectRepository;

    @Autowired
    public UserTagRepository userTagRepository;

    @Autowired
    public ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;


    public ResponseEntity<User> create(User user) {

        // ** MULTIPLE CHECKS WITH RETURN MESSAGES FOR FRONTEND USE **

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bruker objektet er ikke satt.");
        } else if (user.getUsername() == null || user.getUsername() == ""){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Brukernavn er ikke satt.");
        } else if (user.getName() == null || user.getName() == ""){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Navn er ikke satt.");
        } else if (user.getUsername().length() > MAXEGENERALLENGTH){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passord må være kortere enn " + MAXEGENERALLENGTH + " tegn.");
        } else if (user.getName().length() > MAXEGENERALLENGTH){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Navn må være kortere enn " + MAXEGENERALLENGTH + " tegn.");
        } else if (user.getEmail().length() > MAXEMAILLENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Epost addresse må være kortere enn " + MAXEMAILLENGTH + " tegn.");
        } else if (user.getLocale() != null && user.getLocale().length() > MAXEGENERALLENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sted må være kortere enn " + MAXEGENERALLENGTH + " tegn.");
        } else if (user.getBio() != null && user.getBio().length() > MAXEBIOLENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Biografi må være kortere enn " + MAXEBIOLENGTH + " tegn.");
        }

        // Retrieve user
        Optional<User> existingUser = userRepository.findByUsernameOrEmail(user.getUsername(), user.getEmail());

        // Checks if the user already exists
        if (existingUser.isPresent()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Bruker eksisterer allerede");
        }

        // Saves the user
        User returnUser = userRepository.save(user);

        // Generates a confirmation token to send users a confirmation email
        ConfirmationToken confirmationToken = new ConfirmationToken(user);
        confirmationTokenRepository.save(confirmationToken);

        //Generates and sends a confirmation email
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Fullfør registreringen!");
        mailMessage.setFrom("lagalt.noreply@gmail.com");
        mailMessage.setText("For å bekrefte din bruker, klikk her: "
                +"https://lagalt-service.herokuapp.com/api/v1/public/confirm-account?token="+confirmationToken.getConfirmationToken());

        emailSenderService.sendEmail(mailMessage);

        HttpStatus httpStatus = HttpStatus.CREATED;
        return new ResponseEntity<>(returnUser, httpStatus);
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }


    public ResponseEntity<User> getById(Long id, String authHeader) {

        // Get the user from jwt token
        String requestingUser = jwtTokenUtil.getUsernameFromToken(authHeader.substring(7));

        //Retrieve related user id
        Optional<User> fetchedUser = userRepository.findById(id);

        // Retrieve requesting user
        Optional<User> requestByUser = userRepository.findByUsername(requestingUser);

        if (fetchedUser.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No user found by id: " + id);
        }

        if (requestByUser.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Illegal request by user");
        }

        // Checks if the user in question is hidden and the user requesting the information is not the one in question
        // THat way a user always has access to his whole profile despite being in hidden mode
        if (fetchedUser.get().isHidden() && !fetchedUser.get().getId().equals(requestByUser.get().getId())){
            User hiddenUser = new User();
            hiddenUser.setId(fetchedUser.get().getId());
            hiddenUser.setUsername(fetchedUser.get().getUsername());
            hiddenUser.setName(fetchedUser.get().getName());
            hiddenUser.setHidden(true);
            return ResponseEntity.ok(hiddenUser);
        }

        return ResponseEntity.ok(fetchedUser.get());
    }


    public ResponseEntity<User> deleteUser(Long id, String authHeader) {

        // Gets the user in question, and the user who is requesting it
        String requestingUser = jwtTokenUtil.getUsernameFromToken(authHeader.substring(7));
        Optional<User> requestUser = userRepository.findByUsername(requestingUser);
        Optional<User> fetchedUser = userRepository.findById(id);

        if (requestUser.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No real user sent the request");
        }

        if (fetchedUser.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No user found to delete");
        }

        // Check that a user is not trying to delete anyone else
        if (!requestUser.get().getId().equals(fetchedUser.get().getId())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not your user to delete");
        }

        // Delete the related confirmation token entry, resetting whether a user has clicked the confirmation email or not
        Long userId = fetchedUser.get().getId();
        ConfirmationToken cfttoken = confirmationTokenRepository.findByUser_Id(userId);
        if(cfttoken != null) {
            confirmationTokenRepository.delete(cfttoken);
        }

        userRepository.delete(fetchedUser.get());
        return ResponseEntity.ok(fetchedUser.get());
    }

    // Edit -> Add skills with mEeEeEeeEeEee.
    public ResponseEntity<User> editUser(User user, Long id, String authHeader){

        //  Retrieve the requesting user
        String requestingUser = jwtTokenUtil.getUsernameFromToken(authHeader.substring(7));
        Optional<User> requestUser = userRepository.findByUsername(requestingUser);

        // Gets the current state of the user in question
        User currentUserState = userRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ingen bruker funnet med id: " + id));

        // Bunch of checks for valid changes to the user object
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bruker objektet er ikke satt.");
        } else if (user.getName().length() > MAXEGENERALLENGTH){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Navn må være kortere enn " + MAXEGENERALLENGTH + " tegn.");
        } else if (user.getLocale() != null && user.getLocale().length() > MAXEGENERALLENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sted må være kortere enn " + MAXEGENERALLENGTH + " tegn.");
        } else if (user.getBio() != null && user.getBio().length() > MAXEBIOLENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Biografi må være kortere enn " + MAXEBIOLENGTH + " tegn.");
        } else if (user.getUserTags() != null && user.getUserTags().size() > LIMITADDTAGS){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kan ikke legge til mer enn " + LIMITADDTAGS + " per redigering av profil.");
        }

        if (requestUser.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Illegal user tried to edit");
        }

        if (!requestUser.get().getId().equals(id)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Illegal user tried to edit");
        }

        // Checks if there are any edit changes and if so change the current user

        if (user.getName() != null && (!user.getName().equals(""))){
            currentUserState.setName(user.getName());
        }
        if (user.getBio() != null && !user.getBio().equals("")){
            currentUserState.setBio(user.getBio());
        }
        if (user.getLocale() != null && !user.getLocale().equals("")){
            currentUserState.setLocale(user.getLocale());
        }

        currentUserState.setHidden(user.isHidden());


        //Create the tags!
        if (user.getUserTags() != null) {

            // Generates new tags if the user has given tags
            List<String> currentTags = new ArrayList<>();
            for (UserTag t : currentUserState.getUserTags()) {
                if (t.getTag().length() > MAXEGENERALLENGTH){
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lengden på kvalifikasjoner kan ikke være lengre enn " + MAXEGENERALLENGTH);
                }
                currentTags.add(t.getTag().toLowerCase());
            }
            for (UserTag tag : user.getUserTags()) {
                String userTag = tag.getTag();
                if (!currentTags.contains(userTag.toLowerCase())) {
                    tag.setUser(currentUserState);
                    userTagRepository.save(tag);
                }
            }
        }

        return ResponseEntity.ok(userRepository.save(currentUserState));
    }


}
