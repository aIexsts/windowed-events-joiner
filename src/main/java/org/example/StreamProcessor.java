package org.example;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.example.model.Session;
import org.example.model.WatchingEvent;
import org.example.util.WatchingEventTimestampExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@Slf4j
@RequiredArgsConstructor
public class StreamProcessor {

    private final Serde<WatchingEvent> watchingEventSerde;
    private final Serde<Session> sessionSerde;

    @Autowired
    public void process(StreamsBuilder builder) {
        KStream<String, WatchingEvent> startWatchingEventsStream = builder.stream(
                "start-watching-events",
                Consumed.with(Serdes.String(), watchingEventSerde).withTimestampExtractor(new WatchingEventTimestampExtractor())
        );

        KStream<String, WatchingEvent> stopWatchingEventsStream = builder.stream(
                "stop-watching-events",
                Consumed.with(Serdes.String(), watchingEventSerde).withTimestampExtractor(new WatchingEventTimestampExtractor())
        );

        // repartition the key:
        KStream<String, WatchingEvent> startWatchingEventsStreamRepartitioned = startWatchingEventsStream.selectKey((key, value) -> value.sessionId());
        KStream<String, WatchingEvent> stopWatchingEventsStreamRepartitioned = stopWatchingEventsStream.selectKey((key, value) -> value.sessionId());

        // join streams on window of 10 minutes
        KStream<String, Session> joinedShortSessions = startWatchingEventsStreamRepartitioned.join(stopWatchingEventsStreamRepartitioned,
                // Joiner
                (startWatchingEvent, stopWatchingEvent) -> new Session(
                        startWatchingEvent.sessionId(),
                        startWatchingEvent.titleId(),
                        startWatchingEvent.timestamp(),
                        stopWatchingEvent.timestamp()
                ),
                // Window
                JoinWindows.ofTimeDifferenceAndGrace(Duration.ofMinutes(10), Duration.ofMinutes(5)),
                // Params
                StreamJoined.with(Serdes.String(), watchingEventSerde, watchingEventSerde)
        );

        joinedShortSessions.to("short-sessions", Produced.with(Serdes.String(), sessionSerde));

        // debug only
        startWatchingEventsStreamRepartitioned.print(Printed.<String, WatchingEvent>toSysOut().withLabel("start-watching-events"));
        stopWatchingEventsStreamRepartitioned.print(Printed.<String, WatchingEvent>toSysOut().withLabel("stop-watching-events"));
        joinedShortSessions.print(Printed.<String, Session>toSysOut().withLabel("short-sessions"));
    }
}
