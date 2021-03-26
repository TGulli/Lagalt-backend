package com.noroff.lagalt.projectcollaborators.service;

import com.noroff.lagalt.user.model.User;
import com.noroff.lagalt.project.model.Project;
import com.noroff.lagalt.project.repository.ProjectRepository;
import com.noroff.lagalt.projectcollaborators.models.ProjectCollaborators;
import com.noroff.lagalt.projectcollaborators.repository.ProjectCollaboratorsRepository;
import com.noroff.lagalt.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        HttpStatus status;
        Long userId = projectCollaborator.getUser().getId();
        Long projectId = projectCollaborator.getProject().getId();
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,"No Project by id: " ));

        List<User> owners = project.getOwners();
        List<ProjectCollaborators> collaboratorsList = project.getCollaborators();

        for (User owner : owners){
            if (owner.getId().equals(userId)){
                status = HttpStatus.BAD_REQUEST;
                return new ResponseEntity<>(null, status);
            }
        }
        if (collaboratorsList != null){
            for (ProjectCollaborators projectCollaborators : collaboratorsList){
                User user = projectCollaborators.getUser();
                if (user.getId().equals(userId)){
                    status = HttpStatus.BAD_REQUEST;
                    return new ResponseEntity<>(null, status);
                }
            }
        }
        ProjectCollaborators newCollaborator = projectCollaboratorsRepository.save(projectCollaborator);
        status = HttpStatus.CREATED;
        return new ResponseEntity<>(newCollaborator, status);

    }

    public ResponseEntity<List<ProjectCollaborators>> getAll() {
        List<ProjectCollaborators> collaborators = projectCollaboratorsRepository.findAll();
        HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(collaborators, status);
    }

    public ResponseEntity<ProjectCollaborators> getById(Long id) {
        Optional<ProjectCollaborators> collaborators = projectCollaboratorsRepository.findById(id);
        if (collaborators.isPresent()){
            return ResponseEntity.ok(collaborators.get());
        }
        throw new ResponseStatusException(
                HttpStatus.CONFLICT, "No projectcollaborator with id: " + id);
    }

    public ResponseEntity<ProjectCollaborators> update (Long id, ProjectCollaborators collaborator, Long userId){

        if(!id.equals(collaborator.getId())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Id does not match the id in projectcollaborator.");
        } else if (!userRepository.existsById(collaborator.getUser().getId())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A user with id: " + collaborator.getUser().getId() + " does not exist in the database.");
        } else if (!projectRepository.existsById(collaborator.getProject().getId())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A project with id: " + collaborator.getUser().getId() + " does not exist in the database.");
        }

        Long projectId = collaborator.getProject().getId();
        Project existingProject = projectRepository.findById(projectId).get();
        List<User> owners = existingProject.getOwners();
        for (User owner : owners){
            if (owner.getId().equals(userId)){
                ProjectCollaborators updatedCollaborators = projectCollaboratorsRepository.save(collaborator);
                return new ResponseEntity<>(updatedCollaborators, HttpStatus.OK);
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't update a project you don't own");

    }


}
