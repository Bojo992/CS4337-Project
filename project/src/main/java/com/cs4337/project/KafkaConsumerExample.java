package com.cs4337.project;
import org.apache.kafka.clients.consumer.*;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class KafkaConsumerExample {
    public static void main(String[] args) {
        // Set Kafka consumer properties
        Properties props = new Properties();
        props.put("bootstrap.servers", "b-2.cs4337proj.08clt8.c2.kafka.eu-west-1.amazonaws.com:9092");
        props.put("group.id", "test_group");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("enable.auto.commit", "true"); // Enable auto commit of offsets
        props.put("auto.commit.interval.ms", "1000"); // Commit every second
        props.put("session.timeout.ms", "30000"); // Set session timeout to 30 seconds
        props.put("heartbeat.interval.ms", "10000"); // Heartbeat every 10 seconds

        // Create Kafka consumer
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList("test_topic"));

        // Poll for messages
        try {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    System.out.printf("Received message: key = %s, value = %s%n", record.key(), record.value());
                }
            }
        } catch (Exception e) {
            // Handle any exceptions that might occur
            System.err.println("Error while polling messages: " + e.getMessage());
        } finally {
            // Clean up and close the consumer gracefully
            consumer.close();
        }
    }
}
