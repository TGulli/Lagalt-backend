package com.noroff.lagalt.project.repository;

import com.noroff.lagalt.project.model.PartialProject;
import com.noroff.lagalt.project.model.Project;
import com.noroff.lagalt.user.model.PartialUser;
import com.noroff.lagalt.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;


import java.util.List;
import java.util.Optional;


public interface ProjectRepository extends JpaRepository<Project, Long>, PagingAndSortingRepository<Project, Long> {
    Optional<Project> findByName(String name);
    Page<Project> findByNameContainingIgnoreCase(String name, Pageable var1);
    Page<Project> findByCategoryIgnoreCase(String name, Pageable var1);
    Page<Project> findByNameContainingIgnoreCaseAndCategoryIgnoreCase(String name, String category, Pageable var1);
    Boolean existsByName(String name);


    @Query("SELECT new com.noroff.lagalt.project.model.PartialProject(p.name, p.description, p.category,p.progress, p.owner.username) FROM Project p WHERE p.id = ?1")
    PartialProject getPublicProjectById(long id);
}
