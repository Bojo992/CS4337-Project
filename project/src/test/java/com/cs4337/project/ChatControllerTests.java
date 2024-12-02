package com.cs4337.project;

import com.cs4337.project.model.ChatMessage;
import com.cs4337.project.model.MessageType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Testcontainers
@RunWith(SpringRunner.class)
@EmbeddedKafka(partitions = 1, topics = { "testTopic" })
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChatControllerTests {
    private static final Logger LOGGER = Logger.getLogger(ChatControllerTests.class.getName());
    private final WebSocketStompClient stompClient;
    private final BlockingQueue<ChatMessage> messageQueue = new LinkedBlockingQueue<>();
    @Value("${local.server.port}")
    private int port;
    @Container
    static final MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0.28")
            .withDatabaseName("randomTest")
            .withUsername("test")
            .withPassword("1234")
            .withCopyFileToContainer(
                    MountableFile.forClasspathResource("schema.sql"),
                    "/docker-entrypoint-initdb.d/schema.sql")
            ;

    static {
        mySQLContainer.withDatabaseName("test").start();
    }


    @DynamicPropertySource
    static void configureTestProperties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url",() -> mySQLContainer.getJdbcUrl());
        registry.add("spring.datasource.username",() -> mySQLContainer.getUsername());
        registry.add("spring.datasource.password",() -> mySQLContainer.getPassword());
        registry.add("spring.jpa.hibernate.ddl-auto",() -> "create");
    }

    public ChatControllerTests() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());


        stompClient = new WebSocketStompClient(
                new SockJsClient(Collections.singletonList(new WebSocketTransport(new StandardWebSocketClient())))
        );


        MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();
        messageConverter.setObjectMapper(objectMapper);
        stompClient.setMessageConverter(messageConverter);
    }

    @Test
    public void testChat() throws Exception {
        String url = "ws://localhost:" + port + "/ws";
        StompSession session = stompClient.connectAsync(url, new StompSessionHandlerAdapter() {}).get();


        session.subscribe("/topic/public", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return ChatMessage.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                try {
                    ChatMessage receivedMessage = (ChatMessage) payload;
                    LOGGER.info("Received message: " + receivedMessage);
                    LOGGER.info("Received sentAt: " + receivedMessage.getSentAt());
                    messageQueue.add(receivedMessage);
                } catch (Exception e) {
                    LOGGER.severe("Error parsing payload: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });


        testWebSocketCommunication(session);
    }

    private void testWebSocketCommunication(StompSession session) throws Exception {
        sendTestMessage(session);


        ChatMessage receivedMessage = messageQueue.poll(10, TimeUnit.SECONDS);
        if (receivedMessage == null) {
            LOGGER.warning("No message received within the timeout period.");
        } else {
            LOGGER.info("Received message content: " + receivedMessage.getContent());
        }


        Assertions.assertNotNull(receivedMessage, "Expected to receive a message within the timeout period.");
        Assertions.assertEquals("Test message", receivedMessage.getContent(),
                "Message content verification failed. Actual content: " + receivedMessage.getContent());


        Assertions.assertEquals("testUser", receivedMessage.getSender(), "Sender verification failed.");
        Assertions.assertEquals(MessageType.CHAT, receivedMessage.getType(), "Message type verification failed.");
        Assertions.assertNotNull(receivedMessage.getSentAt(), "Sent time should not be null.");
        Assertions.assertFalse(receivedMessage.isSeen(), "Message should not be seen yet.");
        Assertions.assertNull(receivedMessage.getMedia(), "Media should be null.");
    }

    public void sendTestMessage(StompSession session) throws IOException {

        ChatMessage testMessage = new ChatMessage(
                "Test message",
                "testUser",
                MessageType.CHAT,
                LocalDateTime.now().toString(),
                null,
                false,
                "public"
        );


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