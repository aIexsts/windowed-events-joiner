package org.example;

import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.WatchingEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
@RequiredArgsConstructor
public class Producer {
    private final KafkaTemplate<String, WatchingEvent> template;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Scheduled(cron = "*/10 * * * * *")
    public void generateWatchingEventsEveryTenSeconds() {
        Faker faker = Faker.instance();

        String sessionId = "session_" + faker.random().nextInt(1, 100000);
        Integer titleId = faker.random().nextInt(1, 100000);
        LocalDateTime startWatchingTime = LocalDateTime.now().minusHours(faker.random().nextInt(1, 336));
        LocalDateTime stopWatchingTime = startWatchingTime.plusMinutes(faker.random().nextInt(0, 20));

        WatchingEvent startWatchingEvent = new WatchingEvent(sessionId, titleId, dateTimeFormatter.format(startWatchingTime));
        WatchingEvent stopWatchingEvent = new WatchingEvent(sessionId, titleId, dateTimeFormatter.format(stopWatchingTime));

        template.send("start-watching-events", sessionId, startWatchingEvent);
        template.send("stop-watching-events", sessionId, stopWatchingEvent);

        log.info("[PRODUCING USER ACTIVITY]");
    }
}
