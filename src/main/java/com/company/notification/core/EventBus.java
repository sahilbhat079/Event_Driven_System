package com.company.notification.core;

import com.company.notification.event.Event;
import com.company.notification.filters.EventFilter;
import com.company.notification.model.publisher.Publisher;
import com.company.notification.model.subscriber.AdminSubscriber;
import com.company.notification.model.subscriber.Subscriber;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public class EventBus {

    private static final Logger logger = Logger.getLogger(EventBus.class.getName());

    private final Map<Publisher, Set<Subscriber>> publisherSubscriberMap = new ConcurrentHashMap<>();
    private final Map<Subscriber, Set<Publisher>> subscriberPublisherMap = new ConcurrentHashMap<>();
    private final Map<Subscriber, AtomicReference<EventFilter>> subscriberFilterMap = new ConcurrentHashMap<>();
    private final Set<Subscriber> adminSubscribers = ConcurrentHashMap.newKeySet();

    private final AdminSubscriber dummyAdmin;
    private final EventHistory eventHistory;

    public EventBus(EventHistory eventHistory) {
        this.eventHistory = eventHistory;
        this.dummyAdmin = new AdminSubscriber("DummyAdmin", event -> true); // Accept all events
        adminSubscribers.add(dummyAdmin);
        subscriberFilterMap.put(dummyAdmin, new AtomicReference<>(dummyAdmin.getFilter()));
        logger.info("SystemAdmin (DummyAdmin) registered.");
    }

    public AdminSubscriber getDummyAdmin() {
        return dummyAdmin;
    }

    public void registerPublisher(Publisher publisher) {
        if (publisher == null) throw new IllegalArgumentException("Publisher cannot be null");
        publisherSubscriberMap.putIfAbsent(publisher, ConcurrentHashMap.newKeySet());
        logger.info("Publisher registered: " + publisher.getName());
    }

    public void registerAdminSubscriber(Subscriber admin, EventFilter filter) {
        if (admin == null || filter == null)
            throw new IllegalArgumentException("Admin subscriber or filter cannot be null");
        adminSubscribers.add(admin);
        subscriberFilterMap.put(admin, new AtomicReference<>(filter));
        logger.info("Admin subscriber registered: " + admin.getName());
    }

    public void subscribe(Subscriber subscriber, Publisher publisher, EventFilter filter) {
        if (subscriber == null || publisher == null || filter == null)
            throw new IllegalArgumentException("Subscriber, Publisher, or Filter cannot be null");

        publisherSubscriberMap
                .computeIfAbsent(publisher, k -> ConcurrentHashMap.newKeySet())
                .add(subscriber);

        subscriberPublisherMap
                .computeIfAbsent(subscriber, k -> ConcurrentHashMap.newKeySet())
                .add(publisher);

        subscriberFilterMap.putIfAbsent(subscriber, new AtomicReference<>(filter));

        logger.info(subscriber.getName() + " subscribed to " + publisher.getName());
    }

    public void unsubscribe(Subscriber subscriber, Publisher publisher) {
        if (subscriber == null || publisher == null)
            throw new IllegalArgumentException("Subscriber or Publisher cannot be null");

        publisherSubscriberMap.computeIfPresent(publisher, (pub, subs) -> {
            subs.remove(subscriber);
            return subs.isEmpty() ? null : subs;
        });

        subscriberPublisherMap.computeIfPresent(subscriber, (sub, pubs) -> {
            pubs.remove(publisher);
            if (pubs.isEmpty() && !adminSubscribers.contains(sub)) {
                subscriberFilterMap.remove(sub);
                logger.info("Subscriber removed completely: " + sub.getName());
                return null;
            }
            return pubs;
        });

        logger.info(subscriber.getName() + " unsubscribed from " + publisher.getName());
    }

    public void publishFromPublisher(Publisher publisher, Event event) {
        if (publisher == null) throw new IllegalArgumentException("Publisher cannot be null");
        if (event == null) throw new IllegalArgumentException("Event cannot be null");

        // Deliver to regular subscribers
        Set<Subscriber> subscribers = new HashSet<>(publisherSubscriberMap.getOrDefault(publisher, Set.of()));
        for (Subscriber subscriber : subscribers) {
            EventFilter filter = Optional.ofNullable(subscriberFilterMap.get(subscriber))
                    .map(AtomicReference::get)
                    .orElse(null);
            if (filter != null && filter.shouldProcess(event)) {
                subscriber.enqueue(event);
                logger.info(subscriber.getName() + " received event: " + event);
            }
        }

        // Deliver to admins
        for (Subscriber admin : new HashSet<>(adminSubscribers)) {
            EventFilter filter = Optional.ofNullable(subscriberFilterMap.get(admin))
                    .map(AtomicReference::get)
                    .orElse(null);
            if (filter == null || filter.shouldProcess(event)) {
                admin.enqueue(event);
            }
        }


        try {
            eventHistory.logEvent(event, publisher);
        } catch (Exception e) {
            logger.warning("Failed to log event: " + e.getMessage());
        }
    }

    public Set<Publisher> getPublishersForSubscriber(Subscriber subscriber) {
        if (subscriber == null) throw new IllegalArgumentException("Subscriber cannot be null");
        return new HashSet<>(subscriberPublisherMap.getOrDefault(subscriber, Set.of()));
    }

    public Set<Publisher> getAllPublishers() {
        return new HashSet<>(publisherSubscriberMap.keySet());
    }

    public boolean hasSubscribers(Publisher publisher) {
        if (publisher == null) return false;
        return Optional.ofNullable(publisherSubscriberMap.get(publisher))
                .map(subs -> !subs.isEmpty())
                .orElse(false);
    }

    public Set<Subscriber> getSubscribers(Publisher publisher) {
        if (publisher == null) return Set.of();
        return new HashSet<>(publisherSubscriberMap.getOrDefault(publisher, Set.of()));
    }

    public Optional<String> getPublisherName(String publisherId) {
        if (publisherId == null || publisherId.isBlank()) return Optional.empty();
        return publisherSubscriberMap.keySet().stream()
                .filter(p -> publisherId.equals(p.getId()))
                .map(Publisher::getName)
                .findFirst();
    }

    public void updateFilter(Subscriber subscriber, EventFilter newFilter) {
        if (subscriber == null) {
            throw new IllegalArgumentException("Subscriber cannot be null");
        }
        if (newFilter == null) {
            throw new IllegalArgumentException("EventFilter cannot be null");
        }

        subscriberFilterMap.compute(subscriber, (key, existingRef) -> {
            if (existingRef == null) {
                return new AtomicReference<>(newFilter);
            } else {
                existingRef.set(newFilter);
                return existingRef;
            }
        });
    }
}
