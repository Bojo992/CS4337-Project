package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.demo.model.NotificationRequest;
import com.example.demo.service.KafkaConsumerService;
import com.example.demo.service.NotificationService;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class KafkaConsumerServiceTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private KafkaConsumerService kafkaConsumerService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public KafkaConsumerServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testKafkaListener() throws Exception {
        // Arrange
        String kafkaMessage = "{\"email\": \"user@example.com\", \"subject\": \"Test Subject\", \"message\": \"Test Message\"}";
        NotificationRequest request = objectMapper.readValue(kafkaMessage, NotificationRequest.class);

        // Act
        kafkaConsumerService.listen(kafkaMessage);

        // Assert
        verify(notificationService, times(1)).sendEmail(request);
    }
}

