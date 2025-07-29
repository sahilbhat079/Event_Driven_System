package com.company.notification.event;

import java.time.LocalDateTime;
import java.util.Objects;

public class PriorityEvent implements Event {
    private final Priority priority;
    private final LocalDateTime timeStamp;
    private final String message;
    private final String sourcePublisherId;

    public PriorityEvent(Priority priority, String message, String sourcePublisherId) {
       // null check
        if (priority == null) {
            throw new IllegalArgumentException("Priority cannot be null");
        }
        if (message == null || message.isEmpty()) {
            throw new IllegalArgumentException("Message cannot be null or empty");
        }
        if (sourcePublisherId == null || sourcePublisherId.isEmpty()) {
            throw new IllegalArgumentException("Source publisher ID cannot be null or empty");
        }
        this.priority = priority;
        this.timeStamp = LocalDateTime.now();
        this.message = message;
        this.sourcePublisherId = sourcePublisherId;
    }

    public Priority getPriority() {
        return priority;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public EventTypes getType() {
        return EventTypes.PRIORITY;
    }

    @Override
    public LocalDateTime getDateTime() {
        return null;
    }

    @Override
    public String getSourcePublisherId() {
        return sourcePublisherId;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PriorityEvent that)) return false;
        return priority == that.priority && Objects.equals(timeStamp, that.timeStamp) && Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(priority, timeStamp, message);
    }
}
