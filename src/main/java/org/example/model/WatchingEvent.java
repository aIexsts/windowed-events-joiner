package org.example.model;

public record WatchingEvent(
        String sessionId,
        Integer titleId,
        String timestamp
) {

}
