package com.company.notification.event;

import java.time.LocalDateTime;
import java.util.Objects;

public class TaskEvent implements Event {
    private final String taskDescription;
    private final String taskName;
    private final LocalDateTime timeStamp;
    private final Priority priority;

    public TaskEvent(String taskDescription, String taskName, LocalDateTime timeStamp, Priority priority) {
        this.taskDescription = taskDescription;
        this.taskName = taskName;
        this.timeStamp = timeStamp;
        this.priority = priority;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

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
        return null;
    }

    @Override
    public LocalDateTime getDateTime() {
        return null;
    }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TaskEvent taskEvent)) return false;
        return Objects.equals(taskDescription, taskEvent.taskDescription) && Objects.equals(taskName, taskEvent.taskName) && Objects.equals(timeStamp, taskEvent.timeStamp) && Objects.equals(priority, taskEvent.priority);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskDescription, taskName, timeStamp,priority);
    }
}
