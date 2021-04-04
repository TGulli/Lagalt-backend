package com.noroff.lagalt.chat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.noroff.lagalt.chat.model.ChatMessage;
import com.noroff.lagalt.chat.repository.ChatMessageRepository;
import com.noroff.lagalt.project.model.Project;
import com.noroff.lagalt.project.repository.ProjectRepository;
import com.noroff.lagalt.projectcollaborators.models.ProjectCollaborators;
import com.noroff.lagalt.projectcollaborators.models.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


@Transactional
@Service
public class ChatMessageService {

    @Autowired
    ChatMessageRepository chatMessageRepository;

    @Autowired
    ProjectRepository projectRepository;

    //Adds a user to the messageheader for project, where the user and message is in the json object
    public ChatMessage addUser(ObjectNode json, SimpMessageHeaderAccessor headerAccessor) {
        if (json == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Json node is null");
        } else if (headerAccessor == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "headerAccessor is null");
        }

        JsonNode jsonUserId = json.get("user");
        Long userId = jsonUserId.get("id").asLong();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonChatMessage = json.get("message");


        if (userId == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fant ikke user id i json objektet");
        } else if (jsonChatMessage == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fant ikke message i json objektet");
        }

        ChatMessage chatMessage;
        try{
            chatMessage = objectMapper.treeToValue(jsonChatMessage, ChatMessage.class);
        } catch ( JsonProcessingException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Feilet med å adde bruker i chat.");
        }

        // Finds the project to the chat message.
        Long projectId = chatMessage.getProject().getId();
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No project"));

        // Adds the user and the project to the messageheader
        if (project.getOwner().getId().equals(userId)) {
            headerAccessor.getSessionAttributes().put("user", chatMessage.getSender());
            headerAccessor.getSessionAttributes().put("project", chatMessage.getProject().getId());
            return chatMessage;
        }


        for (ProjectCollaborators collaborator : project.getCollaborators()) {
            if (collaborator.getStatus().equals(Status.APPROVED) && collaborator.getUser().getId().equals(userId)) {
                if (headerAccessor.getSessionAttributes() == null){
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "headerAccessor.getSessionAttributes() er null");
                }
                headerAccessor.getSessionAttributes().put("user", chatMessage.getSender());
                headerAccessor.getSessionAttributes().put("project", chatMessage.getProject().getId());
                return chatMessage;
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kunne ikke legge til bruker i chat.");
    }
}
