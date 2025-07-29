package com.company.notification.model.subscriber;

import com.company.notification.event.Event;
import com.company.notification.filters.EventFilter;

import java.util.UUID;

public interface Subscriber {
    void enqueue(Event event);
    void processQueue();
    String getName();
    void setName(String name);
    UUID getId();

    EventFilter getFilter();


}