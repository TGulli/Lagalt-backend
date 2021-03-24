package com.noroff.lagalt.projecttags.repository;

import com.noroff.lagalt.projecttags.model.ProjectTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProjectTagRepository extends JpaRepository<ProjectTag, Long> {
    @Query("SELECT DISTINCT p.tag FROM ProjectTag p")
    List<String> findUniqueTags();
}
