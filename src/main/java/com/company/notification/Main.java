package com.company.notification;

import com.company.notification.core.EventBus;
import com.company.notification.core.SchedulerManager;
import com.company.notification.event.Priority;
import com.company.notification.event.PriorityEvent;
import com.company.notification.event.TaskEvent;
import com.company.notification.filters.AlwaysTrueFilter;
import com.company.notification.filters.EventFilter;
import com.company.notification.filters.PriorityFilter;
import com.company.notification.filters.TimeWindowFilter;
import com.company.notification.model.publisher.ConcretePublisher;
import com.company.notification.model.publisher.Publisher;
import com.company.notification.model.subscriber.AdminSubscriber;
import com.company.notification.model.subscriber.Subscriber;
import com.company.notification.model.subscriber.UserSubscriber;

import java.time.LocalTime;
public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println(" Starting Event-Driven Notification System...");

        // 1. Core setup
        EventBus eventBus = new EventBus();
        SchedulerManager schedulerManager = new SchedulerManager(eventBus);
        Publisher publisher = new ConcretePublisher("SystemPublisher");
        eventBus.registerPublisher(publisher);

        // 2. Filters
        PriorityFilter highPriorityFilter = new PriorityFilter(Priority.HIGH);
        PriorityFilter lowPriorityFilter = new PriorityFilter(Priority.LOW);
        AlwaysTrueFilter alwaysTrueFilter = new AlwaysTrueFilter();

        LocalTime now = LocalTime.now();
        TimeWindowFilter timeWindowFilter = new TimeWindowFilter(now.minusSeconds(5), now.plusSeconds(10));

        // 3. Subscribers
        Subscriber highUser = new UserSubscriber("Alice-HIGH", highPriorityFilter);
        Subscriber lowUser = new UserSubscriber("Charlie-LOW", lowPriorityFilter);
        Subscriber timeWindowUser = new UserSubscriber("Time-Window", timeWindowFilter);
        Subscriber admin = new AdminSubscriber("Admin", alwaysTrueFilter);
        eventBus.registerAdminSubscriber(admin, alwaysTrueFilter);

        // 4. Subscriptions
        eventBus.subscribe(highUser, publisher, highUser.getFilter());
        eventBus.subscribe(lowUser, publisher, lowUser.getFilter());
        eventBus.subscribe(timeWindowUser, publisher, timeWindowUser.getFilter());

        // 5. Scheduler (heartbeat)
        schedulerManager.registerScheduler(publisher, 2); // Every 2 seconds

        // 6. Publish task events
        sendTaskEvent(eventBus, publisher, "Critical Fix", "Memory bug", Priority.HIGH);
        Thread.sleep(500);
        sendTaskEvent(eventBus, publisher, "UI Update", "Dark mode", Priority.LOW);
        Thread.sleep(500);
        sendTaskEvent(eventBus, publisher, "Retry Setup", "Resend payment", Priority.MEDIUM);
        Thread.sleep(500);

        // 7. Publish a PriorityEvent
        PriorityEvent pEvent = new PriorityEvent("System Alert", Priority.HIGH, "Database down", publisher.getId());
        System.out.println("➡ Publishing PriorityEvent: " + pEvent.getMessage());
        eventBus.publishFromPublisher(publisher, pEvent);

        // Wait to allow heartbeat events
        Thread.sleep(6000);

        // 8. Process all queues
        System.out.println("\n Processing queues...");
        highUser.processQueue();
        lowUser.processQueue();
        timeWindowUser.processQueue();
        admin.processQueue();

        // 9. Shutdown
        schedulerManager.shutdownAllSchedulers();
        System.out.println("\n All schedulers stopped. Test complete.");
    }

    private static void sendTaskEvent(EventBus eventBus, Publisher publisher, String name, String desc, Priority priority) {
        TaskEvent event = new TaskEvent(name, desc, publisher.getId(), priority);
        System.out.println("➡ Publishing TaskEvent: " + name + " [Priority: " + priority + "]");
        eventBus.publishFromPublisher(publisher, event);
    }
    public static void  test(){
        System.out.println("🔔 Starting Event-Driven Notification System...");

        // 1. Core Setup
        EventBus eventBus = new EventBus();
        SchedulerManager schedulerManager = new SchedulerManager(eventBus);

        Publisher publisher = new ConcretePublisher("SystemPublisher");
        eventBus.registerPublisher(publisher);

        // 2. Admin Setup (receives all events including heartbeat)
        EventFilter allowAll = event -> true;
        Subscriber admin = new AdminSubscriber("SuperAdmin", allowAll);
        eventBus.registerAdminSubscriber(admin, allowAll);

        // 3. Start Scheduler (heartbeat every 2 seconds)
        schedulerManager.registerScheduler(publisher, 2); // 2 seconds

        // 4. Let the system run for a while and process periodically
        int runtimeSeconds = 20; // run for 20 seconds
        for (int i = 1; i <= runtimeSeconds; i++) {
            try {
                Thread.sleep(1000); // 1 second sleep
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (i % 5 == 0) {
                System.out.println("\n⏳ Time: " + i + " seconds elapsed. Processing admin queue...");
                admin.processQueue();
            }
        }

        // 5. Shutdown
        System.out.println("\n🛑 Stopping all schedulers...");
        schedulerManager.shutdownAllSchedulers();
        System.out.println("✅ System terminated.");
    }



}