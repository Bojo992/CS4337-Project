package com.example.demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.demo.model.NotificationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class KafkaConsumerService {

    @Autowired
    private NotificationService notificationService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // List to hold active SSE emitters
    private final List<SseEmitter> emitters = new ArrayList<>();

    /**
     * Add an SSE emitter to the list
     */
    public SseEmitter addEmitter() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE); // Create an emitter with no timeout
        synchronized (emitters) {
            emitters.add(emitter);
        }
        
        // Remove emitter on completion, timeout, or error
        emitter.onCompletion(() -> removeEmitter(emitter));
        emitter.onTimeout(() -> removeEmitter(emitter));
        emitter.onError(e -> removeEmitter(emitter));
        
        return emitter;
    }

    /**
     * Remove an SSE emitter from the list
     */
    private void removeEmitter(SseEmitter emitter) {
        synchronized (emitters) {
            emitters.remove(emitter);
        }
    }

    /**
     * Listen to Kafka topic and broadcast messages via SSE
     */
    @KafkaListener(topics = "chat-notifications", groupId = "notification-service")
    public void listen(String message) {
        try {
            // Deserialize the Kafka message into a NotificationRequest object
            NotificationRequest request = objectMapper.readValue(message, NotificationRequest.class);
            
            // Send the email notification using the existing service
            notificationService.sendEmail(request);

            // Broadcast the notification via SSE to all connected clients
            broadcastToClients(request);
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
        }
    }

    public List<SseEmitter> getEmitters() {
        return emitters;
    }
    

    /**
     * Broadcast a message to all connected SSE clients
     */
    private void broadcastToClients(NotificationRequest request) {
        synchronized (emitters) {
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(SseEmitter.event()
                            .name("notification")
                            .data(request));
                } catch (IOException e) {
                    emitters.remove(emitter);
                    System.err.println("Error broadcasting to client: " + e.getMessage());
                }
            }
        }
    }
}


