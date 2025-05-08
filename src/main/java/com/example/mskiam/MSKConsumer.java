package com.example.mskiam;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class MSKConsumer {
    private static final Logger logger = LoggerFactory.getLogger(MSKConsumer.class);

    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("Usage: MSKConsumer <bootstrap-servers> <topic-name> <consumer-group-id>");
            System.exit(1);
        }

        String bootstrapServers = args[0];
        String topicName = args[1];
        String groupId = args[2];

        // Create consumer properties
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        
        // Set IAM authentication properties
        properties.put("security.protocol", "SASL_SSL");
        properties.put("sasl.mechanism", "AWS_MSK_IAM");
        properties.put("sasl.jaas.config", "software.amazon.msk.auth.iam.IAMLoginModule required;");
        properties.put("sasl.client.callback.handler.class", "software.amazon.msk.auth.iam.IAMClientCallbackHandler");

        // Create the consumer
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties)) {
            // Subscribe to the topic
            consumer.subscribe(Collections.singletonList(topicName));
            logger.info("Connected to MSK cluster: {}", bootstrapServers);
            logger.info("Subscribed to topic: {}", topicName);
            logger.info("Using consumer group: {}", groupId);
            
            // Poll for messages
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                
                for (ConsumerRecord<String, String> record : records) {
                    logger.info("Received message: topic={}, partition={}, offset={}, key={}, value={}",
                            record.topic(), record.partition(), record.offset(), record.key(), record.value());
                }
            }
        }
    }
}
