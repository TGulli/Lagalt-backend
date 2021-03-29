package com.noroff.lagalt.project.repository;

import com.noroff.lagalt.project.model.PartialProject;
import com.noroff.lagalt.project.model.Project;
import com.noroff.lagalt.user.model.PartialUser;
import com.noroff.lagalt.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Optional<Project> findByName(String name);
    Boolean existsByName(String name);


    //String name, String description, Progress progress List<ProjectTag> projectTags
    /*
    @Query("SELECT new com.noroff.lagalt.project.model.PartialProject(p.name, p.description, p.progress, p.projectTags) " +
            "from Project p where p.id = ?1 AND p.projectTags = (SELECT pt.tag FROM ProjectTag pt WHERE pt.id = ?1)"
    PartialProject findPartialById(long id);

     */
}
