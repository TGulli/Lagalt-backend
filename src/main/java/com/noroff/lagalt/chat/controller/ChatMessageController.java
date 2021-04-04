package com.noroff.lagalt.chat.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.noroff.lagalt.chat.model.ChatMessage;
import com.noroff.lagalt.chat.repository.ChatMessageRepository;
import com.noroff.lagalt.chat.service.ChatMessageService;
import com.noroff.lagalt.message.model.Message;
import com.noroff.lagalt.user.model.User;
import com.noroff.lagalt.project.model.Project;
import com.noroff.lagalt.project.repository.ProjectRepository;
import com.noroff.lagalt.projectcollaborators.models.ProjectCollaborators;
import com.noroff.lagalt.projectcollaborators.models.Status;
import com.noroff.lagalt.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
                               SimpMessageHeaderAccessor headerAccessor) {
        return  chatMessageService.addUser(json, headerAccessor);


    }

    @Operation(summary = "Get all chatMessages by project id", security = { @SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Got all chatMessages by project",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ChatMessage.class)))}),
            @ApiResponse(responseCode = "400", description = "No existing projects by project id/No existing user sent the request/Could not find the projects chatMessages ",
                    content = @Content)})
    @GetMapping("/chatmessages/project/{id}/user/{userId}")
    public ResponseEntity<List<ChatMessage>> getChatByProject(@PathVariable(name = "id") Long projectId, @PathVariable(name = "userId" ) Long userId) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ingen prosjekt med id: " + projectId));
        if(!userRepository.existsById(userId)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ingen bruker med id: " + userId);
        }

        List<ChatMessage> chatMessages = chatMessageRepository.findAll();
        List<ChatMessage> projectMessages = chatMessages.stream().filter(message ->
                (message.getProject().getId().equals(projectId))).collect(Collectors.toList());

        User owner = project.getOwner();
        List<ProjectCollaborators> collaborators = project.getCollaborators();


        if(owner.getId().equals(userId)){
            return new ResponseEntity<>(projectMessages, HttpStatus.OK);
        }


        for(ProjectCollaborators collaborator : collaborators){
            if(collaborator.getStatus().equals(Status.APPROVED)){
                if (collaborator.getUser().getId().equals(userId)){
                    return new ResponseEntity<>(projectMessages, HttpStatus.OK);
                }
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fant ikke chaten til prosjektet.");
    }
}
