package com.company.notification.core;
 import com.company.notification.event.Event;
 import com.company.notification.filters.EventFilter;
 import com.company.notification.model.publisher.Publisher;
 import com.company.notification.model.subscriber.Subscriber;

 import java.util.*;
public class EventBus {
   //maps Publisher to their subscribers
    private final Map<Publisher,Set<Subscriber>> publisherSubscriberMap = new HashMap<>();
   //maps Subscriber to their publishers
    private final Map<Subscriber,Set<Publisher>> subscriberPublisherMap = new HashMap<>();

    //maps subscriber to their filters
    private final Map<Subscriber, EventFilter> subscriberFilterMap = new HashMap<>();


    /* Register a publisher in the system */
    public void registerPublisher(Publisher publisher) {
        //null check
        if (publisher == null) {
            throw new IllegalArgumentException("Publisher cannot be null");
        }
        publisherSubscriberMap.putIfAbsent(publisher, new HashSet<>());
    }

    //subscribe a subscriber to a publisher with a filter
    public void subscribe(Subscriber subscriber, Publisher publisher, EventFilter filter) {
       // null check
        if (subscriber == null) {
            throw new IllegalArgumentException("Subscriber cannot be null");
        }
        if (publisher == null) {
            throw new IllegalArgumentException("Publisher cannot be null");
        }
        if (filter == null) {
            throw new IllegalArgumentException("Filter cannot be null");
        }
        publisherSubscriberMap.computeIfAbsent(publisher, k -> new HashSet<>()).add(subscriber);
        subscriberPublisherMap.computeIfAbsent(subscriber, k -> new HashSet<>()).add(publisher);
        subscriberFilterMap.put(subscriber, filter);
    }




    //unsubscribe a subscriber from a publisher
    public void unsubscribe(Subscriber subscriber, Publisher publisher) {
        //null check
        if (subscriber == null) {
            throw new IllegalArgumentException("Subscriber cannot be null");
        }
        if (publisher == null) {
            throw new IllegalArgumentException("Publisher cannot be null");
        }
        Set<Subscriber> subscribers = publisherSubscriberMap.get(publisher);
        if (subscribers != null) {
            subscribers.remove(subscriber);
        }
        Set<Publisher> publishers = subscriberPublisherMap.get(subscriber);
        if (publishers != null) {
            publishers.remove(publisher);
        }
        subscriberFilterMap.remove(subscriber);

        //if there are no subscriptions left remove the filter
        if(subscriberPublisherMap.getOrDefault(subscriber,Set.of()).isEmpty()) {
            subscriberFilterMap.remove(subscriber);
        }

    }




    public void publishFromPublisher(Publisher publisher, Event event) {
        //null check
        if (publisher == null) {
            throw new IllegalArgumentException("Publisher cannot be null");
        }
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }
        Set<Subscriber> subscribers = publisherSubscriberMap.getOrDefault(publisher, Set.of());

        for(Subscriber subscriber : subscribers) {
            EventFilter filter = subscriber.getFilter();
            if(filter == null || filter.shouldProcess(event)) {
                subscriber.enqueue(event);
            }
        }







    }



}
