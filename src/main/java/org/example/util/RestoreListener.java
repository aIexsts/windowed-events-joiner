package org.example.util;

import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.streams.processor.StateRestoreListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestoreListener implements StateRestoreListener {

    private static final Logger log = LoggerFactory.getLogger(RestoreListener.class);

    @Override
    public void onRestoreStart(
            TopicPartition topicPartition,
            String storeName,
            long startingOffset,
            long endingOffset
    ) {
        log.info("The following state store is being restored: {}", storeName);
    }

    @Override
    public void onRestoreEnd(
            TopicPartition topicPartition,
            String storeName,
            long totalRestored
    ) {
        log.info("Restore complete for the following state store: {}", storeName);
    }

    @Override
    public void onBatchRestored(
            TopicPartition topicPartition,
            String storeName,
            long batchEndOffset,
            long numRestored
    ) {
        // this is very noisy. don't log anything
    }
}
