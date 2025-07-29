package com.company.notification.filters;

import com.company.notification.event.Event;
import com.company.notification.event.Priority;

public class PriorityFilter implements EventFilter {
    private final Priority priority;

    public PriorityFilter(Priority priority) {
        this.priority = priority;
    }

    @Override
    public boolean shouldProcess(Event event) {
        return event.getPriority() == priority;
    }


    @Override
    public String toString() {
        return "PriorityFilter{" +
                "priority=" + priority +
                '}';
    }
}
