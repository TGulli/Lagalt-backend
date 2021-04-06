package com.noroff.lagalt.usertags.repository;

import com.noroff.lagalt.usertags.model.UserTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * A repository containing all tags created by users
 */

public interface UserTagRepository extends JpaRepository<UserTag, Long> {

    /**
     * Gets the unique user tags.
     * The other part of populating the suggestion box
     */

    @Query("SELECT DISTINCT u.tag FROM UserTag u")
    List<String> findUniqueTags();
}
