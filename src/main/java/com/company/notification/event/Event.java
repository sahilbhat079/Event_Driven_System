package com.company.notification.event;


import java.time.LocalDateTime;

public interface Event {
    EventTypes getType();
    LocalDateTime getDateTime();
    String getSourcePublisherId();

  default Priority getPriority() {
      return Priority.MEDIUM;
  }
}
