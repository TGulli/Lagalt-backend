package com.noroff.lagalt.chat;

import com.noroff.lagalt.chat.model.ChatMessage;
import com.noroff.lagalt.chat.model.ChatMessageType;
import com.noroff.lagalt.model.User;
import com.noroff.lagalt.project.model.Project;
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


    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event){
        System.out.println("Received new connection");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String user = (String) headerAccessor.getSessionAttributes().get("user");
        System.out.println("in handle disconnect");
        System.out.println(headerAccessor);
        if(user != null) {
            System.out.println("User left chat: " + user);

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(ChatMessageType.LEAVE);
            chatMessage.setSender(user);



            messagingTemplate.convertAndSend("/topic/public", chatMessage);
        }

    }
}
