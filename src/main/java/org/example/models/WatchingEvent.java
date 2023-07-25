package org.example.models;

public record WatchingEvent(
        String sessionId,
        Integer titleId,
        String createdAt
) implements Event {
    @Override
    public String getTimestamp() {
        return this.createdAt;
    }
}
