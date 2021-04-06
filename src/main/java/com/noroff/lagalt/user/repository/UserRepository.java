package com.noroff.lagalt.user.repository;

import com.noroff.lagalt.user.model.PartialUser;
import com.noroff.lagalt.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * Repository for the users table
 */

public interface UserRepository extends JpaRepository<User, Long>, CrudRepository<User, Long> {

    // Specific user searches
    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameOrEmail(String username, String email);
    Optional<User> findByEmail(String email);

    // Methods for assuring no duplicate users are being created
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);

    // Retrieves part of the user object as PartialUser for hidden profile or non-authorized view
    @Query("select new com.noroff.lagalt.user.model.PartialUser(u.username, u.name, u.bio) from User u where u.id = ?1")
    PartialUser findPartialById(long id);
}
