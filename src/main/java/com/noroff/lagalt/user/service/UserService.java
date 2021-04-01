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

import java.util.*;

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


    @Autowired
    private JwtTokenUtil jwtTokenUtil;


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


    public ResponseEntity<User> getById(Long id, String authHeader) {

        String requestingUser = jwtTokenUtil.getUsernameFromToken(authHeader.substring(7));


        Optional<User> fetchedUser = userRepository.findById(id);
        Optional<User> requestByUser = userRepository.findByUsername(requestingUser);

        if (fetchedUser.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No user found by id: " + id);
        }

        if (requestByUser.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Illegal request by user");
        }

        //REWORK ME
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

        String requestingUser = jwtTokenUtil.getUsernameFromToken(authHeader.substring(7));
        Optional<User> requestUser = userRepository.findByUsername(requestingUser);
        Optional<User> fetchedUser = userRepository.findById(id);

        if (requestUser.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No real user sent the request");
        }

        if (fetchedUser.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No user found to delete");
        }

        if (!requestUser.get().getId().equals(fetchedUser.get().getId())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not your user to delete");
        }

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

        String requestingUser = jwtTokenUtil.getUsernameFromToken(authHeader.substring(7));
        Optional<User> requestUser = userRepository.findByUsername(requestingUser);

        Optional<User> currentUserState = userRepository.findById(id);

        if (currentUserState.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No user found by id: " + id);
        }
        if (user == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The given user is null");
        }

        if (requestUser.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Illegal user tried to edit");
        }

        if (!requestUser.get().getId().equals(id)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Illegal user tried to edit");
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
