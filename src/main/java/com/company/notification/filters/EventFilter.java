package com.company.notification.filters;

import com.company.notification.event.Event;
@FunctionalInterface
public interface EventFilter {
    boolean shouldProcess(Event event);
}
