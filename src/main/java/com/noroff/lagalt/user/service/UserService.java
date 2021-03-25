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


    public ResponseEntity<User> create(User user) throws UserExistException, UserNullException {
        if (user == null || user.getUsername() == null || user.getEmail() == null){
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

    public HttpStatus deleteUser(long id) throws NoItemFoundException {
        User fetchedUser = userRepository.findById(id).orElseThrow(() -> new NoItemFoundException("No user by id: " + id));

        List<Project> projects = projectRepository.findAll();
        for (Project p : projects) {
            p.getOwners().remove(fetchedUser);
        }
        userRepository.delete(fetchedUser);
        HttpStatus status = HttpStatus.OK;
        return status;
    }

    // Edit -> Add skills with mEeEeEeeEeEee.
    public ResponseEntity<User> editUser(User user, long id) throws NoItemFoundException {

        User currentUserState = userRepository.findById(id).orElseThrow(() -> new NoItemFoundException("No user by id"));

        if (!user.getName().equals("")) currentUserState.setName(user.getName());
        if (!user.getDescription().equals("")) currentUserState.setDescription(user.getDescription());

        //Create the tags!
        if (user.getUserTags() != null) {

            List<String> currentTags = new ArrayList<>();
            for (UserTag t : currentUserState.getUserTags()) {
                currentTags.add(t.getTag());
            }
            for (UserTag tag : user.getUserTags()) {
                String userTag = tag.getTag();
                if (!currentTags.contains(userTag)) {
                    tag.setUser(currentUserState);
                    userTagRepository.save(tag);
                }

            }
        }

        User editUser = userRepository.save(currentUserState);
        return ResponseEntity.ok(editUser);
    }
}
