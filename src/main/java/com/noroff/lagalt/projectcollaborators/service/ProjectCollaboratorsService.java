package com.noroff.lagalt.projectcollaborators.service;

import com.noroff.lagalt.exceptions.NoItemFoundException;
import com.noroff.lagalt.user.model.User;
import com.noroff.lagalt.project.model.Project;
import com.noroff.lagalt.project.repository.ProjectRepository;
import com.noroff.lagalt.projectcollaborators.models.ProjectCollaborators;
import com.noroff.lagalt.projectcollaborators.repository.ProjectCollaboratorsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectCollaboratorsService {

    @Autowired
    ProjectCollaboratorsRepository projectCollaboratorsRepository;
    @Autowired
    private ProjectRepository projectRepository;

    //Vi må hente ut prosjektet sin liste med owners og collaborators, og sjekke om brukeren finnes i de listene.
    public ResponseEntity<ProjectCollaborators> create(ProjectCollaborators collaborators) throws NoItemFoundException{
        HttpStatus status;
        Long userId = collaborators.getUser().getId();
        Long projectId = collaborators.getProject().getId();
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new NoItemFoundException("No Project by id: " + projectId));

        List<User> owners = project.getOwners();
        List<ProjectCollaborators> collaboratorsList = project.getCollaborators();

        for (User owner : owners){
            if (owner.getId() == userId){
                status = HttpStatus.BAD_REQUEST;
                return new ResponseEntity<>(null, status);
            }
        }
        if (collaboratorsList != null){
            for (ProjectCollaborators projectCollaborators : collaboratorsList){
                User user = projectCollaborators.getUser();
                if (user.getId() == userId){
                    status = HttpStatus.BAD_REQUEST;
                    return new ResponseEntity<>(null, status);
                }
            }
        }
        ProjectCollaborators newCollaborator = projectCollaboratorsRepository.save(collaborators);
        status = HttpStatus.CREATED;
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

    public ResponseEntity<ProjectCollaborators> updateStatus (long id, ProjectCollaborators collaborators, Long userId) throws NoItemFoundException{
        ProjectCollaborators updatedCollaborators = new ProjectCollaborators();
        HttpStatus status;

        if(id != collaborators.getId()){
            status = HttpStatus.BAD_REQUEST;
            return new ResponseEntity<>(updatedCollaborators, status);
        }

        Long projectId = collaborators.getProject().getId();
        Project existingProject = projectRepository.findById(projectId).orElseThrow(() -> new NoItemFoundException("No Project by id: " + id));
        List<User> owners = existingProject.getOwners();
        for (User owner : owners){
            if (owner.getId() == userId){
                updatedCollaborators = projectCollaboratorsRepository.save(collaborators);
                status = HttpStatus.OK;
                return new ResponseEntity<>(updatedCollaborators, status);
            }
        }
        status = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(null, status);



    }




}
