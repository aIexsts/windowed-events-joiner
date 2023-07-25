package org.example.json;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.example.models.Session;
import org.example.models.WatchingEvent;

public class JsonSerdes {

    public static Serde<WatchingEvent> WatchingEvent() {
        JsonSerializer<WatchingEvent> serializer = new JsonSerializer<>();
        JsonDeserializer<WatchingEvent> deserializer = new JsonDeserializer<>(WatchingEvent.class);
        return Serdes.serdeFrom(serializer, deserializer);
    }

    public static Serde<Session> Session() {
        JsonSerializer<Session> serializer = new JsonSerializer<>();
        JsonDeserializer<Session> deserializer = new JsonDeserializer<>(Session.class);
        return Serdes.serdeFrom(serializer, deserializer);
    }
}
