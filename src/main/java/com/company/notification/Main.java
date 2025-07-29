package com.company.notification;

import com.company.notification.core.EventBus;
import com.company.notification.core.SchedulerManager;
import com.company.notification.event.Priority;
import com.company.notification.event.TaskEvent;
import com.company.notification.filters.EventFilter;
import com.company.notification.filters.PriorityFilter;
import com.company.notification.model.publisher.ConcretePublisher;
import com.company.notification.model.publisher.Publisher;
import com.company.notification.model.subscriber.AdminSubscriber;
import com.company.notification.model.subscriber.Subscriber;
import com.company.notification.model.subscriber.UserSubscriber;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws InterruptedException {

        System.out.println(" Starting Event-Driven Notification System...");

        // 1. Create core components
        EventBus eventBus = new EventBus();
        SchedulerManager schedulerManager = new SchedulerManager(eventBus);
        Publisher publisher = new ConcretePublisher("SystemPublisher");
        eventBus.registerPublisher(publisher);

        // 2. Create subscribers with different priority filters
        Subscriber highSub = new UserSubscriber("Alice-HIGH", new PriorityFilter(Priority.HIGH));
        Subscriber mediumSub = new UserSubscriber("Bob-MEDIUM", new PriorityFilter(Priority.MEDIUM));
        Subscriber lowSub = new UserSubscriber("Charlie-LOW", new PriorityFilter(Priority.LOW));

        // 3. Subscribe them
        eventBus.subscribe(highSub, publisher, highSub.getFilter());
        eventBus.subscribe(mediumSub, publisher, mediumSub.getFilter());
        eventBus.subscribe(lowSub, publisher, lowSub.getFilter());

        // 4. Send multiple TaskEvents from publisher
        System.out.println("\n Sending TaskEvents...");

        sendEvent(publisher, eventBus, "Fix Critical Bug", "Memory leak issue", Priority.HIGH);
        Thread.sleep(500);
        sendEvent(publisher, eventBus, "Feature Update", "UI enhancements", Priority.MEDIUM);
        Thread.sleep(500);
        sendEvent(publisher, eventBus, "Log Cleanup", "Remove old logs", Priority.LOW);
        Thread.sleep(500);
        sendEvent(publisher, eventBus, "Payment Failure", "Retry mechanism", Priority.HIGH);
        Thread.sleep(500);
        sendEvent(publisher, eventBus, "Optimize Query", "Improve DB performance", Priority.MEDIUM);

        // 5. Let each subscriber process their queue
        System.out.println("\n Processing all subscriber queues:\n");

        highSub.processQueue();
        mediumSub.processQueue();
        lowSub.processQueue();

        // Use a filter that accepts all events
        EventFilter allowAll = event -> true;

        Subscriber admin = new AdminSubscriber("SystemAdmin", allowAll);
        eventBus.subscribe(admin, publisher, admin.getFilter());

// Wait some time to allow heartbeat or tasks to be delivered
        Thread.sleep(5000);

        admin.processQueue();





        // 6. Shutdown any scheduled processes if added
        schedulerManager.shutdownAllSchedulers();

        System.out.println("\n Test completed.");
    }

    private static void sendEvent(Publisher publisher, EventBus eventBus, String title, String desc, Priority priority) {
        TaskEvent event = new TaskEvent(title, desc, publisher.getId(), priority);
        System.out.println("➡ Publishing: " + title + " [Priority: " + priority + "]");
        eventBus.publishFromPublisher(publisher, event);
    }





}