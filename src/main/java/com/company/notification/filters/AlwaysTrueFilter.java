package com.company.notification.filters;

import com.company.notification.event.Event;

public class AlwaysTrueFilter implements EventFilter{
    @Override
    public boolean shouldProcess(Event event) {
        return true;
    }
}
