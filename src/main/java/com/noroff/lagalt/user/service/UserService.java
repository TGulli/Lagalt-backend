package com.noroff.lagalt.user.service;

import com.noroff.lagalt.security.twofa.model.ConfirmationToken;
import com.noroff.lagalt.security.twofa.repository.ConfirmationTokenRepository;
import com.noroff.lagalt.security.twofa.service.EmailSenderService;
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
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

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
        if (user == null || user.getUsername() == null || user.getSecret() == null){
//        if (user == null || user.getEmail() == null || user.getUsername() == null || user.getName() == null || user.getSecret() == null){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "User, user.email, user.name, user.secret or user.username is null.");
        }
       Optional<User> existingUser = userRepository.findByUsernameOrEmail(user.getUsername(), user.getEmail());

        if (existingUser.isPresent()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists");
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


        Long userId = fetchedUser.get().getId();
        ConfirmationToken cfttoken = confirmationTokenRepository.findByUser_Id(userId);
        confirmationTokenRepository.delete(cfttoken);


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
