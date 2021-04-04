package com.noroff.lagalt.projecttags.repository;

import com.noroff.lagalt.projecttags.model.ProjectTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectTagRepository extends JpaRepository<ProjectTag, Long> {

    /**
     * Retrieves the unique tags from project which
     * is part of populating the suggestion box
     */

    @Query("SELECT DISTINCT p.tag FROM ProjectTag p")
    List<String> findUniqueTags();


    /**
     * Retrieves all related project tags to a project
     * for the display/main page
     */

    @Query("SELECT p FROM ProjectTag p WHERE p.project.id = ?1")
    List<ProjectTag> findProjectTagsByProjectId(Long id);
}
