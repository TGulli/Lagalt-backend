package com.noroff.lagalt.message.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.noroff.lagalt.message.model.Message;
import com.noroff.lagalt.message.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("api/v1")
@CrossOrigin(origins = "*")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping("/messages")
    public ResponseEntity<Message> addMessage(@RequestBody Message message) {
        return messageService.create(message);
    }

    @PutMapping("/message/{id}")
    public ResponseEntity<Message> editMessage(@PathVariable(value = "id") Long id, @RequestBody ObjectNode json)  {
        if (json == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Json objektet er null");
        }
        JsonNode JsonUserId = json.get("user");
        if (JsonUserId == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "JsonID for user var ikke lagret.");
        }
        JsonNode userId = JsonUserId.get("id");
        if (userId == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId var ikke lagret.");
        }
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonMessage = json.get("message");
        if (jsonMessage == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "jsonMessage var ikke lagret.");
        }
        try{
            Message message = objectMapper.treeToValue(jsonMessage, Message.class);
            return messageService.editMessage(id, message, userId.asLong());
        } catch (JsonProcessingException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "jsonMessage ble ikke endret.");
        }
    }

    @GetMapping("/messages/project/{id}/user/{userid}")
    public ResponseEntity<List<Message>> getMessagesByProjectId(@PathVariable(value ="id") Long id, @PathVariable(value = "userid") Long userid){
        return messageService.getAllByProject(id, userid);
    }

    @GetMapping("/messages")
    public ResponseEntity<List<Message>> getAllMessages(){
        return messageService.getAll();
    }
}
