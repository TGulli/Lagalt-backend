package com.noroff.lagalt.message.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.noroff.lagalt.exceptions.NoItemFoundException;
import com.noroff.lagalt.message.model.Message;
import com.noroff.lagalt.message.service.MessageService;
import com.noroff.lagalt.projectcollaborators.models.ProjectCollaborators;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1")
@CrossOrigin(origins = "*")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping("/messages")
    public ResponseEntity<Message> addMessage(@RequestBody Message message) throws NoItemFoundException {
        return messageService.create(message);
    }

    @PutMapping("/message/{id}")
    public ResponseEntity<Message> editMessage(@PathVariable(value = "id") Long id, @RequestBody ObjectNode json) throws JsonProcessingException, NoItemFoundException {
        JsonNode JsonUserId = json.get("user");
        Long userId = JsonUserId.get("id").asLong();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonMessage = json.get("message");
        Message message = objectMapper.treeToValue(jsonMessage, Message.class);
        return messageService.editMessage(id, message, userId);
    }

    @GetMapping("/messages/project/{id}/user/{userid}")
    public ResponseEntity<List<Message>> getMessagesByProjectId(@PathVariable(value ="id") Long id, @PathVariable(value = "userid") Long userid) throws NoItemFoundException{
        return messageService.getAllByProject(id, userid);
    }

    @GetMapping("/messages")
    public ResponseEntity<List<Message>> getAllMessages(){
        return messageService.getAll();
    }



}
