package com.noroff.lagalt.projectcollaborators.service;

import com.noroff.lagalt.exceptions.NoItemFoundException;
import com.noroff.lagalt.project.model.Project;
import com.noroff.lagalt.projectcollaborators.models.ProjectCollaborators;
import com.noroff.lagalt.projectcollaborators.repository.ProjectCollaboratorsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectCollaboratorsService {

    @Autowired
    ProjectCollaboratorsRepository projectCollaboratorsRepository;

    public ResponseEntity<ProjectCollaborators> create(ProjectCollaborators projectCollaborators){
        ProjectCollaborators newCollaborator = projectCollaboratorsRepository.save(projectCollaborators);
        HttpStatus status = HttpStatus.CREATED;
        return new ResponseEntity<>(newCollaborator, status);
    }

    public ResponseEntity<List<ProjectCollaborators>> getAll() {
        List<ProjectCollaborators> collaborators = projectCollaboratorsRepository.findAll();
        HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(collaborators, status);
    }

    public ResponseEntity<Optional<ProjectCollaborators>> getById(long id) {
        Optional<ProjectCollaborators> collaborators = projectCollaboratorsRepository.findById(id);
        HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(collaborators, status);
    }

    public ResponseEntity<ProjectCollaborators> updateStatus (long id, ProjectCollaborators collaborators){
        ProjectCollaborators updatedCollaborators = new ProjectCollaborators();
        HttpStatus status;
        if(id != collaborators.getId()){
            status = HttpStatus.BAD_REQUEST;
            return new ResponseEntity<>(updatedCollaborators, status);
        }
        updatedCollaborators = projectCollaboratorsRepository.save(collaborators);
        status = HttpStatus.OK;
        return new ResponseEntity<>(updatedCollaborators, status);
    }




}
