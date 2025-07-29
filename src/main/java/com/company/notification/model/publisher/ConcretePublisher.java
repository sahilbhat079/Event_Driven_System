package com.company.notification.model.publisher;

import com.company.notification.event.Event;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class ConcretePublisher implements Publisher {
    private final String name;
    private final String id;
    private final LocalDateTime createdAt;

    public ConcretePublisher(String name) {
        //null check
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Publisher name cannot be null or empty");
        }
        this.name = name;
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
    }


    @Override
    public void publish(Event event) {

    }

    @Override
    public String getName() {
        return "ConcretePublisher";
    }

    @Override
    public String getId() {
        return id;
    }


    @Override
    public String toString() {
        return "ConcretePublisher{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", createdAt=" + createdAt +
                '}';

    }


    @Override
    public int hashCode() {
        return Objects.hash(name, id, createdAt);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ConcretePublisher that)) return false;
        return name.equals(that.name) && id.equals(that.id) && createdAt.equals(that.createdAt);
    }
}
