package org.example;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.Stores;
import org.apache.kafka.streams.state.WindowBytesStoreSupplier;
import org.example.json.JsonSerdes;
import org.example.models.Session;
import org.example.models.WatchingEvent;
import org.example.util.CustomTimestampExtractor;
import org.example.util.RestoreListener;

import java.time.Duration;
import java.util.Properties;

public class StreamsApp {
    public static Properties config() {
        Properties properties = new Properties();
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-application");
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return properties;
    }


    // STORE FOR EVENTS TO BE JOINED
    private static final Duration retentionPeriod = Duration.ofSeconds(300);

    private static final WindowBytesStoreSupplier storeStartEventSupplier = Stores.inMemoryWindowStore(
            "start-watching-event-store", retentionPeriod, Duration.ofSeconds(240), true);
    private static final WindowBytesStoreSupplier storeStopEventSupplier = Stores.inMemoryWindowStore(
            "stop-watching-event-store", retentionPeriod, Duration.ofSeconds(240), true);

    public static Topology createTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, WatchingEvent> startWatchingEventsStream = builder.stream(
                "start-watching-events",
                Consumed.with(Serdes.String(), JsonSerdes.WatchingEvent()).withTimestampExtractor(new CustomTimestampExtractor())
        );

        KStream<String, WatchingEvent> stopWatchingEventsStream = builder.stream(
                "stop-watching-events",
                Consumed.with(Serdes.String(), JsonSerdes.WatchingEvent()).withTimestampExtractor(new CustomTimestampExtractor())
        );

        // repartition the key:
        KStream<String, WatchingEvent> startWatchingEventsStreamRepartitioned = startWatchingEventsStream.selectKey((key, value) -> value.sessionId());
        KStream<String, WatchingEvent> stopWatchingEventsStreamRepartitioned = stopWatchingEventsStream.selectKey((key, value) -> value.sessionId());

        // join streams on window of 2 minutes
        KStream<String, Session> joinedShortSessions = startWatchingEventsStreamRepartitioned.join(stopWatchingEventsStreamRepartitioned,
                // Joiner
                (startWatchingEvent, stopWatchingEvent) -> new Session(
                        startWatchingEvent.sessionId(),
                        startWatchingEvent.titleId(),
                        startWatchingEvent.getTimestamp(),
                        stopWatchingEvent.getTimestamp()
                ),

                // Window
                JoinWindows.of(Duration.ofMinutes(2)).grace(Duration.ofMinutes(1)),

                // Serdes + Stores
                StreamJoined.<String, WatchingEvent, WatchingEvent>with(storeStartEventSupplier, storeStopEventSupplier)
                        .withKeySerde(Serdes.String())
                        .withValueSerde(JsonSerdes.WatchingEvent())
                        .withOtherValueSerde(JsonSerdes.WatchingEvent())
        );

        joinedShortSessions.to("short-sessions", Produced.with(Serdes.String(), JsonSerdes.Session()));

        // debug only
        startWatchingEventsStreamRepartitioned.print(Printed.<String, WatchingEvent>toSysOut().withLabel("start-watching-event"));
        stopWatchingEventsStreamRepartitioned.print(Printed.<String, WatchingEvent>toSysOut().withLabel("stop-watching-event"));
        joinedShortSessions.print(Printed.<String, Session>toSysOut().withLabel("short-sessions"));

        return builder.build();
    }

    public static void main(String[] args) {
        KafkaStreams streams = new KafkaStreams(createTopology(), config());
        configureStreams(streams);
        streams.start();
    }

    private static void configureStreams(KafkaStreams streams) {
        // state listener
        streams.setStateListener((oldState, newState) -> {
            if (newState.equals(KafkaStreams.State.REBALANCING)) {
                System.out.println("REBALANCING");
            }
        });

        // state restore listener
        streams.setGlobalStateRestoreListener(new RestoreListener());

        // close Kafka Streams when the JVM shuts down (e.g. SIGTERM)
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

        streams.cleanUp();
    }
}
