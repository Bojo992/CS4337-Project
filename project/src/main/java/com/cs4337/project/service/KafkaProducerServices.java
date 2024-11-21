package com.cs4337.project.service;

import com.cs4337.project.model.ChatMessage;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerServices {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducerServices.class);
    private static final String TOPIC = "public-chats";
    private final KafkaTemplate<String, ChatMessage> kafkaTemplate;
    public KafkaProducerServices(KafkaTemplate<String, ChatMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    public void sendMessage(ChatMessage chatMessage) {
        kafkaTemplate.send(TOPIC, chatMessage);
        LOGGER.info("sENT message to Kafka: {}", chatMessage);
    }
}
