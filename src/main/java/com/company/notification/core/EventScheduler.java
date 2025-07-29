package com.company.notification.core;

import com.company.notification.event.HeartBeatEvent;
import com.company.notification.event.Priority;
import com.company.notification.model.publisher.Publisher;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EventScheduler {
private final EventBus eventBus ;
private final Publisher publisher;
private final long intervalSeconds;
private ScheduledExecutorService scheduler;


    public EventScheduler(EventBus eventBus, Publisher publisher, long intervalSeconds) {
      //  null check
        if (eventBus == null) {
            throw new IllegalArgumentException("Event bus cannot be null");
        }
        if (publisher == null) {
            throw new IllegalArgumentException("Publisher cannot be null");
        }
        if (intervalSeconds <= 0) {
            throw new IllegalArgumentException("Interval seconds must be greater than 0");
        }
        this.eventBus = eventBus;
        this.publisher = publisher;
        this.intervalSeconds = intervalSeconds;
    }



/* <<<<<<<<<<<<<<  ✨ Windsurf Command ⭐ >>>>>>>>>>>>>>>> */
    /**
     * Start the heartbeat scheduler.
     * <p>
     * This method will schedule a single-threaded executor to send heartbeats to the
     * event bus at the specified interval. The executor will be daemonize and will not
     * prevent the JVM from exiting.
     */
    public void start() {
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            t.setName("HeartbeatScheduler-" + publisher.getName());
            return t;
        });

        scheduler.scheduleAtFixedRate(() -> {

            HeartBeatEvent heartBeatEvent = new HeartBeatEvent(publisher.getId(), Priority.MEDIUM);
            System.out.println("🕒 [" + Thread.currentThread().getName() + "] Heartbeat #" +
                    " from " + publisher.getName() + " at " + LocalDateTime.now());
            eventBus.publishFromPublisher(publisher, heartBeatEvent);
        }, 0, intervalSeconds, TimeUnit.SECONDS);
    }


    public  void shutdown() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
            System.out.println("Scheduler shutdown for " + publisher.getName());
        }




    }


}
