package com.noroff.lagalt.project.service;

import com.noroff.lagalt.exceptions.NoItemFoundException;
import com.noroff.lagalt.model.User;
import com.noroff.lagalt.project.model.Project;
import com.noroff.lagalt.project.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    public ResponseEntity<Project> create (Project project){
        Project createdProject = projectRepository.save(project);
        HttpStatus status = HttpStatus.CREATED;
        return new ResponseEntity<>(createdProject, status);
    }

    public ResponseEntity<List<Project>> getAll (){
        List<Project> projects = projectRepository.findAll();
        HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(projects, status);
    }

    public ResponseEntity<Project> getById(long id) throws NoItemFoundException {
        Project fetchedProject = projectRepository.findById(id).orElseThrow(() -> new NoItemFoundException("No project by id: " + id));
        return ResponseEntity.ok(fetchedProject);
    }

    public ResponseEntity<Page<Project>> showDisplayProjects(int page) {
        Pageable p = PageRequest.of(page, 3);
        Page<Project> givenPage = projectRepository.findAll(p);
        return ResponseEntity.ok(givenPage);
    }



}
