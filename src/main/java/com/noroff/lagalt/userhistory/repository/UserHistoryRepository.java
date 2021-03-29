package com.noroff.lagalt.userhistory.repository;

import com.noroff.lagalt.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserHistoryRepository extends JpaRepository<User, Long> {
}
