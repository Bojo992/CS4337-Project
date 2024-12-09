package com.cs4337.project.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/***
 * Basic setup for the Message Broker, currently websockets using STOMP protocol and Sock JS library
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    /***
     * For the websockets implementation, it adds an endpoint for SockJS to control incoming messages.
     * @param registry The Stomp endpoint to register
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    }
    /***
     * Enables the message broker, subscribed to the "/topic" prefix.
     * @param registry The registry of message broker options.
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }
  
  
    @Bean
    public MappingJackson2MessageConverter messageConverter() {
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json()
                .modulesToInstall(JavaTimeModule.class) // Register JavaTimeModule
                .build();
        return new MappingJackson2MessageConverter(objectMapper);
    }
}
