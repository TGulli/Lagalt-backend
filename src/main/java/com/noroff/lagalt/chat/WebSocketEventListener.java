package com.noroff.lagalt.chat;

import com.noroff.lagalt.chat.model.ChatMessage;
import com.noroff.lagalt.chat.model.ChatMessageType;
import com.noroff.lagalt.chat.repository.ChatMessageRepository;
import com.noroff.lagalt.exceptions.NoItemFoundException;
import com.noroff.lagalt.model.User;
import com.noroff.lagalt.project.model.Project;
import com.noroff.lagalt.project.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
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


    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event){
        System.out.println("Received new connection");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) throws NoItemFoundException{
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String user = (String) headerAccessor.getSessionAttributes().get("user");
        Long projectId = (Long) headerAccessor.getSessionAttributes().get("project");
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new NoItemFoundException("No project with id: " + projectId));
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
            chatMessageRepository.save(chatMessage);





            messagingTemplate.convertAndSend("/topic/public", chatMessage);
        }

    }
}
