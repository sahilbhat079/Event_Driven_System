package com.company.notification.utils;

import com.company.notification.event.Event;

import java.util.Comparator;

public class EventComparator implements Comparator<Event> {

    @Override
    public int compare(Event e1, Event e2) {
        return e1.getPriority().compareTo(e2.getPriority());
    }
}
