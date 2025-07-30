package com.company.notification.model.subscriber;

import com.company.notification.event.Event;
import com.company.notification.filters.EventFilter;

import java.util.LinkedList;
import java.util.Objects;
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
        if (event == null) {
            System.out.println("Admin: " + name + "] Null event ignored.");
            return;
        }
    if (eventFilter.shouldProcess(event))
        queue.offer(event);

    }

    @Override
    public void processQueue() {
        if (queue.isEmpty()) {
            System.out.println(" [Admin: " + name + "] No events to process.");
            return;
        }

        System.out.println("\n [Admin: " + name + "] Processing event log:");

        while (!queue.isEmpty()) {
            Event event = queue.poll();
            System.out.println( event);
        }

        System.out.println(" [Admin: " + name + "] All events processed.\n");
    }

    @Override
    public EventFilter getFilter() {
        return eventFilter;
    }

    @Override
    public String toString() {
        return "AdminSubscriber{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AdminSubscriber that)) return false;
        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
