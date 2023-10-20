package org.example;

import io.vertx.core.json.JsonObject;
import io.vertx.kafka.client.serialization.JsonObjectDeserializer;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KafkaUtil {
    private int timout;
    private final String BROKER_URL = "b-2.hgcminonprodqakakfa.cij5j6.c2.kafka.eu-west-2.amazonaws.com:9092,b-1.hgcminonprodqakakfa.cij5j6.c2.kafka.eu-west-2.amazonaws.com:9092";
    private final int POLLING_INTERVAL_IN_SECONDS = 10;
    private final String OFFSET_STRATEGY = "earliest";

    public KafkaUtil(int timout) {
        this.timout = timout;
    }


    /***
     * Checks to see if the kafka broker is up and available for consumer connections.
     * @return
     */
    public boolean checkBrokerHealth() {
        boolean isTheBrokerRunning = false;
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, BROKER_URL);

        try (AdminClient adminClient = AdminClient.create(props)) {
            DescribeClusterResult clusterResult = adminClient.describeCluster();
            KafkaFuture<String> clusterIdFuture = clusterResult.clusterId();

            String clusterId = clusterIdFuture.get();
            Logger.getAnonymousLogger().log(Level.INFO, String.format("Kafka broker is running. Cluster ID: %s", clusterId));
            isTheBrokerRunning = true;
        } catch (Exception e) {
            e.printStackTrace();
            Logger.getAnonymousLogger().log(Level.INFO, "Kafka broker is not running.");
        }
        return isTheBrokerRunning;
    }

    /***
     * consumes a given kafka topic from the beginning and returns the collection of events.
     * @param topic
     * @param sleepDuration
     * @return
     */
    public JsonObject consume(String topic, int sleepDuration, String attributeName, String attributeValueSearchTerm) {
        Properties consumerProperties = new Properties();
        consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BROKER_URL);
        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonObjectDeserializer.class.getName());
        consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
        consumerProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, OFFSET_STRATEGY);

        //todo : exception handling improvements
        KafkaConsumer<String, JsonObject> consumer = new KafkaConsumer<>(consumerProperties);
        JsonObject event = null;
        try {
            consumer.subscribe(Arrays.asList(topic));
            Thread.sleep(sleepDuration);
            Logger.getAnonymousLogger().log(Level.INFO, "Polling at an interval of " + POLLING_INTERVAL_IN_SECONDS);

            int durationInSeconds = timout;
            long startTime = System.currentTimeMillis();
            long endTime = startTime + (durationInSeconds * 1000);

            while (System.currentTimeMillis() < endTime) {
                JsonObject result = processPolledInData(consumer.poll(Duration.ofSeconds(5)), attributeName, attributeValueSearchTerm);
                if (result != null) {
                    event = result;
                }
            }
        } catch (NullPointerException | InterruptedException e) {
            Logger.getAnonymousLogger().log(Level.INFO, "End of the stream reached.");
        } finally {
            consumer.close();
        }
        return event;
    }

    /***
     * processing related utility methods
     */


    /***
     * The event processor returns an event that match the search criteria from the collection of events. Or a null.
     * @param events the collection of events.
     * @param attributeName name of the attribute that will be used as part of the search criteria.
     * @param attributeValueSearchTerm the value of the attribute that will be used as part of the search criteria.
     * @return
     */
    public JsonObject processPolledInData(ConsumerRecords<String, JsonObject> events, String attributeName, String attributeValueSearchTerm) {
        JsonObject result = null;
        if (events.isEmpty()) {
            return result;
        }
        Iterator<ConsumerRecord<String, JsonObject>> iterator = events.iterator();
        Logger.getAnonymousLogger().log(Level.INFO, String.format("Number of events in the topic: %s", events.count()));
        while (iterator.hasNext()) {
            ConsumerRecord<String, JsonObject> event = iterator.next();
            if (event.value().containsKey(attributeName)) {
                if (event.value().getString(attributeName).toString().contains(attributeValueSearchTerm)) {
                    Logger.getAnonymousLogger().log(Level.INFO, String.format("Event found: %s", events.count()));
                    result = event.value();
                }
            }
        }
        return result;
    }

    public static int getTheEventCountForTheTopic(ConsumerRecords<String, JsonObject> dataStream) {
        return dataStream.count();
    }

}
