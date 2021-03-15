package com.noroff.lagalt.projectcollaborators.repository;

import com.noroff.lagalt.projectcollaborators.models.ProjectCollaborators;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectCollaboratorsRepository extends JpaRepository<ProjectCollaborators, Long> {
}
