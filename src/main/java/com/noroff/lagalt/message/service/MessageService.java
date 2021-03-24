package com.noroff.lagalt.message.service;

import com.noroff.lagalt.exceptions.NoItemFoundException;
import com.noroff.lagalt.message.model.Message;
import com.noroff.lagalt.message.repository.MessageRepository;
import com.noroff.lagalt.model.User;
import com.noroff.lagalt.project.model.Project;
import com.noroff.lagalt.project.repository.ProjectRepository;
import com.noroff.lagalt.projectcollaborators.models.ProjectCollaborators;
import com.noroff.lagalt.projectcollaborators.models.Status;
import com.noroff.lagalt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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



    public ResponseEntity<Message> create (Message message) throws NoItemFoundException{
        Long projectId = message.getProject().getId();
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new NoItemFoundException("No project with id: " + projectId ));
        List<User> owners = project.getOwners();
        List<ProjectCollaborators> collaborators = project.getCollaborators();
        Long userId = message.getUser().getId();
        User user = userRepository.findById(userId).orElseThrow(() -> new NoItemFoundException("No user with id: " + userId));

        for(User owner : owners){
            if(owner.getId().equals(userId)){
                Message createdMessage = messageRepository.save(message);
                HttpStatus status = HttpStatus.CREATED;
                return new ResponseEntity<>(createdMessage, status);
            }
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

    public ResponseEntity<Message> editMessage(Long id, Message message, Long userId) throws NoItemFoundException{
        HttpStatus status;
        if(!id.equals(message.getId())){
            status = HttpStatus.BAD_REQUEST;
            return  new ResponseEntity<>(null, status);
        }
        Message checkMessage = messageRepository.findById(id).orElseThrow(()-> new NoItemFoundException("No message with id: " + id));

        if(!userId.equals(message.getUser().getId())){
            status = HttpStatus.BAD_REQUEST;
            return new ResponseEntity<>(null, status);
        }
        messageRepository.save(message);
        status = HttpStatus.CREATED;
        return new ResponseEntity<>(message, status);
    }

    public ResponseEntity<List<Message>> getAllByProject(Long projectId, Long userId) throws NoItemFoundException{
        List<Message> allMessages = messageRepository.findAll();

        Project project = projectRepository.findById(projectId).orElseThrow(() -> new  NoItemFoundException("No project with id: " + projectId));

        List<Message> projectMessages = allMessages.stream().filter(message ->
                (message.getProject().getId().equals(projectId))).collect(Collectors.toList());


        User user = userRepository.findById(userId).orElseThrow(() -> new NoItemFoundException("No user with id: " + userId));

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

        System.out.println("Length of projetmessages: " + projectMessages.size());
        System.out.println("Responseentity: " + new ResponseEntity<>(projectMessages, HttpStatus.OK));
        return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);

    }

}
