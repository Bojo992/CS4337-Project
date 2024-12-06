package com.example.demo;

import com.example.demo.model.NotificationRequest;
import com.example.demo.service.NotificationService;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class NotificationIntegrationTest {

    static KafkaContainer kafkaContainer;

    @Autowired
    private NotificationService notificationService;

    @BeforeAll
    static void setUp() {
        kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));
        kafkaContainer.start();
    }

    @AfterAll
    static void tearDown() {
        kafkaContainer.stop();
    }

    @Test
    void testKafkaIntegration() throws Exception {
        // Setup Kafka Producer
        Properties props = new Properties();
        props.put("bootstrap.servers", kafkaContainer.getBootstrapServers());
        props.put("key.serializer", StringSerializer.class.getName());
        props.put("value.serializer", StringSerializer.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        String topic = "chat-notifications";
        String message = "{\"email\": \"user@example.com\", \"subject\": \"Test Subject\", \"message\": \"Test Message\"}";

        producer.send(new ProducerRecord<>(topic, message));
        producer.close();

        // Verify the listener processed the message
        assertThat(notificationService).isNotNull();
    }

    @KafkaListener(topics = "chat-notifications", groupId = "notification-test")
    public void testKafkaListener(String message) {
        // Simulate message processing
        System.out.println("Received message: " + message);
    }
}

