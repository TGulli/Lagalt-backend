package com.noroff.lagalt.chat.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.noroff.lagalt.chat.model.ChatMessage;
import com.noroff.lagalt.chat.repository.ChatMessageRepository;
import com.noroff.lagalt.chat.service.ChatMessageService;
import com.noroff.lagalt.exceptions.NoItemFoundException;
import com.noroff.lagalt.user.model.User;
import com.noroff.lagalt.project.model.Project;
import com.noroff.lagalt.project.repository.ProjectRepository;
import com.noroff.lagalt.projectcollaborators.models.ProjectCollaborators;
import com.noroff.lagalt.projectcollaborators.models.Status;
import com.noroff.lagalt.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController()
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
public class ChatMessageController {

    @Autowired
    ChatMessageRepository chatMessageRepository;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ChatMessageService chatMessageService;


    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage){

        chatMessageRepository.save(chatMessage);
        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ObjectNode json,
                               SimpMessageHeaderAccessor headerAccessor) throws JsonProcessingException, NoItemFoundException {
        return  chatMessageService.addUser(json, headerAccessor);


    }

    @GetMapping("/chatmessages/project/{id}/user/{userId}")
    public ResponseEntity<List<ChatMessage>> getChatByProject(@PathVariable(name = "id") Long projectId, @PathVariable(name = "userId" ) Long userId) throws NoItemFoundException {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new NoItemFoundException("Found no project with id: " + projectId));
        User user = userRepository.findById(userId).orElseThrow(() -> new NoItemFoundException("Found no user with id: " + userId));

        List<ChatMessage> chatMessages = chatMessageRepository.findAll();
        List<ChatMessage> projectMessages = chatMessages.stream().filter(message ->
                (message.getProject().getId().equals(projectId))).collect(Collectors.toList());

        List<User> owners = project.getOwners();
        List<ProjectCollaborators> collaborators = project.getCollaborators();

        for(User owner : owners){
            if(owner.getId().equals(userId)){
                return new ResponseEntity<>(projectMessages, HttpStatus.OK);
            }
        }

        for(ProjectCollaborators collaborator : collaborators){
            if(collaborator.getStatus().equals(Status.APPROVED)){
                if (collaborator.getUser().getId().equals(userId)){
                    return new ResponseEntity<>(projectMessages, HttpStatus.OK);
                }
            }
        }

        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }


}
