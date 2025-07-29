package com.company.notification.filters;

import com.company.notification.event.Event;

import java.time.LocalTime;

public class TimeWindowFilter implements EventFilter {
   private final LocalTime startTime;
   private final LocalTime endTime;

    public TimeWindowFilter(LocalTime startTime, LocalTime endTime) {
     //null check
        if (startTime == null) {
            throw new IllegalArgumentException("Start time cannot be null");
        }
        if (endTime == null) {
            throw new IllegalArgumentException("End time cannot be null");
        }
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public boolean shouldProcess(Event event) {
        LocalTime eventTime = event.getDateTime().toLocalTime();
        return !eventTime.isBefore(startTime) && !eventTime.isAfter(endTime);
    }

}
