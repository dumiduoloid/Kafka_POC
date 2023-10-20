package org.example;

import io.vertx.core.json.JsonObject;
import org.apache.kafka.clients.consumer.*;
import org.testng.annotations.Test;

import java.util.*;

public class ConsumerTest {

    @Test()
    public static void testKafkaHealthCheckMethod() {
        KafkaUtil kafkaUtil = new KafkaUtil(60);
        boolean status = kafkaUtil.checkBrokerHealth();
        System.out.println("-------- kafka broker status " + status);
    }


    @Test()
    public static void testConsumerMethod() {
        KafkaUtil kafkaUtil = new KafkaUtil(60);
        JsonObject actual = kafkaUtil.consume("use-case-topic", 60, "ClientStatusChangeNotification", "1697604718889");
        System.out.println("The actual result is: \t" + actual.toString());
    }
}
