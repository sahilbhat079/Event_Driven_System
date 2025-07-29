package com.company.notification.model.subscriber;

import com.company.notification.event.Event;

public interface Subscriber {
    void enqueue(Event event);
    void processQueue();
}