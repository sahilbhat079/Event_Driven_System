package com.company.notification.event;

import java.time.LocalDateTime;
import java.util.Objects;

public class TaskEvent implements Event {
    private final String taskDescription;
    private final String taskName;
    private final String sourcePublisherId;
    private final LocalDateTime timeStamp;
    private final Priority priority;


    public TaskEvent(String taskName,String taskDescription , String sourcePublisherId, Priority priority) {
        //null check
        if (taskDescription == null || taskDescription.isEmpty()) {
            throw new IllegalArgumentException("Task description cannot be null or empty");
        }
        if (taskName == null || taskName.isEmpty()) {
            throw new IllegalArgumentException("Task name cannot be null or empty");
        }
        if (sourcePublisherId == null || sourcePublisherId.isEmpty()) {
            throw new IllegalArgumentException("Source publisher ID cannot be null or empty");
        }
        if (priority == null) {
            throw new IllegalArgumentException("Priority cannot be null");
        }
        this.taskDescription = taskDescription;
        this.taskName = taskName;
        this.sourcePublisherId = sourcePublisherId;
        this.timeStamp = LocalDateTime.now();
        this.priority = priority;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    @Override
    public Priority getPriority() {
        return priority;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    @Override
    public EventTypes getType() {
        return EventTypes.TASK;
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
        if (!(o instanceof TaskEvent taskEvent)) return false;
        return Objects.equals(taskDescription, taskEvent.taskDescription)
                && Objects.equals(taskName, taskEvent.taskName)
                && Objects.equals(sourcePublisherId, taskEvent.sourcePublisherId)
                && Objects.equals(timeStamp, taskEvent.timeStamp)
                && Objects.equals(priority, taskEvent.priority);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskDescription, taskName, sourcePublisherId, timeStamp, priority);
    }

    @Override
    public String toString() {
        return "TaskEvent{" +
                "taskName='" + taskName + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                '}';
    }
}
