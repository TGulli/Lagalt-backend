package com.noroff.lagalt.projectcollaborators.repository;

import com.noroff.lagalt.projectcollaborators.models.ProjectCollaborators;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectCollaboratorsRepository extends JpaRepository<ProjectCollaborators, Long> {
    List<ProjectCollaborators> findAllByProject_Id(Long id);
}
