package com.noroff.lagalt.user.service;

import com.noroff.lagalt.exceptions.NoItemFoundException;
import com.noroff.lagalt.exceptions.UserExistException;
import com.noroff.lagalt.exceptions.UserNullException;
import com.noroff.lagalt.user.model.User;
import com.noroff.lagalt.project.model.Project;
import com.noroff.lagalt.project.repository.ProjectRepository;
import com.noroff.lagalt.user.repository.UserRepository;
import com.noroff.lagalt.usertags.model.UserTag;
import com.noroff.lagalt.usertags.repository.UserTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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


    public ResponseEntity<?> create(User user) {
        if (user == null || user.getUsername() == null || user.getSecret() == null){
//        if (user == null || user.getEmail() == null || user.getUsername() == null || user.getName() == null || user.getSecret() == null){
            return UserNullException.catchException("User, user.email, user.name, user.secret or user.username is null.");
        }
       Optional<User> existingUser = userRepository.findByUsernameOrEmail(user.getUsername(), user.getEmail());

        if (existingUser.isPresent()){
            return UserExistException.catchException("User already exists");
        }
        User x = userRepository.save(user);
        return ResponseEntity.ok(x);
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }


    public ResponseEntity<?> getById(long id) {
        Optional<User> fetchedUser = userRepository.findById(id);

        if (fetchedUser.isEmpty()){
            return NoItemFoundException.catchException("No user found by id: " + id);
        }
        //TODO:
        // Fetch by anyone but project owner. Find way to differentiate between project owner and everyone else
        // 3 state: ikkje logga, logga inn, owner
        if (fetchedUser.get().isHidden()){
            User hiddenUser = new User();
            hiddenUser.setUsername(fetchedUser.get().getUsername());
            return ResponseEntity.ok(hiddenUser);
        }

        return ResponseEntity.ok(fetchedUser.get());
    }

    public HttpStatus deleteUser(long id) {
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
    public ResponseEntity<?> editUser(User user, long id){
        Optional<User> currentUserState = userRepository.findById(id);

        if (currentUserState.isEmpty()){
            return NoItemFoundException.catchException("No user found by id: " + id);
        }

        if (!user.getName().equals("")) currentUserState.get().setName(user.getName());
        if (user.getDescription() != null && !user.getDescription().equals("")) currentUserState.get().setDescription(user.getDescription());

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
