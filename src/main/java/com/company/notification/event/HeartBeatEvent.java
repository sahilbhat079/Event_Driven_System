package com.company.notification.event;

import java.time.LocalDateTime;

public class HeartBeatEvent implements Event {
    private final LocalDateTime timeStamp;
    private final String publisherId;

    public HeartBeatEvent(String publisherId) {
        this.timeStamp = LocalDateTime.now();
        this.publisherId = publisherId;
    }


    @Override
    public EventTypes getType() {
        return EventTypes.HEARTBEAT;
    }

    @Override
    public LocalDateTime getDateTime() {
        return timeStamp;
    }

    @Override
    public String getSourcePublisherId() {
        return publisherId;
    }

    @Override
    public Priority getPriority() {
        return Priority.LOW;
    }


}
