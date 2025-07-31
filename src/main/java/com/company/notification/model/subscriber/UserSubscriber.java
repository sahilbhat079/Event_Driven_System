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

        if (eventFilter == null || eventFilter.shouldProcess(event))
            queue.offer(event);

        //log who sent the event

    }



    @Override
    public void processQueue() {
        if (queue.isEmpty()) {
            System.out.println("\u001B[33m[" + name + "] No events to process.\u001B[0m");
            return;
        }

        System.out.println("\u001B[34m[" + name + "] Processing events (Priority-based):\u001B[0m");

        while (!queue.isEmpty()) {
            Event event = queue.poll();
            if (event instanceof com.company.notification.event.TaskEvent taskEvent) {
                System.out.println("\u001B[36m[Task Event]\u001B[0m");
                System.out.println("\u001B[36m  Task Name     : \u001B[0m" + taskEvent.getTaskName());
                System.out.println("\u001B[36m  Description   : \u001B[0m" + taskEvent.getTaskDescription());

            } else if (event instanceof com.company.notification.event.PriorityEvent priorityEvent) {
                System.out.println("\u001B[33m[Priority Event]\u001B[0m");
                System.out.println("\u001B[33m  Task Name     : \u001B[0m" + priorityEvent.getTaskName());
                System.out.println("\u001B[33m  Priority Level: \u001B[0m" + priorityEvent.getPriority());

            } else if (event instanceof com.company.notification.event.HeartBeatEvent heartbeatEvent) {
                System.out.println("\u001B[32m[Heartbeat Event]\u001B[0m");
                System.out.println("\u001B[32m  Task Name     : \u001B[0m" + heartbeatEvent.getTaskName());
                System.out.println("\u001B[32m  Priority Level: \u001B[0m" + heartbeatEvent.getPriority());
            } else {
                System.out.println("\u001B[90m[Unknown Event] \u001B[0m" + event);
            }
        }

        System.out.println("\u001B[32m[" + name + "] All events processed.\u001B[0m\n");
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

    @Override
    public String toString() {
        return "UserSubscriber{" +
                ", name='" + name + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
