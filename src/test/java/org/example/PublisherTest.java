package org.example;


import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.testng.annotations.Test;

import java.util.Properties;

/**
 * Unit test for simple App.
 */
public class PublisherTest {

    @Test(enabled = false)
    public static void kafkaTest() {
        // Set up producer properties
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        // Create a Kafka producer instance
        System.out.println("-- creating producer object ----");
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        // Topic to publish to
        String topic = "sample-topic";

        try {
            for (int i = 0; i < 2; i++) {
                // Create a message
                String key = "key-" + i;
                String value = "value-" + i;

                // Publish the message to the topic
                ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);
                System.out.println("-- publishing event ----");
                producer.send(record);

                System.out.println("Published message: Key = " + key + ", Value = " + value);
            }
        } catch (Exception e) {
            System.out.println("---- an error occurred while publishing to the broker ----");
            e.printStackTrace();
        } finally {
            producer.close(); // Close the producer when done
        }
    }
}
