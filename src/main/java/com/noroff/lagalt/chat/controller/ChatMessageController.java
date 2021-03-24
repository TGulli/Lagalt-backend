package com.noroff.lagalt.chat.controller;

import com.noroff.lagalt.chat.model.ChatMessage;
import com.noroff.lagalt.chat.repository.ChatMessageRepository;
import com.noroff.lagalt.exceptions.NoItemFoundException;
import com.noroff.lagalt.model.User;
import com.noroff.lagalt.repository.UserRepository;
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

@Controller
@CrossOrigin(origins = "*")
public class ChatMessageController {

    @Autowired
    ChatMessageRepository chatMessageRepository;

    @Autowired
    UserRepository userRepository;

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage){
        System.out.println("Inside sendMessage");
        System.out.println("CONTENT " + chatMessage.getContent());
        System.out.println("TYPE: " + chatMessage.getType());
        //chatMessageRepository.save(chatMessage);
        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage,
                               SimpMessageHeaderAccessor headerAccessor) throws NoItemFoundException{
        // Add username in web socket session
        System.out.println("Inside addUser");
        System.out.println(chatMessage);

        headerAccessor.getSessionAttributes().put("user", chatMessage.getSender());
        return chatMessage;
    }

    /*@GetMapping("/chatmessages/user/{id}")
    public ResponseEntity<List<ChatMessage>> getChatByUser(@PathVariable(name = "id") Long userId) throws NoItemFoundException {
        User user = userRepository.findById(userId).orElseThrow(() -> new NoItemFoundException("Found no user with id: " + userId));
        List<ChatMessage> chatMessages = chatMessageRepository.findAll();
        List<ChatMessage> senderMessages = chatMessages.stream().filter(message ->
                (message.getSender().getId().equals(userId))).collect(Collectors.toList());
        List<ChatMessage> receiverMessages = new ArrayList<>();

        for(ChatMessage chatMessage : chatMessages){
            List<User> receivers = chatMessage.getReceivers();
            for(User receiver: receivers){
                if(receiver.getId().equals(userId)){
                    receiverMessages.add(chatMessage);
                }
            }
        }
        List<ChatMessage> returnList = Stream.concat(senderMessages.stream(), receiverMessages.stream())
                .collect(Collectors.toList());
        return new ResponseEntity<>(returnList, HttpStatus.OK);
    }*/
}
