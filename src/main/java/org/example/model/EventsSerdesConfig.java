package org.example.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.kafka.common.serialization.Serde;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;

@Configuration
public class EventsSerdesConfig {

    private static final TypeFactory TYPE_FACTORY = TypeFactory.defaultInstance();

    @Bean
    public Serde<WatchingEvent> watchingEventSerde(ObjectMapper objectMapper) {
        return new JsonSerde<>(TYPE_FACTORY.constructType(WatchingEvent.class), objectMapper);
    }

    @Bean
    public Serde<Session> sessionSerde(ObjectMapper objectMapper) {
        return new JsonSerde<>(TYPE_FACTORY.constructType(Session.class), objectMapper);
    }
}
