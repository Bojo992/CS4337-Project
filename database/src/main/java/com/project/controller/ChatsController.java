package com.project.controller;

import com.project.model.Chats;
import com.project.service.ChatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/chats")
public class ChatsController {

    @Autowired
    private ChatsService chatsService;

    @GetMapping("/id")
    public ResponseEntity<Chats> getChat(@RequestParam Long id) {
        Optional<Chats> chat = chatsService.getChatById(id);
        return chat.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<?> createContact(@RequestBody Chats chat) {
        try {
            chatsService.createChat(chat);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateContact(@RequestBody Chats chat) {
        try {
            chatsService.updateChat(chat.getId(), chat);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUserById(@RequestParam Long id) {
        try {
            chatsService.deleteChat(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
