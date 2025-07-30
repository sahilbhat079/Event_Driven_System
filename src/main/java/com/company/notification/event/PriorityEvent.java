package com.company.notification.event;

import java.time.LocalDateTime;
import java.util.Objects;

public class PriorityEvent implements Event {

    private final String taskName;
    private final Priority priority;
    private final LocalDateTime timeStamp;
    private final String taskDescription;
    private final String sourcePublisherId;

    public PriorityEvent(String taskName, Priority priority, String taskDescription, String sourcePublisherId) {
        this.taskName = taskName;
        // null check
        if (priority == null) {
            throw new IllegalArgumentException("Priority cannot be null");
        }
        if (taskDescription == null || taskDescription.isEmpty()) {
            throw new IllegalArgumentException("Message cannot be null or empty");
        }
        if (sourcePublisherId == null || sourcePublisherId.isEmpty()) {
            throw new IllegalArgumentException("Source publisher ID cannot be null or empty");
        }
        this.priority = priority;
        this.timeStamp = LocalDateTime.now();
        this.taskDescription = taskDescription;
        this.sourcePublisherId = sourcePublisherId;
    }

    public Priority getPriority() {
        return priority;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public String getTaskName() {
        return taskName;
    }

    @Override
    public EventTypes getType() {
        return EventTypes.PRIORITY;
    }

    @Override
    public LocalDateTime getDateTime() {
        return timeStamp;
    }

    @Override
    public String getSourcePublisherId() {
        return sourcePublisherId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PriorityEvent that)) return false;
        return priority == that.priority
                && Objects.equals(timeStamp, that.timeStamp)
                && Objects.equals(taskName, that.taskName)
                && Objects.equals(taskDescription, that.taskDescription)
                && Objects.equals(sourcePublisherId, that.sourcePublisherId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(priority, timeStamp, taskName, taskDescription, sourcePublisherId);
    }


    @Override
    public String toString() {
        return  "PriorityEvent{" +
                "taskName='" + taskName + '\'' +
                ", message='" + taskDescription + '\'' ;
    }

    public String getTaskDescription() {
        return taskDescription;
    }
}
