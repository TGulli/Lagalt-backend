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
           if (retrievedUser.getUsername().equals(user.getUsername()) && retrievedUser.getSecret().equals(user.getSecret())){
               return ResponseEntity.ok(retrievedUser);
           }
        }

        return null;
    }

    public ResponseEntity<User> getById(long id) throws NoItemFoundException{
        User fetchedUser = userRepository.findById(id).orElseThrow(() -> new NoItemFoundException("No character by id: " + id));
        //TODO:
        // Fetch by anyone but project owner. Find way to differentiate between project owner and everyone else
        // 3 state: ikkje logga, logga inn, owner
        if (fetchedUser.isHidden()){
            User hiddenUser = new User();
            hiddenUser.setUsername(fetchedUser.getUsername());
            return ResponseEntity.ok(hiddenUser);
        }

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
}
