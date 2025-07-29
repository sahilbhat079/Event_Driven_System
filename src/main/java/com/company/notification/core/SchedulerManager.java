package com.company.notification.core;

import com.company.notification.model.publisher.Publisher;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SchedulerManager {
    private final EventBus eventBus;
    private final Map<Publisher, EventScheduler> schedulerMap;

    public SchedulerManager(EventBus eventBus) {
        this.eventBus = eventBus;
        this.schedulerMap = new ConcurrentHashMap<>();
    }

    public void registerScheduler(Publisher publisher, long intervalSeconds) {
        if (schedulerMap.containsKey(publisher)) {
            System.out.println("Scheduler already exists for publisher: " + publisher.getName());
            return;
        }
        eventBus.registerPublisher(publisher);
        EventScheduler scheduler = new EventScheduler(eventBus, publisher, intervalSeconds);
        schedulerMap.put(publisher, scheduler);
        scheduler.start();
    }

    public void shutdownScheduler(Publisher publisher) {
        EventScheduler scheduler = schedulerMap.remove(publisher);
        if (scheduler != null) {
            scheduler.shutdown();
            System.out.println("Scheduler shutdown for publisher: " + publisher.getName());
        }
    }

    public void shutdownAllSchedulers() {
        schedulerMap.forEach((publisher, scheduler) -> scheduler.shutdown());
        System.out.println("Scheduler shutdown for all publishers");
        schedulerMap.clear();
    }
}
