package org.example;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.model.Session;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class Consumer {
    @KafkaListener(topics = {"short-sessions"}, groupId = "spring-boot-kafka")
    public void consume(ConsumerRecord<String, Session> record) {
        log.info("[CONSUMER RECEIVED ALERT] - Short Session " + record.value() + " with key " + record.key());
    }
}
