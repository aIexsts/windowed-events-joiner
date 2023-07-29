package org.example.util;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;
import org.example.model.WatchingEvent;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;

public class WatchingEventTimestampExtractor implements TimestampExtractor {

    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());


    @Override
    public long extract(ConsumerRecord<Object, Object> record, long partitionTime) {
        try {
            WatchingEvent event = (WatchingEvent) record.value();
            if (event != null && event.timestamp() != null) {
                Instant timeStamp = Instant.from(dateTimeFormatter.parse(event.timestamp()));
                return timeStamp.toEpochMilli();
            }
        } catch (DateTimeParseException ignore) {}

        return partitionTime;
    }
}
