package com.noroff.lagalt.user.repository;

import com.noroff.lagalt.user.model.PartialProjection;
import com.noroff.lagalt.user.model.PartialUser;
import com.noroff.lagalt.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameOrEmail(String username, String email);
    Optional<User> findByEmail(String email);

    //Partial
    //@Query("SELECT u.username, u.name  FROM User u WHERE u.id = ?1")
    //PartialProjection findPartialById(long id);

    //@Query("SELECT u.username, u.name, u.bio  FROM User u WHERE u.id = ?1")
    //PartialUser findPartialById(long id);
}
