package com.cs4337.project.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {


    /**
     * Using the TopicBuilder class to build our "Mailbox" with 10 partitions.
     * You can have as many partitions as you like which is nice for scalability and parallelism.
     * @return A topic(categorises and stores messages).
     */
    @Bean
    public NewTopic PublicChats() {
        return TopicBuilder.name("public-chats").partitions(10).build();
    }
}
