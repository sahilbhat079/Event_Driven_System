package com.company.notification.model.publisher;


import com.company.notification.core.EventBus;
import com.company.notification.event.Event;

public interface Publisher {
    void publish(EventBus eventBus, Event event);

    String getName();

    String getId();
}