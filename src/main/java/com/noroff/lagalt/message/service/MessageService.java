package com.noroff.lagalt.message.service;

import com.noroff.lagalt.message.model.Message;
import com.noroff.lagalt.message.repository.MessageRepository;
import com.noroff.lagalt.user.model.User;
import com.noroff.lagalt.project.model.Project;
import com.noroff.lagalt.project.repository.ProjectRepository;
import com.noroff.lagalt.projectcollaborators.models.ProjectCollaborators;
import com.noroff.lagalt.projectcollaborators.models.Status;
import com.noroff.lagalt.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {

    @Autowired
    public MessageRepository messageRepository;

    @Autowired
    public ProjectRepository projectRepository;

    @Autowired
    public UserRepository userRepository;

    private final static int MAXMESSAGESIZE = 1000;

    public ResponseEntity<List<Message>> getAll (){
        return ResponseEntity.ok(messageRepository.findAll());
    }

    public ResponseEntity<Message> create (Message message){
        if (message == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Meldingsobjektet er null.");
        } else if (message.getProject() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Meldingsobjektet har ikke et tilhørende prosjekt.");
        } else if (message.getProject().getId() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Meldingsobjektet sitt prosjekt har ikke en satt id.");
        } else if (message.getUser() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Meldingsobjektet har ingen brukere.");
        } else if(message.getContent() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Meldingsinnhold er ikke satt.");
        }  else if (message.getUser().getId() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Meldingsobjektet sin bruker har ikke en satt id.");
        } else if (message.getContent().length() > MAXMESSAGESIZE){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Meldingen kan ikke være lengre enn " + MAXMESSAGESIZE + " tegn.");
        }
        Long projectId = message.getProject().getId();
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fant ikke prosjektet med id: " + projectId + " i systemet."));
        if (project.getOwners() == null || project.getOwners().size() < 1){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prosjekteier(e) er ikke satt.");
        }
        List<User> owners = project.getOwners();
        List<ProjectCollaborators> collaborators = project.getCollaborators();
        Long userId = message.getUser().getId();
        if (!userRepository.existsById(userId)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ingen bruker eksisterer med id: " + userId);
        }

        for(User owner : owners){
            if(owner.getId().equals(userId)){
                return new ResponseEntity<>(messageRepository.save(message), HttpStatus.CREATED);
            }
        }
        for(ProjectCollaborators collaborator : collaborators){
            if(collaborator.getStatus() != null && collaborator.getStatus().equals(Status.APPROVED)){
                if(collaborator.getUser().getId() != null && collaborator.getUser().getId().equals(userId)){
                    return new ResponseEntity<>(messageRepository.save(message), HttpStatus.CREATED);
                }
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kunne ikke sende meldingen.");
    }

    public ResponseEntity<Message> editMessage(Long id, Message message, Long userId){
        if (message == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Meldingsobjektet er null.");
        } else if (message.getProject() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Meldingsobjektet har ikke et tilhørende prosjekt.");
        } else if (message.getProject().getId() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Meldingsobjektet sitt prosjekt har ikke en satt id.");
        } else if (message.getUser() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Meldingsobjektet har ingen brukere.");
        } else if (message.getId() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Meldingsobjektet har ingen id.");
        } else if (message.getUser().getId() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Meldingsobjektet sin bruker har ikke en satt id.");
        } else if(!id.equals(message.getId())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID samsvarer ikke med det som er i meldingsobjektet.");
        } else if(!userId.equals(message.getUser().getId())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User iD samsvarer ikke med det som er i meldingsobjektet.");
        } else if(message.getContent() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Meldingsinnhold er ikke satt.");
        } else if (message.getContent().length() > MAXMESSAGESIZE){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Meldingen kan ikke være lengre enn " + MAXMESSAGESIZE + " tegn.");
        } else if (!messageRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ingen prosjekt med id: " + id + " eksisterer.");
        }

        return new ResponseEntity<>(messageRepository.save(message), HttpStatus.CREATED);
    }

    public ResponseEntity<List<Message>> getAllByProject(Long projectId, Long userId) {
        List<Message> allMessages = messageRepository.findAll();

        Project project = projectRepository.findById(projectId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Det eksister ingen prosjekter med prosjekt id: " + projectId));

        if (!userRepository.existsById(userId)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Det eksisterer ingen prosjekster med bruker id: " + userId);
        }

        List<Message> projectMessages = allMessages.stream().filter(message ->
                (message.getProject().getId().equals(projectId))).collect(Collectors.toList());

        List<User> owners = project.getOwners();
        List<ProjectCollaborators> collaborators = project.getCollaborators();

        for(User owner : owners){
            if(owner.getId().equals(userId)){
                return ResponseEntity.ok(projectMessages);
            }
        }
        for(ProjectCollaborators collaborator : collaborators){
            if(collaborator.getStatus().equals(Status.APPROVED)){
                if(collaborator.getUser().getId().equals(userId)){
                    return ResponseEntity.ok(projectMessages);
                }
            }
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }
}
