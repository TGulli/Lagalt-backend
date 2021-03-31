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

    public ResponseEntity<List<Message>> getAll (){
        List<Message> messages = messageRepository.findAll();
        HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(messages, status);
    }



    public ResponseEntity<Message> create (Message message){
        Long projectId = message.getProject().getId();
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No project"));
        User owner = project.getOwner();
        List<ProjectCollaborators> collaborators = project.getCollaborators();
        Long userId = message.getUser().getId();
        User user = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No user"));


        if(owner.getId().equals(userId)){
            Message createdMessage = messageRepository.save(message);
            HttpStatus status = HttpStatus.CREATED;
            return new ResponseEntity<>(createdMessage, status);
        }

        for(ProjectCollaborators collaborator : collaborators){
            if(collaborator.getStatus().equals(Status.APPROVED)){
                if(collaborator.getUser().getId().equals(userId)){
                    Message createdMessage = messageRepository.save(message);
                    HttpStatus status = HttpStatus.CREATED;
                    return new ResponseEntity<>(createdMessage, status);
                }
            }
        }

        HttpStatus status = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(null, status);
    }

    public ResponseEntity<Message> editMessage(Long id, Message message, Long userId){
        HttpStatus status;
        if(!id.equals(message.getId())){
            status = HttpStatus.BAD_REQUEST;
            return  new ResponseEntity<>(null, status);
        }
        Message checkMessage = messageRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No project"));

        if(!userId.equals(message.getUser().getId())){
            status = HttpStatus.BAD_REQUEST;
            return new ResponseEntity<>(null, status);
        }
        messageRepository.save(message);
        status = HttpStatus.CREATED;
        return new ResponseEntity<>(message, status);
    }

    public ResponseEntity<List<Message>> getAllByProject(Long projectId, Long userId) {
        List<Message> allMessages = messageRepository.findAll();

        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No project"));

        List<Message> projectMessages = allMessages.stream().filter(message ->
                (message.getProject().getId().equals(projectId))).collect(Collectors.toList());


        userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No user"));

        User owner = project.getOwner();
        List<ProjectCollaborators> collaborators = project.getCollaborators();


        if(owner.getId().equals(userId)){
            return ResponseEntity.ok(projectMessages);
        }

        for(ProjectCollaborators collaborator : collaborators){
            if(collaborator.getStatus().equals(Status.APPROVED)){
                if(collaborator.getUser().getId().equals(userId)){
                    return ResponseEntity.ok(projectMessages);
                }
            }
        }

        System.out.println("Length of projetmessages: " + projectMessages.size());
        System.out.println("Responseentity: " + new ResponseEntity<>(projectMessages, HttpStatus.OK));
        return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);

    }

}
