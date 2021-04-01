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
        if (projectCollaborator == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Objektet til requestobjektet er ikke satt");
        } else if (projectCollaborator.getUser() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bruker er ikke satt i objektet projectCollaborator.");
        } else if (projectCollaborator.getUser().getId() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bruker id er ikke satt i objektet projectCollaborator.");
        } else if (projectCollaborator.getProject() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prosjekt er ikke satt i objektet projectCollaborator.");
        } else if (projectCollaborator.getProject().getId() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prosjekt id er ikke satt i objektet projectCollaborator.");
        }

        Long userId = projectCollaborator.getUser().getId();
        Long projectId = projectCollaborator.getProject().getId();

        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "En bruker med  id: " + userId + " eksisterer ikke.");
        } else if (!projectRepository.existsById(projectId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Et prosjekt med id: " + projectId + " eksisterer ikke.");
        }

        // Duplikat?
//        for (ProjectCollaborators pc : projectCollaboratorsRepository.findAll()) {
//            if(pc.getUser().getId().equals(projectCollaborator.getUser().getId()) &&
//                    pc.getProject().getId().equals(projectCollaborator.getProject().getId())){
//                throw new ResponseStatusException(
//                        HttpStatus.BAD_REQUEST, "Medlemmet er allerede lagt til i prosjektet.");
//            }
//        }

        Project project = projectRepository.findById(projectId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Et prosjekt med id: " + projectId + " eksisterer ikke."));

        List<User> owners = project.getOwners();
        List<ProjectCollaborators> collaboratorsList = project.getCollaborators();

        for (User owner : owners) {
            if (owner.getId().equals(userId)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Kan ikke være medlem itillegg til eier av et prosjekt.");
            }
        }
        if (collaboratorsList != null) {
            for (ProjectCollaborators projectCollaborators : collaboratorsList) {
                User user = projectCollaborators.getUser();
                if (user.getId().equals(userId)) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "Medlemmet er allerede lagt til i prosjektet");
                }
            }
        }


        ProjectCollaborators newCollaborator = projectCollaboratorsRepository.save(projectCollaborator);
        return new ResponseEntity<>(newCollaborator, HttpStatus.CREATED);
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
                HttpStatus.CONFLICT, "Fant ikke et projektmedlem med id: " + id);
    }

    public ResponseEntity<ProjectCollaborators> update (Long id, ProjectCollaborators collaborator, Long userId){

        if (collaborator == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Objektet til requestobjektet er ikke satt");
        } else if (collaborator.getId() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Medlem er ikke satt i objektet projectCollaborator.");
        } else if (collaborator.getProject() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prosjekt er ikke satt i objektet projectCollaborator.");
        } else if (collaborator.getProject().getId() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prosjekt id er ikke satt i objektet projectCollaborator.");
        } else if (!collaborator.getId().equals(id)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Medlem id samsvarer ikke med id i pathen.");
        }

        Long projectId = collaborator.getProject().getId();
        Project existingProject = projectRepository.findById(projectId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prosjekt med id: " + projectId + " eksisterer ikke."));

        for (User owner : existingProject.getOwners()){
            if (owner.getId().equals(userId)){
                return new ResponseEntity<>(projectCollaboratorsRepository.save(collaborator), HttpStatus.OK);
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kan ikke oppdatere medlemmer, da bruker ikke er eier av prosjektet.");

    }


}
