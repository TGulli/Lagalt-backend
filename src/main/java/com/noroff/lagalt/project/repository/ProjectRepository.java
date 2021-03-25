package com.noroff.lagalt.project.repository;

import com.noroff.lagalt.project.model.Project;
import com.noroff.lagalt.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Optional<Project> findByName(String name);
    Boolean existsByName(String name);
}
