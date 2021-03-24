package com.noroff.lagalt.service;

import com.noroff.lagalt.exceptions.NoItemFoundException;
import com.noroff.lagalt.exceptions.UserExistException;
import com.noroff.lagalt.exceptions.UserNullException;
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
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    public UserRepository userRepository;

    @Autowired
    public ProjectRepository projectRepository;


    public ResponseEntity<User> create(User user) throws UserExistException, UserNullException {
        if (user == null || user.getEmail() == null || user.getUsername() == null){
            throw new UserNullException("User, user.getEmail or user.username is null.");
        }

        Optional<User> existingUser = userRepository.findByUsernameOrEmail(user.getUsername(), user.getEmail());

        if (existingUser.isPresent()){
            throw new UserExistException("User already exists");
        }
        User x = userRepository.save(user);
        return ResponseEntity.ok(x);
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }


//    //Make me pretty!
//    public ResponseEntity<User> findByNameAndSecret(User user){
//        List<User> users = userRepository.findAll();
//
//        for (User retrievedUser : users){
//           if (retrievedUser.getUsername().equals(user.getUsername()) && retrievedUser.getSecret().equals(user.getSecret())){
//               return ResponseEntity.ok(retrievedUser);
//           }
//        }
//
//        return null;
//    }

    public ResponseEntity<User> getById(long id) throws NoItemFoundException{
        User fetchedUser = userRepository.findById(id).orElseThrow(() -> new NoItemFoundException("No user by id: " + id + " found."));
        return ResponseEntity.ok(fetchedUser);
    }

    public HttpStatus deleteUser(long id) throws  NoItemFoundException{
        User fetchedUser = userRepository.findById(id).orElseThrow(() -> new NoItemFoundException("No user by id: " + id + " found."));

        List<Project> projects = projectRepository.findAll();
        for (Project p: projects) {
            p.getOwners().remove(fetchedUser);
        }
        userRepository.delete(fetchedUser);
        HttpStatus status = HttpStatus.OK;
        return status;
    }
}
