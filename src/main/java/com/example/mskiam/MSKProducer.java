package com.example.mskiam;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class MSKProducer {
    private static final Logger logger = LoggerFactory.getLogger(MSKProducer.class);
    private static final Random random = new Random();

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: MSKProducer <bootstrap-servers> <topic-name> [number-of-messages]");
            System.exit(1);
        }

        String bootstrapServers = args[0];
        String topicName = args[1];
        int numMessages = args.length > 2 ? Integer.parseInt(args[2]) : 10;

        // Create producer properties
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        
        // Set IAM authentication properties
        properties.put("security.protocol", "SASL_SSL");
        properties.put("sasl.mechanism", "AWS_MSK_IAM");
        properties.put("sasl.jaas.config", "software.amazon.msk.auth.iam.IAMLoginModule required;");
        properties.put("sasl.client.callback.handler.class", "software.amazon.msk.auth.iam.IAMClientCallbackHandler");

        // Create the producer
        try (KafkaProducer<String, String> producer = new KafkaProducer<>(properties)) {
            logger.info("Connected to MSK cluster: {}", bootstrapServers);
            logger.info("Sending {} messages to topic: {}", numMessages, topicName);

            // Send messages
            for (int i = 0; i < numMessages; i++) {
                String messageKey = UUID.randomUUID().toString();
                String messageValue = generateRandomMessage();
                
                ProducerRecord<String, String> record = new ProducerRecord<>(topicName, messageKey, messageValue);
                
                // Send record and get metadata
                try {
                    RecordMetadata metadata = producer.send(record).get();
                    logger.info("Message sent: topic={}, partition={}, offset={}, key={}, value={}",
                            metadata.topic(), metadata.partition(), metadata.offset(), messageKey, messageValue);
                } catch (InterruptedException | ExecutionException e) {
                    logger.error("Error sending message", e);
                }
                
                // Small delay between messages
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            
            logger.info("All messages sent successfully!");
        }
    }

    private static String generateRandomMessage() {
        String[] events = {"login", "logout", "purchase", "page_view", "click", "signup"};
        String[] users = {"user123", "user456", "user789", "user101", "user202"};
        
        String event = events[random.nextInt(events.length)];
        String user = users[random.nextInt(users.length)];
        long timestamp = System.currentTimeMillis();
        
        return String.format("{\"event\":\"%s\",\"user\":\"%s\",\"timestamp\":%d}", event, user, timestamp);
    }
}
