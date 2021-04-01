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


    @Query("SELECT new com.noroff.lagalt.project.model.PartialProject(p.name, p.description, p.progress, p.category, p.owner.username) FROM Project p WHERE p.id = ?1")
    PartialProject getPublicProjectById(long id);


}
