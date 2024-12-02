package com.cs4337.project.config;

import com.cs4337.project.model.ChatMessage;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Map;

@Configuration
public class KafkaTopicConfig {


    /**
     * Using the TopicBuilder class to build our "Mailbox" with 10 partitions.
     * You can have as many partitions as you like which is nice for scalability and parallelism.
     * @return A topic(categorises and stores messages).
     */
    @Bean
    public NewTopic PublicChats() {
        return TopicBuilder.name("public-chats").partitions(3).build();
    }



}
