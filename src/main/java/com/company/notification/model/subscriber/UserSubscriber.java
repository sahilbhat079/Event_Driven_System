package com.company.notification.model.subscriber;

import com.company.notification.event.Event;
import com.company.notification.filters.EventFilter;
import com.company.notification.utils.EventComparator;

import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;

public class UserSubscriber extends BaseSubscriber implements Subscriber{
  private final Queue<Event> queue;
  private final EventFilter eventFilter;
    public UserSubscriber(String name, EventFilter eventFilter) {
        super(name);
        this.queue =new PriorityQueue<>(new EventComparator());
        this.eventFilter = eventFilter;
    }


    @Override
    public void enqueue(Event event) {
        if (event == null) {
            System.out.println(" [" + name + "] Received null event. Ignored.");
            return;
        }

        if (eventFilter == null || eventFilter.shouldProcess(event)) {
            queue.offer(event);
            System.out.println(" [" + name + "] Queued event: " + event);
        } else {
            System.out.println(" [" + name + "] Event filtered out: " + event);
        }
    }

    @Override
    public void processQueue() {
        if (queue.isEmpty()) {
            System.out.println(" [" + name + "] No events to process.");
            return;
        }

        System.out.println(" [" + name + "] Processing events (Priority-based):");

        while (!queue.isEmpty()) {
            Event event = queue.poll(); // automatically gives highest priority first
            System.out.println("    " + event);
        }

        System.out.println("[" + name + "] All events processed.\n");
    }

    @Override
    public EventFilter getFilter() {
        return eventFilter;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserSubscriber that)) return false;
        return id.equals(that.id) && name.equals(that.name) && createdAt.equals(that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, createdAt);
    }
}
