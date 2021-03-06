package com.noroff.lagalt.projectcollaborators.service;

import com.noroff.lagalt.projectcollaborators.models.Status;
import com.noroff.lagalt.security.JwtTokenUtil;
import com.noroff.lagalt.user.model.User;
import com.noroff.lagalt.project.model.Project;
import com.noroff.lagalt.project.repository.ProjectRepository;
import com.noroff.lagalt.projectcollaborators.models.ProjectCollaborators;
import com.noroff.lagalt.projectcollaborators.repository.ProjectCollaboratorsRepository;
import com.noroff.lagalt.user.repository.UserRepository;
import com.noroff.lagalt.userhistory.model.ActionType;
import com.noroff.lagalt.userhistory.model.UserHistory;
import com.noroff.lagalt.userhistory.repository.UserHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
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

    @Autowired
    private UserHistoryRepository userHistoryRepository;

    // Creates a new project collaborator
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

        // Check if the user and the project already exists
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "En bruker med  id: " + userId + " eksisterer ikke.");
        } else if (!projectRepository.existsById(projectId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Et prosjekt med id: " + projectId + " eksisterer ikke.");
        }

        // Checks if the project collaborator already exists
        for (ProjectCollaborators pc : projectCollaboratorsRepository.findAll()) {
            if(pc.getUser().getId().equals(projectCollaborator.getUser().getId()) &&
                    pc.getProject().getId().equals(projectCollaborator.getProject().getId())){
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT, "The projectcallaborator already exists in the project.");
            }
        }

        Project project = projectRepository.findById(projectId).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.BAD_REQUEST, "Et prosjekt med id: " + projectId + " eksisterer ikke."));


        if (project.getOwner().getId().equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Can't apply to a project you already own");
        }

        List<ProjectCollaborators> collaboratorsList = project.getCollaborators();

        if (collaboratorsList != null) {
            for (ProjectCollaborators projectCollaborators : collaboratorsList) {
                User user = projectCollaborators.getUser();
                if (user.getId().equals(userId)) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "Can't apply to same project twice or something");
                }
            }
        }

        // Saves to user history
        LocalDate localDate = LocalDate.now();
        UserHistory uh = new UserHistory();
        uh.setUser(projectCollaborator.getUser());
        uh.setProject_id(projectCollaborator.getProject().getId());
        uh.setActionType(ActionType.APPLIED);
        uh.setTimestamp(localDate.toString());
        userHistoryRepository.save(uh);

        ProjectCollaborators newCollaborator = projectCollaboratorsRepository.save(projectCollaborator);
        return new ResponseEntity<>(newCollaborator, HttpStatus.CREATED);
    }

    // Returns all the project collaborators
    public ResponseEntity<List<ProjectCollaborators>> getAll() {
        List<ProjectCollaborators> collaborators = projectCollaboratorsRepository.findAll();
        HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(collaborators, status);
    }

    // Returns all the project collaborators for a given project
    public ResponseEntity<List<ProjectCollaborators>> getAllByProjectId(Long id, String authHeader) {
        String username = jwtTokenUtil.getUsernameFromToken(authHeader.substring(7));
        Optional<User> user = userRepository.findByUsername(username);

        Project p = projectRepository.findById(id).orElseThrow( () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Illegal user tried to read"));

        if (user.isPresent()){
            if (!p.getOwner().getId().equals(user.get().getId())){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Illegal user tried to read");
            }
            List<ProjectCollaborators> collaborators = projectCollaboratorsRepository.findAllByProject_Id(id).stream().filter(x -> x.getStatus().equals(Status.PENDING)).collect(Collectors.toList());
            HttpStatus status = HttpStatus.OK;
            return new ResponseEntity<>(collaborators, status);

        }
        throw new ResponseStatusException(HttpStatus.CONFLICT, "Token user is not a legal user");
    }

    // Gets a project collaborator by collaborator id
    public ResponseEntity<ProjectCollaborators> getById(Long id) {
        Optional<ProjectCollaborators> collaborators = projectCollaboratorsRepository.findById(id);
        if (collaborators.isPresent()){
            return ResponseEntity.ok(collaborators.get());
        }
        throw new ResponseStatusException(
                HttpStatus.CONFLICT, "Fant ikke et projektmedlem med id: " + id);
    }

    // Updates a project collaborator
    public ResponseEntity<ProjectCollaborators> update (Long id, ProjectCollaborators collaborator, String authHeader){
        String username = jwtTokenUtil.getUsernameFromToken(authHeader.substring(7));
        Optional<User> requestUser = userRepository.findByUsername(username);

        // Check if legal user and the required data is set
        if (requestUser.isEmpty()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Token user is not a legal user");
        } else if (collaborator == null){
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

            // If user is accepted, save history as user collaborates
            if (collaborator.getStatus().equals(Status.APPROVED)) {
                LocalDate localDate = LocalDate.now();
                UserHistory uh = new UserHistory();
                uh.setUser(collaborator.getUser());
                uh.setProject_id(collaborator.getProject().getId());
                uh.setActionType(ActionType.COLLABORATED);
                uh.setTimestamp(localDate.toString());
                userHistoryRepository.save(uh);
            }

            ProjectCollaborators updatedCollaborators = projectCollaboratorsRepository.save(collaborator);
            return new ResponseEntity<>(updatedCollaborators, HttpStatus.OK);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kan ikke oppdatere medlemmer, da bruker ikke er eier av prosjektet.");
    }
}
