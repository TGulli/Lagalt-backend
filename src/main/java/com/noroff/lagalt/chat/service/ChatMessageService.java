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
import com.noroff.lagalt.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Transactional
@Service
public class ChatMessageService {

    @Autowired
    ChatMessageRepository chatMessageRepository;

    @Autowired
    ProjectRepository projectRepository;

    public ChatMessage addUser(ObjectNode json,
                               SimpMessageHeaderAccessor headerAccessor) throws JsonProcessingException {

        JsonNode jsonUserId = json.get("user");
        Long userId = jsonUserId.get("id").asLong();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonChatMessage = json.get("message");
        ChatMessage chatMessage = objectMapper.treeToValue(jsonChatMessage, ChatMessage.class);

        Long projectId = chatMessage.getProject().getId();
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No project"));
        User owner = project.getOwner();
        List<ProjectCollaborators> collaborators = project.getCollaborators();


        if (owner.getId().equals(userId)) {
            //chatMessageRepository.save(chatMessage);
            headerAccessor.getSessionAttributes().put("user", chatMessage.getSender());
            headerAccessor.getSessionAttributes().put("project", chatMessage.getProject().getId());
            return chatMessage;
        }


        for (ProjectCollaborators collaborator : collaborators) {
            if (collaborator.getStatus().equals(Status.APPROVED)) {
                if (collaborator.getUser().getId().equals(userId)) {
                    //chatMessageRepository.save(chatMessage);
                    headerAccessor.getSessionAttributes().put("user", chatMessage.getSender());
                    headerAccessor.getSessionAttributes().put("project", chatMessage.getProject().getId());
                    return chatMessage;
                }
            }
        }
        System.out.println("Add user returning null");
        //headerAccessor.getSessionAttributes().put("user", chatMessage.getSender());
        //headerAccessor.getSessionAttributes().put("project", chatMessage.getProject().getId());
        return null;
    }
}
