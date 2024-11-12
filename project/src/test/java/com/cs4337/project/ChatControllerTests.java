package com.cs4337.project;

import com.cs4337.project.model.ChatMessage;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.http.converter.json.MappingJackson2MessageConverter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import java.util.Collections;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;

public class ChatControllerTests {

    WebSocketStompClient stompClient;

    public ChatControllerTests() {
        stompClient = new WebSocketStompClient(
                new SockJsClient(Collections.singletonList(new WebSocketTransport(new StandardWebSocketClient())))
        );
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    public void testChat() throws ExecutionException, InterruptedException {
        String url = "ws://localhost:8080/ws"; // Replace with actual endpoint path
        StompSession session = stompClient.connect(url, new StompSessionHandlerAdapter() {}).get();

        BlockingQueue<ChatMessage> messageQueue = new LinkedBlockingQueue<>();

        // Subscribe to a topic and add messages to the queue when received
        session.subscribe("/topic/public", new StompFrameHandler() {
            @Override
            public Class<ChatMessage> getPayloadType(StompHeaders headers) {
                return ChatMessage.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                messageQueue.add((ChatMessage) payload);
            }
        });

        // Test logic (optional)
        // Send test messages or perform actions that trigger WebSocket communication
        // Example:
        // sendTestMessage(session);

        // Optionally, wait for messages and assert or process
        // ChatMessage message = messageQueue.take(); // blocks until message is received
        // Assert some condition on message or verify behavior
    }
}
