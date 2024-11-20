package com.cs4337.project.controller;

import com.cs4337.project.model.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

/***
 * The controller class for chat rooms, received from stompJS calls. The mappings are stored here.
 * @author royfl
 */
@Controller
public class ChatController {
    @Autowired
    private SimpMessageSendingOperations simpMessagingTemplate;

    /***
     * This handles sending a user's chat message, and processing certain values such
     * as the timestamp, before sending it off to the server.
     * @param chatMessage the chat message as sent by the user.
     * @return the formatted chat message
     */
    @MessageMapping("/chat.sendMsg")
    @SendTo("/topic/{id}")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        chatMessage.setSentAt(LocalDateTime.now());
        return chatMessage;
    }
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor)  {
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }

    @MessageMapping("/chat.privateMessage")
    public ChatMessage pmUser(@Payload ChatMessage chatMessage)  {
        chatMessage.setSentAt(LocalDateTime.now());
        simpMessagingTemplate.convertAndSendToUser(chatMessage.getRoom(),"room/",chatMessage);
        return chatMessage;
    }
}
