package com.noroff.lagalt.project.repository;

import com.noroff.lagalt.project.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
