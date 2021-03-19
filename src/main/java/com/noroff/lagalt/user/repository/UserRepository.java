package com.noroff.lagalt.user.repository;

import com.noroff.lagalt.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
