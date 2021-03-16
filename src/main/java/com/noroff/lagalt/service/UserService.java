package com.noroff.lagalt.service;

import com.noroff.lagalt.exceptions.NoItemFoundException;
import com.noroff.lagalt.model.User;
import com.noroff.lagalt.project.model.Project;
import com.noroff.lagalt.project.repository.ProjectRepository;
import com.noroff.lagalt.repository.UserRepository;
import com.noroff.lagalt.utility.GoogleTokenVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@Service
public class UserService {

    @Autowired
    public UserRepository userRepository;

    @Autowired
    public ProjectRepository projectRepository;


    public ResponseEntity<User> create(User user){
        User x = userRepository.save(user);
        return ResponseEntity.ok(x);
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }


    //Make me pretty!
    public ResponseEntity<User> findByNameAndSecret(User user){
        List<User> users = userRepository.findAll();

        for (User retrievedUser : users){
           if (retrievedUser.getName().equals(user.getName()) && retrievedUser.getSecret().equals(user.getSecret())){
               return ResponseEntity.ok(retrievedUser);
           }
        }

        return null;
    }

    public ResponseEntity<User> getById(long id) throws NoItemFoundException{
        User fetchedUser = userRepository.findById(id).orElseThrow(() -> new NoItemFoundException("No character by id: " + id));
        return ResponseEntity.ok(fetchedUser);
    }

    public HttpStatus deleteUser(long id) throws  NoItemFoundException{
        User fetchedUser = userRepository.findById(id).orElseThrow(() -> new NoItemFoundException("No character by id: " + id));

        List<Project> projects = projectRepository.findAll();
        for (Project p: projects) {
            p.getOwners().remove(fetchedUser);
        }
        userRepository.delete(fetchedUser);
        HttpStatus status = HttpStatus.OK;
        return status;
    }

    public HttpStatus verifiyToken(String token){
        try {
            if (GoogleTokenVerifier.verifiyGoogleToken(token)){
                return HttpStatus.ACCEPTED;
            }
            else {
                return HttpStatus.CONFLICT;
            }
        } catch (IOException | GeneralSecurityException io){
            return HttpStatus.BAD_REQUEST;
        }
    }
}
