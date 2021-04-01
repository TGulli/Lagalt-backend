package com.noroff.lagalt.projectcollaborators.service;

import com.noroff.lagalt.projectcollaborators.models.Status;
import com.noroff.lagalt.security.JwtTokenUtil;
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
import java.util.stream.Collectors;

@Service
public class ProjectCollaboratorsService {

    @Autowired
    private ProjectCollaboratorsRepository projectCollaboratorsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

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


            for (ProjectCollaborators pc : projectCollaboratorsRepository.findAll()) {
                if(pc.getUser().getId().equals(projectCollaborator.getUser().getId()) &&
                        pc.getProject().getId().equals(projectCollaborator.getProject().getId())){
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT, "The projectcallaborator already exists in the project.");
                }
            }

            Project project = projectRepository.findById(projectId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Et prosjekt med id: " + projectId + " eksisterer ikke."));

            User owner = project.getOwner();
            List<ProjectCollaborators> collaboratorsList = project.getCollaborators();


            if (owner.getId().equals(userId)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "CAn't apply to a project you alreay own");
            }

            if (collaboratorsList != null) {
                for (ProjectCollaborators projectCollaborators : collaboratorsList) {
                    User user = projectCollaborators.getUser();
                    if (user.getId().equals(userId)) {
                        throw new ResponseStatusException(
                                HttpStatus.BAD_REQUEST, "Can't apply to same project twice or something");
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

    public ResponseEntity<List<ProjectCollaborators>> getAllByProjectId(Long id, String authHeader) {

        String username = jwtTokenUtil.getUsernameFromToken(authHeader.substring(7));

        Optional<User> user = userRepository.findByUsername(username);

        if (user.isPresent()){
            if (!user.get().getId().equals(id)){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Illegal user tried to read");
            }
            List<ProjectCollaborators> collaborators = projectCollaboratorsRepository.findAllByProject_Id(id).stream().filter(x -> x.getStatus().equals(Status.PENDING)).collect(Collectors.toList());
            HttpStatus status = HttpStatus.OK;
            return new ResponseEntity<>(collaborators, status);

        }
        throw new ResponseStatusException(HttpStatus.CONFLICT, "Token user is not a legal user");
    }

    public ResponseEntity<ProjectCollaborators> getById(Long id) {
        Optional<ProjectCollaborators> collaborators = projectCollaboratorsRepository.findById(id);
        if (collaborators.isPresent()){
            return ResponseEntity.ok(collaborators.get());
        }
        throw new ResponseStatusException(
                HttpStatus.CONFLICT, "Fant ikke et projektmedlem med id: " + id);
    }

    public ResponseEntity<ProjectCollaborators> update (Long id, ProjectCollaborators collaborator, String authHeader){


        String username = jwtTokenUtil.getUsernameFromToken(authHeader.substring(7));
        Optional<User> requestUser = userRepository.findByUsername(username);

        if (requestUser.isEmpty()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Token user is not a legal user");
        }

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
        User owner = existingProject.getOwner();

        if (owner.getId().equals(requestUser.get().getId())){
            ProjectCollaborators updatedCollaborators = projectCollaboratorsRepository.save(collaborator);
            return new ResponseEntity<>(updatedCollaborators, HttpStatus.OK);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kan ikke oppdatere medlemmer, da bruker ikke er eier av prosjektet.");

    }


}
