package com.company.notification.core;

import com.company.notification.event.Event;
import com.company.notification.model.publisher.Publisher;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class EventHistory {

    private final List<EventRecord> history = new CopyOnWriteArrayList<>();

    // Immutable record for storage
    public static final class EventRecord {
        private final Event event;
        private final Instant timestamp;
        private final String publisherId;

        public EventRecord(Event event, String publisherId) {
            if (event == null || publisherId == null) {
                throw new IllegalArgumentException("Event and publisherId cannot be null");
            }
            this.event = event;
            this.timestamp = Instant.now();
            this.publisherId = publisherId;
        }

        public Event getEvent() {
            return event;
        }

        public Instant getTimestamp() {
            return timestamp;
        }

        public String getPublisherId() {
            return publisherId;
        }

        @Override
        public String toString() {
            return "[" + timestamp + "] from " + publisherId + " -> " + event;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof EventRecord)) return false;
            EventRecord that = (EventRecord) o;
            return Objects.equals(event, that.event)
                    && Objects.equals(timestamp, that.timestamp)
                    && Objects.equals(publisherId, that.publisherId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(event, timestamp, publisherId);
        }
    }

    public void logEvent(Event event, Publisher publisher) {
        if (event == null || publisher == null) {
            throw new IllegalArgumentException("Event and Publisher cannot be null");
        }
        history.add(new EventRecord(event, publisher.getId()));
    }

    public List<EventRecord> getAllEvents() {
        return new ArrayList<>(history); // Defensive copy
    }

    public List<EventRecord> getEventsByType(String type) {
        if (type == null) return List.of();
        return history.stream()
                .filter(record -> type.equalsIgnoreCase(record.getEvent().getType().name()))
                .collect(Collectors.toList());
    }

    public List<EventRecord> getEventsByPublisher(String publisherId) {
        if (publisherId == null) return List.of();
        return history.stream()
                .filter(record -> publisherId.equalsIgnoreCase(record.getPublisherId()))
                .collect(Collectors.toList());
    }

    public List<EventRecord> getEventsAfter(Instant timestamp) {
        if (timestamp == null) return List.of();
        return history.stream()
                .filter(record -> record.getTimestamp().isAfter(timestamp))
                .collect(Collectors.toList());
    }

    public Map<String, Long> countEventsByType() {
        return history.stream()
                .collect(Collectors.groupingBy(
                        record -> record.getEvent().getType().name(),
                        Collectors.counting()));
    }

    public void pruneBefore(Instant cutoff) {
        if (cutoff == null) return;
        history.removeIf(record -> record.getTimestamp().isBefore(cutoff));
    }
    //  NEW: Get events from the last hour
    public List<EventRecord> getEventsInLastHour() {
        Instant oneHourAgo = Instant.now().minusSeconds(3600);
        return history.stream()
                .filter(record -> record.getTimestamp().isAfter(oneHourAgo))
                .collect(Collectors.toList());
    }



    public List<EventRecord> getEventsBetween(Instant start, Instant end) {
        if (start == null || end == null) return List.of();
        return history.stream()
                .filter(record -> !record.getTimestamp().isBefore(start) && !record.getTimestamp().isAfter(end))
                .collect(Collectors.toList());
    }

        public void clear() {
        history.clear();
    }
}
