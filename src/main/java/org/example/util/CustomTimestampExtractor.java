package org.example.util;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;
import org.example.models.Event;

import java.time.Instant;

//TODO: format -> yyyy-MM-dd HH:mm:ss
public class CustomTimestampExtractor implements TimestampExtractor {
    @Override
    public long extract(ConsumerRecord<Object, Object> record, long partitionTime) {
        Event event = (Event) record.value();
        if (event != null && event.getTimestamp() != null) {
            String timestamp = event.getTimestamp();
            return Instant.parse(timestamp).toEpochMilli();
        }
        return partitionTime;
    }
}
