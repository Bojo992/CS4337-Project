package com.cs4337.project.controller;

import com.cs4337.project.model.AddOrRemoveUserFromGroupChatRequest;
import com.cs4337.project.model.CreatGroupChatRequest;
import com.cs4337.project.model.CreatPersonalChatRequest;
import com.cs4337.project.service.ChatService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ChatRESTController {
    private ChatService chatService;

    public ChatRESTController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/createPersonalChat")
    public Map<String, Object> createPersonalChat(@RequestBody CreatPersonalChatRequest request) {
        return chatService.createPersonalChat(request);
    }

    @PostMapping("/createGroupChat")
    public Map<String, Object> createGroupChat(@RequestBody CreatGroupChatRequest request) {
        return chatService.creatGroupChat(request);
    }

    @PostMapping("/removeUserFromChat")
    public Map<String, Object> removeUser(@RequestBody AddOrRemoveUserFromGroupChatRequest request) {
        return chatService.removeUserFromGroupChat(request);
    }

    @PostMapping("/addUserToChat")
    public Map<String, Object> addUser(@RequestBody AddOrRemoveUserFromGroupChatRequest request) {
        return chatService.addUserToGroupChat(request);
    }

    @PostMapping("/getAllChatsForUser")
    public Map<String, Object> getAllChatsForUser(@RequestBody Map<String, Integer> request) {
        return chatService.getAllChatsForUser(request.get("userId"));
    }
}
