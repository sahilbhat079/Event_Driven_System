package com.company.notification.model.publisher;


import com.company.notification.event.Event;
import com.company.notification.model.subscriber.Subscriber;

public interface Publisher {
    void publish(Event event);
    String getName();
    String getId();
}