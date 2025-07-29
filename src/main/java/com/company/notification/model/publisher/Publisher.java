package com.company.notification.model.publisher;


import com.company.notification.event.Event;

public interface Publisher {
    void publish(Event event);
}