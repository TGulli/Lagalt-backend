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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final static int MAXEMAILLENGTH = 350;
    private final static int MAXEBIOLENGTH = 1000;
    private final static int MAXEGENERALLENGTH = 50;

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
        Optional<User> fetchedUser = userRepository.findById(id);

        if (fetchedUser.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No user found by id: " + id);
        }

        //REWORK ME
        if (fetchedUser.get().isHidden()){
            User hiddenUser = new User();
            hiddenUser.setId(fetchedUser.get().getId());
            hiddenUser.setUsername(fetchedUser.get().getUsername());
            hiddenUser.setName(fetchedUser.get().getName());
            hiddenUser.setHidden(true);
            return ResponseEntity.ok(hiddenUser);
        }

        return ResponseEntity.ok(fetchedUser.get());
    }

    public ResponseEntity<User> getUpdateUserById(Long id) {

        Optional<User> fetchedUser = userRepository.findById(id);

        if (fetchedUser.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No user found by id: " + id);
        }

        return ResponseEntity.ok(fetchedUser.get());
    }

    public HttpStatus deleteUser(Long id) {
        Optional<User> fetchedUser = userRepository.findById(id);

        if (fetchedUser.isEmpty()){
            return HttpStatus.BAD_REQUEST;
        }

        List<Project> projects = projectRepository.findAll();
        for (Project p : projects) {
            p.getOwners().remove(fetchedUser.get());
        }
        userRepository.delete(fetchedUser.get());
        HttpStatus status = HttpStatus.OK;
        return status;
    }

    // Edit -> Add skills with mEeEeEeeEeEee.
    public ResponseEntity<User> editUser(User user, Long id){
        Optional<User> currentUserState = userRepository.findById(id);

        if (currentUserState.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No user found by id: " + id);
        }
        if (user == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The given user is null");
        }

        if (user.getName() != null && (!user.getName().equals(""))){
            currentUserState.get().setName(user.getName());
        }
        if (user.getBio() != null && !user.getBio().equals("")){
            currentUserState.get().setBio(user.getBio());
        }
        if (user.getLocale() != null && !user.getLocale().equals("")){
            currentUserState.get().setLocale(user.getLocale());
        }

        currentUserState.get().setHidden(user.isHidden());


        //Create the tags!
        if (user.getUserTags() != null) {

            List<String> currentTags = new ArrayList<>();
            for (UserTag t : currentUserState.get().getUserTags()) {
                currentTags.add(t.getTag());
            }
            for (UserTag tag : user.getUserTags()) {
                String userTag = tag.getTag();
                if (!currentTags.contains(userTag)) {
                    tag.setUser(currentUserState.get());
                    userTagRepository.save(tag);
                }

            }
        }

        User editUser = userRepository.save(currentUserState.get());
        return ResponseEntity.ok(editUser);
    }
}
