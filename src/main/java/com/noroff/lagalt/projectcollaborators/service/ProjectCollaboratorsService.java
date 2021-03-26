package com.noroff.lagalt.projectcollaborators.service;

import com.noroff.lagalt.project.model.Project;
import com.noroff.lagalt.project.repository.ProjectRepository;
import com.noroff.lagalt.projectcollaborators.models.ProjectCollaborators;
import com.noroff.lagalt.projectcollaborators.repository.ProjectCollaboratorsRepository;
import com.noroff.lagalt.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectCollaboratorsService {

    @Autowired
    ProjectCollaboratorsRepository projectCollaboratorsRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProjectRepository projectRepository;

    public ResponseEntity<ProjectCollaborators> create(ProjectCollaborators projectCollaborator){
        try{
            if (projectCollaboratorsRepository.existsById(projectCollaborator.getId())){
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "The projectCollaborator table with id: " + projectCollaborator.getId() + " already exists.");
            } else if (!userRepository.existsById(projectCollaborator.getUser().getId())){
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "A user with id: " + projectCollaborator.getUser().getId() + " does not exist in the database.");
            } else if (!projectRepository.existsById(projectCollaborator.getProject().getId())){
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "A project with id: " + projectCollaborator.getUser().getId() + " does not exist in the database.");
            }

            for (ProjectCollaborators pc : projectCollaboratorsRepository.findAll()) {
                if(pc.getUser().getId() == projectCollaborator.getUser().getId() &&
                        pc.getProject().getId() == projectCollaborator.getProject().getId()){
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT, "The projectcallaborator already exists in the project.");
                }
            }
            ProjectCollaborators newCollaborator = projectCollaboratorsRepository.save(projectCollaborator);
            HttpStatus status = HttpStatus.CREATED;
            return new ResponseEntity<>(newCollaborator, status);
        } catch (NullPointerException e){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Some data in projectcollaborator when creating is null.");
        }

    }

    public ResponseEntity<List<ProjectCollaborators>> getAll() {
        List<ProjectCollaborators> collaborators = projectCollaboratorsRepository.findAll();
        HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(collaborators, status);
    }

    public ResponseEntity<ProjectCollaborators> getById(long id) {
        Optional<ProjectCollaborators> collaborators = projectCollaboratorsRepository.findById(id);
        if (collaborators.isPresent()){
            return ResponseEntity.ok(collaborators.get());
        }
        throw new ResponseStatusException(
                HttpStatus.CONFLICT, "No projectcollaborator with id: " + id);
    }

    public ResponseEntity<ProjectCollaborators> update(long id, ProjectCollaborators collaborator){
        if(id != collaborator.getId()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Id does not match the id in projectcollaborator.");
        } else if (!userRepository.existsById(collaborator.getUser().getId())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A user with id: " + collaborator.getUser().getId() + " does not exist in the database.");
        } else if (!projectRepository.existsById(collaborator.getProject().getId())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A project with id: " + collaborator.getUser().getId() + " does not exist in the database.");
        }

        return ResponseEntity.ok(projectCollaboratorsRepository.save(collaborator));
    }
}
