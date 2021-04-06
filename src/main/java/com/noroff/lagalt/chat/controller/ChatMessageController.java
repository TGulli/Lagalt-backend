package com.noroff.lagalt.chat.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.noroff.lagalt.chat.model.ChatMessage;
import com.noroff.lagalt.chat.repository.ChatMessageRepository;
import com.noroff.lagalt.chat.service.ChatMessageService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

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


    // Stores the chat message to send in the database.
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage){

        chatMessageRepository.save(chatMessage);
        return chatMessage;
    }

    // Adds a user to a chat in a project, and appends the user to the database.
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ObjectNode json,
                               SimpMessageHeaderAccessor headerAccessor) {
        return  chatMessageService.addUser(json, headerAccessor);


    }

    // Gets all chatmessages for project based on the id, if the user id matches the owner of the project, or the user is a collaborator in the project.
    @Operation(summary = "Get all chatMessages by project id", security = { @SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Got all chatMessages by project", content = {
                    @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ChatMessage.class)))}),

            @ApiResponse(responseCode = "400", description = "No existing projects by project id/No existing user sent the request/Could not find the projects chatMessages ",
                    content = @Content)})
    @GetMapping("/chatmessages/project/{id}/user/{userId}")
    public ResponseEntity<List<ChatMessage>> getChatByProject(@PathVariable(name = "id") Long projectId,
                                                              @PathVariable(name = "userId" ) Long userId) {
        Project project = projectRepository.findById(projectId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ingen prosjekt med id: " + projectId));
        if(!userRepository.existsById(userId)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ingen bruker med id: " + userId);
        }

        // Filter out the messages for the given project, from all messages from repository.
        List<ChatMessage> projectMessages = chatMessageRepository.findAll().stream().filter(message ->
                (message.getProject().getId().equals(projectId))).collect(Collectors.toList());

        // Returns the messages if the user is the owner
        if(project.getOwner().getId().equals(userId)){
            return new ResponseEntity<>(projectMessages, HttpStatus.OK);
        }

        // Returns the messages if the user is a collaborator
        for(ProjectCollaborators collaborator : project.getCollaborators()){
            if(collaborator.getStatus().equals(Status.APPROVED) && collaborator.getUser().getId().equals(userId)){
                return new ResponseEntity<>(projectMessages, HttpStatus.OK);
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fant ikke chaten til prosjektet.");
    }
}
