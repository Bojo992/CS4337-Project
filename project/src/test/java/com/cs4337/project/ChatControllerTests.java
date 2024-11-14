package com.cs4337.project;

import com.cs4337.project.model.ChatMessage;
import com.cs4337.project.model.MessageType;
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChatControllerTests {

    private static final Logger LOGGER = Logger.getLogger(ChatControllerTests.class.getName());
    private final WebSocketStompClient stompClient;
    private final BlockingQueue<ChatMessage> messageQueue = new LinkedBlockingQueue<>();

    public ChatControllerTests() {
        stompClient = new WebSocketStompClient(
                new SockJsClient(Collections.singletonList(new WebSocketTransport(new StandardWebSocketClient())))
        );
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    @Test
    public void testChat() throws Exception {
        String url = "ws://localhost:8080/ws"; // Adjust the URL to match your WebSocket endpoint
        StompSession session = stompClient.connectAsync(url, new StompSessionHandlerAdapter() {}).get();

        // Subscribe to a topic and add messages to the queue when received
        session.subscribe("/topic/public", new StompFrameHandler() {
            @Override
            public Class<ChatMessage> getPayloadType(StompHeaders headers) {
                return ChatMessage.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                ChatMessage receivedMessage = (ChatMessage) payload;
                System.out.println("Received message: " + receivedMessage);  // Log received message
                messageQueue.add(receivedMessage);
            }
        });

        // Run WebSocket communication test
        testWebSocketCommunication(session);
    }

    private void testWebSocketCommunication(StompSession session) throws Exception {
        sendTestMessage(session);

        // Retrieve the message from the queue
        ChatMessage receivedMessage = messageQueue.poll(5, TimeUnit.SECONDS); // Poll for 5 seconds
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
        Assertions.assertNotNull(receivedMessage.getSentAt(), "Sent time should not be null.");
        Assertions.assertFalse(receivedMessage.isSeen(), "Message should not be seen yet.");
        Assertions.assertNull(receivedMessage.getMedia(), "Media should be null.");
    }

    private void sendTestMessage(StompSession session) {
        String sender = "testUser";
        ChatMessage testMessage = new ChatMessage(
                "Test message",           // content
                sender,               // sender
                MessageType.CHAT,         // type
                LocalDateTime.now(),      // sentAt
                null,                     // media (null if not needed)
                false                     // isSeen (false when first sent)
        );

        session.send("/app/chat.sendMsg", testMessage);
        LOGGER.info("Test message sent to /app/chat.sendMsg: " + testMessage);
    }
}
