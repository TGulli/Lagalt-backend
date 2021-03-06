package com.noroff.lagalt.userhistory.repository;

import com.noroff.lagalt.project.model.PartialProject;
import com.noroff.lagalt.user.model.User;
import com.noroff.lagalt.userhistory.UserHistoryDTO;
import com.noroff.lagalt.userhistory.model.UserHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserHistoryRepository extends JpaRepository<UserHistory, Long> {

    /**
     * Gets the project ID and count (which is how many times a project has been seen) in an ascending order
     * Part of how the system presents new content to a user
     */

    @Query("SELECT new com.noroff.lagalt.userhistory.UserHistoryDTO(u.project_id, COUNT(u)) FROM UserHistory u WHERE u.user.id = ?1 GROUP BY u.project_id ORDER BY COUNT(u) ASC")
    List<UserHistoryDTO> getRecordCountForId(long id);
}
