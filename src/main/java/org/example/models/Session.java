package org.example.models;

public record Session(
        String sessionId,
        Integer titleId,
        String startedWatchingAt,
        String stoppedWatchingAt
) {
}
