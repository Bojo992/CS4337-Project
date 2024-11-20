package com.project.controller;

import com.project.model.ChatMemberKey;
import com.project.model.ChatMembers;
import com.project.service.ChatMembersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/chat-members")
public class ChatMembersController {

    @Autowired
    private ChatMembersService chatMembersService;

    @GetMapping
    public ResponseEntity<ChatMembers> getChatMembers(@RequestBody ChatMemberKey id) {
        Optional<ChatMembers> chatMember =  chatMembersService.getChatMemberById(id);
        return chatMember.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<?> createContact(@RequestBody ChatMembers chatMember) {
        try {
            chatMembersService.createChatMember(chatMember);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateContact(@RequestBody ChatMembers chatMember) {
        try {
            ChatMemberKey chatMemberKey = new ChatMemberKey(chatMember.getChatMemberKey().getUserId(), chatMember.getChatMemberKey().getChatId());
            chatMembersService.updateChatMember(chatMemberKey, chatMember);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUserById(@RequestBody ChatMemberKey id) {
        try {
            chatMembersService.deleteChatMember(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
