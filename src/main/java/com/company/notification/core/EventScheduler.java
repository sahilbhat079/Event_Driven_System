package com.company.notification.core;

import com.company.notification.event.HeartBeatEvent;
import com.company.notification.event.Priority;
import com.company.notification.model.publisher.Publisher;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class EventScheduler {
private final EventBus eventBus ;
private final Publisher publisher;
private final long intervalSeconds;
private ScheduledExecutorService scheduler;
    private static final Logger logger = Logger.getLogger(EventScheduler.class.getName());

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

        if (scheduler != null && !scheduler.isShutdown()) {
            logger.info("Scheduler already started for " + publisher.getName());
            return;
        }

        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            t.setName("HeartbeatScheduler-" + publisher.getName());
            return t;
        });

        scheduler.scheduleAtFixedRate(() -> {
            if (!eventBus.hasSubscribers(publisher)) {
                logger.warning("No subscribers remaining. Shutting down scheduler for " + publisher.getName());
                shutdown();
                return;
            }

            HeartBeatEvent heartBeatEvent = new HeartBeatEvent(publisher.getId(), Priority.MEDIUM);
            eventBus.publishFromPublisher(publisher, heartBeatEvent);

        }, 0, intervalSeconds, TimeUnit.SECONDS);
//        logger.info("Heartbeat scheduler started for publisher: " + publisher.getName());

    }


    public  void shutdown() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
            logger.info("Scheduler shutdown for " + publisher.getName());
        }




    }



    public boolean isShutdown() {
        return scheduler == null || scheduler.isShutdown();
    }

}
