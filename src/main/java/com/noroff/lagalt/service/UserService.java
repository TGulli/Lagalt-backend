package com.noroff.lagalt.service;

import com.noroff.lagalt.exceptions.NoItemFoundException;
import com.noroff.lagalt.exceptions.UserAlreadyExist;
import com.noroff.lagalt.model.User;
import com.noroff.lagalt.project.model.Project;
import com.noroff.lagalt.project.repository.ProjectRepository;
import com.noroff.lagalt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    public UserRepository userRepository;

    @Autowired
    public ProjectRepository projectRepository;


    public ResponseEntity<User> create(User user) throws UserAlreadyExist{
        Optional<User> newUser = userRepository.findByEmail(user.getEmail());
        if (newUser.isPresent()){
          throw new UserAlreadyExist("User with the given email already exists.");
        }
        return ResponseEntity.ok(userRepository.save(user));
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }


    //Make me pretty!
    public ResponseEntity<User> findByEmailAndSecret(User user){
        List<User> users = userRepository.findAll();

        for (User retrievedUser : users){
           if (retrievedUser.getEmail().equals(user.getEmail()) && retrievedUser.getSecret().equals(user.getSecret())){
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

}
