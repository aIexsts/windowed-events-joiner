package org.example;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableKafka
@EnableKafkaStreams
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class WindowedEventJoinerApplication {

    public static void main(String[] args) {
        SpringApplication.run(WindowedEventJoinerApplication.class, args);
    }

    @EventListener(ApplicationStartedEvent.class)
    public void appStartedListener() {
        log.info("APP STARTED");
    }
}


