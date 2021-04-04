package com.noroff.lagalt.chat;

import com.noroff.lagalt.chat.model.ChatMessage;
import com.noroff.lagalt.chat.model.ChatMessageType;
import com.noroff.lagalt.chat.repository.ChatMessageRepository;
import com.noroff.lagalt.project.model.Project;
import com.noroff.lagalt.project.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ProjectRepository projectRepository;


    // When a connection is made, the server prints out a new connection message
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event){
        System.out.println("Received new connection");
    }

    // When connection disconnects, the state is set to LEAVE, and the server prints out information about the user left.
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event){
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String user = (String) headerAccessor.getSessionAttributes().get("user");
        Long projectId = (Long) headerAccessor.getSessionAttributes().get("project");
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No project"));
        System.out.println("SESSIONATTRIBUTES ");
        System.out.println(headerAccessor.getSessionAttributes());
        System.out.println("in handle disconnect");
        System.out.println(headerAccessor);

        if(user != null) {
            System.out.println("User left chat: " + user);

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(ChatMessageType.LEAVE);
            chatMessage.setSender(user);
            chatMessage.setProject(project);

            messagingTemplate.convertAndSend("/topic/public", chatMessage);
        }

    }
}
