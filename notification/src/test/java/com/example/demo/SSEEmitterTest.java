package com.example.demo;

import com.example.demo.model.NotificationRequest;
import com.example.demo.service.KafkaConsumerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

class SSEEmitterTest {

    @InjectMocks
    private KafkaConsumerService kafkaConsumerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddEmitter() {
        // Act
        SseEmitter emitter = kafkaConsumerService.addEmitter();

        // Assert
        assertThat(emitter).isNotNull();
    }

    @Test
    void testEmittersAccess() throws Exception {
        // Arrange
        SseEmitter emitter = kafkaConsumerService.addEmitter();

        // Use reflection to access the private `emitters` field
        Field emittersField = KafkaConsumerService.class.getDeclaredField("emitters");
        emittersField.setAccessible(true);

        // Act
        @SuppressWarnings("unchecked")
        List<SseEmitter> emitters = (List<SseEmitter>) emittersField.get(kafkaConsumerService);

        // Assert
        assertNotNull(emitters);
        assertTrue(emitters.contains(emitter));
    }

    @Test
    void testBroadcastToClients() throws Exception {
        // Arrange
        NotificationRequest request = new NotificationRequest("user@example.com", "Test Subject", "Test Message");
        SseEmitter emitter = kafkaConsumerService.addEmitter();

        // Use reflection to access the private `emitters` field
        Field emittersField = KafkaConsumerService.class.getDeclaredField("emitters");
        emittersField.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<SseEmitter> emitters = (List<SseEmitter>) emittersField.get(kafkaConsumerService);

        // Mock an SSE event listener
        emitter.onError((e) -> emitters.remove(emitter));

        // Act
        kafkaConsumerService.getEmitters().forEach(e -> {
            try {
                e.send(SseEmitter.event().name("notification").data(request));
            } catch (IOException ex) {
                emitters.remove(e);
            }
        });

        // Assert
        assertTrue(emitters.contains(emitter));
    }

    @Test
void testRemoveEmitterOnError() throws Exception {
    // Arrange
    SseEmitter emitter = kafkaConsumerService.addEmitter();

    // Use reflection to access the private `emitters` field
    Field emittersField = KafkaConsumerService.class.getDeclaredField("emitters");
    emittersField.setAccessible(true);

    @SuppressWarnings("unchecked")
    List<SseEmitter> emitters = (List<SseEmitter>) emittersField.get(kafkaConsumerService);

    // Act
    kafkaConsumerService.getEmitters().remove(emitter); // Explicitly remove the emitter to simulate error handling

    // Assert
    assertThat(emitters).doesNotContain(emitter);
}
}

    