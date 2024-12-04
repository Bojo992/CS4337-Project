package com.cs4337.project;

import org.apache.kafka.clients.producer.*;

import java.util.Properties;


public class KafkaProducerExample {
    public static void main(String[] args) {
        // Set Kafka producer properties
        Properties props = new Properties();
        props.put("bootstrap.servers", "b-2.cs4337proj.08clt8.c2.kafka.eu-west-1.amazonaws.com:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        // Create Kafka producer
        Producer<String, String> producer = new KafkaProducer<>(props);

        // Send a message
        ProducerRecord<String, String> record = new ProducerRecord<>("test_topic", "key", "Hello from AWS Kafka!");
        producer.send(record);

        // Close producer
        producer.close();
        System.out.println("Message sent to AWS Kafka!");
    }
}

