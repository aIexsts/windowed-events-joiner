package org.example;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.example.models.WatchingEvent;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Properties;

public class Producer {

    static Properties props() {
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName());
        return properties;
    }

    public static void main(String[] args) {

        final KafkaProducer<String, WatchingEvent> producer = new KafkaProducer<>(props());

        producer.send(new ProducerRecord<>(
                "start-watching-events",
                "session_123",
                new WatchingEvent("session_123", 1, "2021-02-08 02:00:00")
        ));

        producer.send(new ProducerRecord<>(
                "start-watching-events",
                "session_123",
                new WatchingEvent("session_123", 1, "2021-02-08 02:01:30")
        ));

        producer.send(new ProducerRecord<>(
                "start-watching-events",
                "session_456",
                new WatchingEvent("session_456", 1, "2021-02-08 02:00:00")
        ));

        producer.send(new ProducerRecord<>(
                "start-watching-events",
                "session_456",
                new WatchingEvent("session_456", 1, "2021-02-08 02:25:00")
        ));

        producer.flush();
    }
}