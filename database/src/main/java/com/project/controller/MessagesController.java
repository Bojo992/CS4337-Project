package com.project.controller;

import com.project.model.Chats;
import com.project.model.Messages;
import com.project.model.Users;
import com.project.service.MessagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/messages")
public class MessagesController {

    @Autowired
    private MessagesService messagesService;

    @GetMapping("/id")
    public ResponseEntity<Messages> getMessage(@RequestParam Long id) {
        Optional<Messages> message = messagesService.getMessageById(id);
        return message.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/chat")
    public ResponseEntity<List<Messages>> getChats(@RequestParam Long id) {
        List<Messages> messages = messagesService.getMessagesByChat(id);
        return messages.isEmpty() ? ResponseEntity.ok(messages) : ResponseEntity.notFound().build();
    }

    @PostMapping("/create")
    public ResponseEntity<?> createContact(@RequestBody Messages message) {
        try {
            messagesService.createMessage(message);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateContact(@RequestBody Messages message) {
        try {
            messagesService.updateMessage(message.getId(), message);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUserById(@RequestParam Long id) {
        try {
            messagesService.deleteMessage(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
