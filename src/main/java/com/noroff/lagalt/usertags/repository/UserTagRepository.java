package com.noroff.lagalt.usertags.repository;

import com.noroff.lagalt.usertags.model.UserTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserTagRepository extends JpaRepository<UserTag, Long> {
    @Query("SELECT DISTINCT u.tag FROM UserTag u")
    List<String> findUniqueTags();
}
