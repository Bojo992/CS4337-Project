package com.cs4337.project.controller;

import com.cs4337.project.model.ChatMessage;
import com.cs4337.project.service.KafkaConsumerServices;
import com.cs4337.project.service.KafkaProducerServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/***
 * The controller class for chat rooms, received from stompJS calls. The mappings are stored here.
 * @author royfl
 */
@RestController
@Slf4j
public class ChatController {
    @Autowired
    private SimpMessageSendingOperations simpMessagingTemplate;

    private final KafkaProducerServices kafkaProducerServices;
    private final KafkaConsumerServices kafkaConsumerServices;
    public ChatController(KafkaProducerServices kafkaProducerServices, KafkaConsumerServices kafkaConsumerServices) {
        this.kafkaProducerServices = kafkaProducerServices;
        this.kafkaConsumerServices = kafkaConsumerServices;
    }

    /***
     * This handles sending a user's chat message, and processing certain values such
     * as the timestamp, before sending it off to the server.
     * @param chatMessage the chat message as sent by the user.
     * @return the formatted chat message
     */
    @MessageMapping("/chat.sendMsg")
    @SendTo("/topic/{id}")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        chatMessage.setSentAt(LocalDateTime.now().toString());
        kafkaProducerServices.sendMessage(chatMessage);
        return chatMessage;
    }
    @MessageMapping("/chat.addUser")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor)  {
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }

    @MessageMapping("/chat.privateMessage")
    public ChatMessage pmUser(@Payload ChatMessage chatMessage)  {
        chatMessage.setSentAt(LocalDateTime.now().toString());
        simpMessagingTemplate.convertAndSendToUser(chatMessage.getRoom(),"room/",chatMessage);
        return chatMessage;
    }

    @GetMapping("/api/chat")
    public List<ChatMessage> getChatMessages() {
        return kafkaConsumerServices.getChatMessages();
    }
}
