package com.company.notification.model.subscriber;

import com.company.notification.event.Event;
import com.company.notification.filters.EventFilter;

import java.util.PriorityQueue;
import java.util.Queue;

public class UserSubscriber extends BaseSubscriber implements Subscriber{
  private final Queue<Event> queue;
  private EventFilter eventFilter;
    public UserSubscriber(String name, EventFilter eventFilter) {
        super(name);
        this.queue =new PriorityQueue<>();
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
        return null;
    }
}
