package com.noroff.lagalt.message.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.noroff.lagalt.message.model.Message;
import com.noroff.lagalt.message.service.MessageService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("api/v1")
@CrossOrigin(origins = "*")
public class MessageController {

    @Autowired
    private MessageService messageService;

    // Adds a message to the database
    @Operation(summary = "Create a message", security = { @SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created a message",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Message.class)) }),
            @ApiResponse(responseCode = "400", description = "Not a valid message in the body/Not valid input to create message/Could not send message/request is not from an existing user",
                    content = @Content)})
    @PostMapping("/messages")
    public ResponseEntity<Message> addMessage(@RequestBody Message message) {
        return messageService.create(message);
    }

    // Updates a message by its id
    @Operation(summary = "Update a message by its id", security = { @SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Updated message",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Message.class)) }),
            @ApiResponse(responseCode = "400", description = "Not a valid request body/Not valid input to edit message/Could not edit message/request is not from an existing user",
                    content = @Content)})
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

    // Gets all messages by project id
    @Operation(summary = "Get all messages by project id", security = { @SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Got all messages by project",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Message.class)))}),
            @ApiResponse(responseCode = "400", description = "No existing projects by project id/No existing users by user id/request is not from an existing user",
                    content = @Content)})
    @GetMapping("/messages/project/{id}/user/{userid}")
    public ResponseEntity<List<Message>> getMessagesByProjectId(@PathVariable(value ="id") Long id, @PathVariable(value = "userid") Long userid){
        return messageService.getAllByProject(id, userid);
    }

    // Gets all the messages stored
    @Operation(summary = "Get all messages", security = { @SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Got all messages",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Message.class)))}) })
    @GetMapping("/messages")
    public ResponseEntity<List<Message>> getAllMessages(){
        return messageService.getAll();
    }
}
