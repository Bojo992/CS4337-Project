package com.cs4337.project.service;

import com.cs4337.project.model.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class KafkaConsumerServices {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerServices.class);
    private final SimpMessagingTemplate messagingTemplate;
    private static final String TOPIC = "public-chats";

    private final List<ChatMessage> chatMessages = new ArrayList<>();
    public KafkaConsumerServices(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
    @KafkaListener(topicPattern = "chat-.*", groupId = "MyGroup")
    public void handleMessage(ChatMessage chatMessage) {
        LOGGER.info("Received message from Kafka: {}", chatMessage);
        chatMessages.add(chatMessage);
        messagingTemplate.convertAndSend("/topic/public", chatMessage);
    }
    public List<ChatMessage> getChatMessages(String id) {
        return new ArrayList<>(chatMessages);
    }
}
