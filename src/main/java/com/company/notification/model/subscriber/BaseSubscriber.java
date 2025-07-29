package com.company.notification.model.subscriber;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public abstract class BaseSubscriber {
    protected final UUID id;
    protected String name;
    protected final LocalDateTime createdAt;

    protected BaseSubscriber(String name) {
        //null check
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Subscriber name cannot be null or empty");
        }
        this.id = UUID.randomUUID();
        this.name = name;
        this.createdAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setName(String name) {
        this.name = name;
    }

    // equals and hashCode based on ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseSubscriber that)) return false;

        return id.equals(that.id) && name.equals(that.name) && createdAt.equals(that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, createdAt);
    }

    @Override
    public String toString() {
        return "BaseSubscriber{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}