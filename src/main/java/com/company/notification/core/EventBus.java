package com.company.notification.core;

import com.company.notification.event.Event;
import com.company.notification.filters.EventFilter;
import com.company.notification.model.publisher.Publisher;
import com.company.notification.model.subscriber.AdminSubscriber;
import com.company.notification.model.subscriber.Subscriber;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EventBus {

    // Maps Publisher to their Subscribers
    private final Map<Publisher, Set<Subscriber>> publisherSubscriberMap = new ConcurrentHashMap<>();

    // Maps Subscriber to the Publishers they follow
    private final Map<Subscriber, Set<Publisher>> subscriberPublisherMap = new ConcurrentHashMap<>();

    // Maps Subscriber to its assigned filter
    private final Map<Subscriber, EventFilter> subscriberFilterMap = new ConcurrentHashMap<>();

    // Set of admin subscribers who receive all events
    private final Set<Subscriber> adminSubscribers = ConcurrentHashMap.newKeySet();

    private final Subscriber dummyAdmin;
    private final EventHistory eventHistory ;


    public EventBus(EventHistory eventHistory) {
        this.eventHistory = eventHistory;
        // Automatically register dummy admin
        dummyAdmin = new AdminSubscriber("DummyAdmin", event -> true); // Accept all events
        adminSubscribers.add(dummyAdmin);
        subscriberFilterMap.put(dummyAdmin, dummyAdmin.getFilter());
        System.out.println(" SystemAdmin (default admin) registered.");
    }

    public Subscriber getDummyAdmin() {
        return dummyAdmin;
    }


    /**
     * Register a new publisher in the system
     */
    public void registerPublisher(Publisher publisher) {
        if (publisher == null) {
            throw new IllegalArgumentException("Publisher cannot be null");
        }
        publisherSubscriberMap.putIfAbsent(publisher, ConcurrentHashMap.newKeySet());
    }



    /**
     * Register an admin subscriber who should receive all events
     */
    public void registerAdminSubscriber(Subscriber admin, EventFilter filter) {
        if (admin == null || filter == null) {
            throw new IllegalArgumentException("Admin subscriber or filter cannot be null");
        }
        adminSubscribers.add(admin);
        subscriberFilterMap.put(admin, filter);
    }





    /**
     * Subscribe a subscriber to a publisher with a filter
     */
    public void subscribe(Subscriber subscriber, Publisher publisher, EventFilter filter) {
        if (subscriber == null) {
            throw new IllegalArgumentException("Subscriber cannot be null");
        }
        if (publisher == null) {
            throw new IllegalArgumentException("Publisher cannot be null");
        }
        if (filter == null) {
            throw new IllegalArgumentException("Filter cannot be null");
        }

        publisherSubscriberMap
                .computeIfAbsent(publisher, k -> ConcurrentHashMap.newKeySet())
                .add(subscriber);

        subscriberPublisherMap
                .computeIfAbsent(subscriber, k -> ConcurrentHashMap.newKeySet())
                .add(publisher);

        subscriberFilterMap.put(subscriber, filter);
    }











    /**
     * Unsubscribe a subscriber from a publisher
     */
    public void unsubscribe(Subscriber subscriber, Publisher publisher) {
        if (subscriber == null || publisher == null) {
            throw new IllegalArgumentException("Subscriber or Publisher cannot be null");
        }

        Set<Subscriber> subscribers = publisherSubscriberMap.get(publisher);
        if (subscribers != null) {
            subscribers.remove(subscriber);
        }

        subscriberPublisherMap.computeIfPresent(subscriber, (sub, pubs) -> {
            pubs.remove(publisher);
            if (pubs.isEmpty() && !adminSubscribers.contains(sub)) {
                subscriberFilterMap.remove(sub);
                return null; // Clean up the subscriber entry
            }
            return pubs;
        });
    }









    /**
     * Publish an event from a publisher to all its subscribers and all admins
     */
    public void publishFromPublisher(Publisher publisher, Event event) {
        if (publisher == null) {
            throw new IllegalArgumentException("Publisher cannot be null");
        }
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }


        // Send to regular subscribers (copy to avoid concurrent modification)
        Set<Subscriber> subscribers = new HashSet<>(publisherSubscriberMap.getOrDefault(publisher, Set.of()));
        for (Subscriber subscriber : subscribers) {
            EventFilter filter = subscriberFilterMap.getOrDefault(subscriber, e -> true);
            if (filter.shouldProcess(event)) {
                subscriber.enqueue(event);
            }
        }

        // Defensive copy of adminSubscribers set
        for (Subscriber admin : new HashSet<>(adminSubscribers)) {
            EventFilter filter = subscriberFilterMap.get(admin);
            if (filter == null || filter.shouldProcess(event)) {
                admin.enqueue(event);
            }
        }

        // Log the event
        eventHistory.logEvent(event,publisher);
    }




}