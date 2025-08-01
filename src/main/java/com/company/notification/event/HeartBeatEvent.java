package com.company.notification.event;

import java.time.LocalDateTime;
import java.util.Objects;

public class HeartBeatEvent implements Event {
    private final LocalDateTime timeStamp;
    private final String publisherId;
    private final Priority priority ;
    private final String taskDescription;
    private final String taskName;

    // Default constructor
    public HeartBeatEvent(String publisherId, Priority priority) {
        this(publisherId, priority, "Heart Beat", "Reminder Event");
    }

    public HeartBeatEvent(String publisherId, Priority priority, String taskName, String taskDescription) {
        if (publisherId == null || publisherId.isBlank()) {
            throw new IllegalArgumentException("Publisher ID must not be null or blank");
        }
        if (priority == null) {
            throw new IllegalArgumentException("Priority must not be null");
        }

        this.timeStamp = LocalDateTime.now();
        this.publisherId = publisherId;
        this.priority = priority;
        this.taskName = (taskName == null || taskName.isBlank()) ? "Heart Beat" : taskName;
        this.taskDescription = (taskDescription == null || taskDescription.isBlank()) ? "Reminder Event" : taskDescription;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public String getPublisherId() {
        return publisherId;
    }

    @Override
    public EventTypes getType() {
        return EventTypes.HEARTBEAT;
    }

    @Override
    public LocalDateTime getDateTime() {
        return timeStamp;
    }

    @Override
    public String getSourcePublisherId() {
        return publisherId;
    }

    @Override
    public Priority getPriority() {
        return priority;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof HeartBeatEvent that)) return false;
        return Objects.equals(timeStamp, that.timeStamp) && Objects.equals(publisherId, that.publisherId) && priority == that.priority;
    }

    @Override
    public int hashCode() {
        return Objects.hash(timeStamp, publisherId, priority);
    }


    @Override
    public String toString() {
        return "HeartBeatEvent{" +
                "taskName='" + taskName + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                '}';
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public String getTaskName() {
        return taskName;
    }
}
