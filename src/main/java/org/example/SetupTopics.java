package org.example;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.stereotype.Component;

@Component
public class SetupTopics {
    @Bean
    NewTopic startWatchingEventTopic() {
        return TopicBuilder.name("start-watching-events").partitions(4).replicas(1).build();
    }

    @Bean
    NewTopic stopWatchingEventTopic() {
        return TopicBuilder.name("stop-watching-events").partitions(4).replicas(1).build();
    }

    @Bean
    NewTopic shortSessionsTopic() {
        return TopicBuilder.name("short-sessions").partitions(4).replicas(1).build();
    }
}
