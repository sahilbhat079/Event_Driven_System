package com.company.notification.model.subscriber;

import com.company.notification.event.Event;
import com.company.notification.filters.EventFilter;


import java.util.LinkedList;
import java.util.Queue;

public class AdminSubscriber extends BaseSubscriber implements Subscriber {

    private final Queue<Event> queue;
    private final EventFilter eventFilter;

    public AdminSubscriber(String name, EventFilter eventFilter) {
        super(name);
        if (eventFilter == null) {
            throw new IllegalArgumentException("Filter cannot be null");
        }
        this.queue = new LinkedList<>();
        this.eventFilter = eventFilter;

    }




    @Override
    public void enqueue(Event event) {

    }

    @Override
    public void processQueue() {

    }


    @Override
    public EventFilter getFilter() {
        return eventFilter;
    }
}
