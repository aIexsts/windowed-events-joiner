package org.example.model;

public record Session(
        String sessionId,
        Integer titleId,
        String startedWatchingAt,
        String stoppedWatchingAt
) {
}
