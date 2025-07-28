package com.company.notification.event;

import java.time.LocalDateTime;
import java.util.Objects;

public class PriorityEvent implements Event {
    private final Priority priority;
    private final LocalDateTime timeStamp;
    private final String message;

    public PriorityEvent(Priority priority, LocalDateTime timeStamp, String message) {
        this.priority = priority;
        this.timeStamp = timeStamp;
        this.message = message;
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
    public boolean equals(Object o) {
        if (!(o instanceof PriorityEvent that)) return false;
        return priority == that.priority && Objects.equals(timeStamp, that.timeStamp) && Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(priority, timeStamp, message);
    }
}
