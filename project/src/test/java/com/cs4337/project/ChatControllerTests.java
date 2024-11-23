package com.cs4337.project;

import com.cs4337.project.model.ChatMessage;
import com.cs4337.project.model.MessageType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChatControllerTests {

    private static final Logger LOGGER = Logger.getLogger(ChatControllerTests.class.getName());
    private final WebSocketStompClient stompClient;
    private final BlockingQueue<ChatMessage> messageQueue = new LinkedBlockingQueue<>();

    public ChatControllerTests() {
        // Register JavaTimeModule for handling LocalDateTime serialization and deserialization
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Create and configure WebSocketStompClient
        stompClient = new WebSocketStompClient(
                new SockJsClient(Collections.singletonList(new WebSocketTransport(new StandardWebSocketClient())))
        );

        // Set the custom message converter with the properly configured ObjectMapper
        MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();
        messageConverter.setObjectMapper(objectMapper);
        stompClient.setMessageConverter(messageConverter);
    }

    @Test
    public void testChat() throws Exception {
        String url = "ws://localhost:8080/ws"; // Adjust the URL to match your WebSocket endpoint
        StompSession session = stompClient.connectAsync(url, new StompSessionHandlerAdapter() {}).get();

        // Subscribe to a topic and add messages to the queue when received
        session.subscribe("/topic/public", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return ChatMessage.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                try {
                    ChatMessage receivedMessage = (ChatMessage) payload;  // Direct cast to ChatMessage
                    LOGGER.info("Received message: " + receivedMessage);
                    LOGGER.info("Received sentAt: " + receivedMessage.getSentAt());
                    messageQueue.add(receivedMessage);
                } catch (Exception e) {
                    LOGGER.severe("Error parsing payload: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });

        // Run WebSocket communication test
        testWebSocketCommunication(session);
    }

    private void testWebSocketCommunication(StompSession session) throws Exception {
        sendTestMessage(session);

        // Retrieve the message from the queue
        ChatMessage receivedMessage = messageQueue.poll(10, TimeUnit.SECONDS); // Poll for 10 seconds
        if (receivedMessage == null) {
            LOGGER.warning("No message received within the timeout period.");
        } else {
            LOGGER.info("Received message content: " + receivedMessage.getContent());
        }

        // Assertions to verify message content
        Assertions.assertNotNull(receivedMessage, "Expected to receive a message within the timeout period.");
        Assertions.assertEquals("Test message", receivedMessage.getContent(),
                "Message content verification failed. Actual content: " + receivedMessage.getContent());

        // Ensure other fields are correct
        Assertions.assertEquals("testUser", receivedMessage.getSender(), "Sender verification failed.");
        Assertions.assertEquals(MessageType.CHAT, receivedMessage.getType(), "Message type verification failed.");
        Assertions.assertNull(receivedMessage.getSentAt(), "Sent time should not be null.");
        Assertions.assertFalse(receivedMessage.isSeen(), "Message should not be seen yet.");
        Assertions.assertNull(receivedMessage.getMedia(), "Media should be null.");
    }

    public void sendTestMessage(StompSession session) throws IOException {

        ChatMessage testMessage = new ChatMessage(
                "Test message",           // content
                "testUser",                // sender
                MessageType.CHAT, // type
                LocalDateTime.now(),       // sentAt
                null,                      // media (null if not needed)
                false,                      // isSeen (false when first sent)
                "public"
        );

        // Convert to JSON and check for errors
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        try {
            String jsonMessage = objectMapper.writeValueAsString(testMessage);
            System.out.println("Serialized ChatMessage: " + jsonMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        session.send("/app/chat.sendMsg", testMessage);
        LOGGER.info("Test message sent to /app/chat.sendMsg: " + testMessage);
    }
}
