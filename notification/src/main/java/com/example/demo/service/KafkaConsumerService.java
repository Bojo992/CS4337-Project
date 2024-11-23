package com.example.demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.demo.model.NotificationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    @Autowired
    private NotificationService notificationService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "chat-notifications", groupId = "notification-service")
    public void listen(String message) {
        try {
            NotificationRequest request = objectMapper.readValue(message, NotificationRequest.class);
            notificationService.sendEmail(request);
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
        }
    }
}

