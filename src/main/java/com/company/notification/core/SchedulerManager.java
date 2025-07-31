package com.company.notification.core;

import com.company.notification.model.publisher.Publisher;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class SchedulerManager {
    private final EventBus eventBus;
    private final Map<Publisher, EventScheduler> schedulerMap;
    private static final Logger logger = Logger.getLogger(SchedulerManager.class.getName());

    public SchedulerManager(EventBus eventBus) {
        this.eventBus = eventBus;
        this.schedulerMap = new ConcurrentHashMap<>();
    }

    public void registerScheduler(Publisher publisher, long intervalSeconds) {


        // Step 1: Register the publisher first (safe even if already registered)
        eventBus.registerPublisher(publisher);

        // Step 2: Create and start the scheduler
        EventScheduler scheduler = new EventScheduler(eventBus, publisher, intervalSeconds);
        scheduler.start();

        // Step 3: Only put if it's running
        if (!scheduler.isShutdown()) {
            EventScheduler existing = schedulerMap.putIfAbsent(publisher, scheduler);

            if (existing == null) {
                logger.info("Scheduler registered for " + publisher.getName());
            } else {
                // A scheduler was already registered concurrently
                logger.info("Scheduler already exists for " + publisher.getName() + ". Stopping the newly created one.");
                scheduler.shutdown();
            }

        } else {
            logger.warning("Scheduler not started for " + publisher.getName() + " (no subscribers).");
        }
    }

    public void shutdownScheduler(Publisher publisher) {
        EventScheduler scheduler = schedulerMap.remove(publisher);
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }

    public void shutdownAllSchedulers() {
        schedulerMap.forEach((publisher, scheduler) -> scheduler.shutdown());
        System.out.println("Scheduler shutdown for all publishers");
        schedulerMap.clear();
    }



    public void removeScheduler(Publisher publisher) {
        EventScheduler scheduler = schedulerMap.remove(publisher);
        if (scheduler != null) {
            scheduler.shutdown();
            logger.info("Scheduler removed for publisher: " + publisher.getName());
        }
    }

    // method to check whether there is a scheduler or not for the publisher
    public boolean hasScheduler(Publisher publisher) {
        return schedulerMap.containsKey(publisher);
    }






}