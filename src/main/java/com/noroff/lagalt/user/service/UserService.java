package com.noroff.lagalt.user.service;

import com.noroff.lagalt.security.twofa.model.ConfirmationToken;
import com.noroff.lagalt.security.twofa.repository.ConfirmationTokenRepository;
import com.noroff.lagalt.security.twofa.service.EmailSenderService;
import com.noroff.lagalt.user.model.LoginMethod;
import com.noroff.lagalt.user.model.User;
import com.noroff.lagalt.project.model.Project;
import com.noroff.lagalt.project.repository.ProjectRepository;
import com.noroff.lagalt.user.repository.UserRepository;
import com.noroff.lagalt.usertags.model.UserTag;
import com.noroff.lagalt.usertags.repository.UserTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class UserService {
    private final static int MAXEMAILLENGTH = 350;
    private final static int MAXEBIOLENGTH = 1000;
    private final static int MAXEGENERALLENGTH = 50;
    private final static int LIMITADDTAGS = 1000;

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


    public ResponseEntity<User> create(User user) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bruker objektet er ikke satt.");
        } else if (user.getUsername() == null || user.getUsername() == ""){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Brukernavn er ikke satt.");
        } else if (user.getName() == null || user.getName() == ""){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Navn er ikke satt.");
        } else if (user.getSecret() == null || user.getSecret() == "") {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passord er ikke satt.");
        } else if (user.getUsername().length() > MAXEGENERALLENGTH){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passord må være kortere enn " + MAXEGENERALLENGTH + " tegn.");
        } else if (user.getName().length() > MAXEGENERALLENGTH){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Navn må være kortere enn " + MAXEGENERALLENGTH + " tegn.");
        } else if (user.getSecret().length() > MAXEGENERALLENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passord må være kortere enn  " + MAXEGENERALLENGTH + " tegn.");
        } else if (user.getEmail().length() > MAXEMAILLENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Epost addresse må være kortere enn " + MAXEMAILLENGTH + " tegn.");
        } else if (user.getLocale() != null && user.getLocale().length() > MAXEGENERALLENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sted må være kortere enn " + MAXEGENERALLENGTH + " tegn.");
        } else if (user.getBio() != null && user.getBio().length() > MAXEBIOLENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Biografi må være kortere enn " + MAXEBIOLENGTH + " tegn.");
        }

        String encodedPassword = new BCryptPasswordEncoder().encode(user.getSecret());
        user.setLoginMethod(LoginMethod.internal);
        user.setSecret(encodedPassword);
        user.setHidden(false);
        user.setVerified(false);
        user.setOwnedProjects(new ArrayList<>());
        user.setCollaboratorIn(new ArrayList<>());
        user.setUserTags(new ArrayList<>());
        user.setMessages(new ArrayList<>());

       Optional<User> existingUser = userRepository.findByUsernameOrEmail(user.getUsername(), user.getEmail());

        if (existingUser.isPresent()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Bruker eksisterer allerede");
        }
        User returnUser = userRepository.save(user);

        ConfirmationToken confirmationToken = new ConfirmationToken(user);

        confirmationTokenRepository.save(confirmationToken);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Fullfør registreringen!");
        mailMessage.setFrom("lagalt.noreply@gmail.com");
        mailMessage.setText("For å bekrefte din bruker, klikk her: "
                +"http://localhost:8080/api/v1/public/confirm-account?token="+confirmationToken.getConfirmationToken());

        emailSenderService.sendEmail(mailMessage);

        return ResponseEntity.ok(returnUser);
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }


    public ResponseEntity<User> getById(Long id) {
        User fetchedUser = userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ingen bruker funnet med id: " + id));

        //REWORK ME
        if (fetchedUser.isHidden()){
            User hiddenUser = new User();
            hiddenUser.setId(fetchedUser.getId());
            hiddenUser.setUsername(fetchedUser.getUsername());
            hiddenUser.setName(fetchedUser.getName());
            hiddenUser.setHidden(true);
            return ResponseEntity.ok(hiddenUser);
        }

        return ResponseEntity.ok(fetchedUser);
    }

    public ResponseEntity<User> getUpdateUserById(Long id) {
        return ResponseEntity.ok(userRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ingen bruker funnet med id: " + id)));
    }

    public HttpStatus deleteUser(Long id) {
        User fetchedUser = userRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ingen bruker funnet med id: " + id));

        List<Project> projects = projectRepository.findAll();
        for (Project p : projects) {
            p.getOwners().remove(fetchedUser);
        }
        userRepository.delete(fetchedUser);
        return HttpStatus.OK;
    }

    // Edit -> Add skills with mEeEeEeeEeEee.
    public ResponseEntity<User> editUser(User user, Long id){
        User currentUserState = userRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ingen bruker funnet med id: " + id));

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bruker objektet er ikke satt.");
        } else if (user.getName() != null && user.getName() == ""){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Navn kan ikke settes til en tom String.");
        } else if (user.getName().length() > MAXEGENERALLENGTH){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Navn må være kortere enn " + MAXEGENERALLENGTH + " tegn.");
        } else if (user.getLocale() != null && user.getLocale().length() > MAXEGENERALLENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sted må være kortere enn " + MAXEGENERALLENGTH + " tegn.");
        } else if (user.getBio() != null && user.getBio().length() > MAXEBIOLENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Biografi må være kortere enn " + MAXEBIOLENGTH + " tegn.");
        } else if (user.getUserTags() != null && user.getUserTags().size() > LIMITADDTAGS){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kan ikke legge til mer enn " + LIMITADDTAGS + " per redigering av profil.");
        }

        currentUserState.setName(user.getName());
        currentUserState.setBio(user.getBio());
        currentUserState.setLocale(user.getLocale());
        currentUserState.setHidden(user.isHidden());


        //Adds the tags!
        if (user.getUserTags() != null) {

            List<String> currentTags = new ArrayList<>();
            for (UserTag t : currentUserState.getUserTags()) {
                if (t.getTag().length() > MAXEGENERALLENGTH){
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lengden på kvalifikasjoner kan ikke være lengre enn " + MAXEGENERALLENGTH);
                }
                currentTags.add(t.getTag().toLowerCase(Locale.ROOT));
            }
            for (UserTag tag : user.getUserTags()) {
                String userTag = tag.getTag();
                if (!currentTags.contains(userTag.toLowerCase(Locale.ROOT))) {
                    tag.setUser(currentUserState);
                    userTagRepository.save(tag);
                }
            }
        }

        return ResponseEntity.ok(userRepository.save(currentUserState));
    }
}
