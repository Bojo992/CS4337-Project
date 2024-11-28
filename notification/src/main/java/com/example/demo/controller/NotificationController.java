package com.example.demo.controller;

import com.example.demo.model.NotificationRequest;
import com.example.demo.service.KafkaConsumerService;
import com.example.demo.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private KafkaConsumerService kafkaConsumerService;

    /**
     * Send email-based notifications
     */
    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(@RequestBody NotificationRequest request) {
        try {
            notificationService.sendEmail(request);
            return new ResponseEntity<>("Notification sent successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error sending notification: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * SSE endpoint for clients to subscribe to notifications
     */
    @GetMapping("/subscribe")
    public SseEmitter subscribeToNotifications() {
        return kafkaConsumerService.addEmitter();
    }
}

