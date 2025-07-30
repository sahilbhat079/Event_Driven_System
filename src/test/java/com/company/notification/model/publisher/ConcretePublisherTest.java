package com.company.notification.model.publisher;

import com.company.notification.core.EventBus;
import com.company.notification.event.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConcretePublisherTest {

    private EventBus mockEventBus;
    private Event mockEvent;

    @BeforeEach
    void setup() {
        mockEventBus = mock(EventBus.class);
        mockEvent = mock(Event.class);
    }

    @Test
    void constructor_shouldThrowException_whenNameIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new ConcretePublisher(null));
    }

    @Test
    void constructor_shouldThrowException_whenNameIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> new ConcretePublisher(""));
    }

    @Test
    void constructor_shouldCreateValidPublisher_whenNameIsGiven() {
        ConcretePublisher publisher = new ConcretePublisher("Publisher1");
        assertNotNull(publisher.getName());
        assertNotNull(publisher.getId());
        assertEquals("Publisher1", publisher.getName());
    }

    @Test
    void publish_shouldThrowException_whenEventBusIsNull() {
        ConcretePublisher publisher = new ConcretePublisher("P1");
        assertThrows(IllegalArgumentException.class, () -> publisher.publish(null, mockEvent));
    }

    @Test
    void publish_shouldThrowException_whenEventIsNull() {
        ConcretePublisher publisher = new ConcretePublisher("P1");
        assertThrows(IllegalArgumentException.class, () -> publisher.publish(mockEventBus, null));
    }

    @Test
    void publish_shouldDelegateToEventBus() {
        ConcretePublisher publisher = new ConcretePublisher("PublisherA");
        publisher.publish(mockEventBus, mockEvent);
        verify(mockEventBus, times(1)).publishFromPublisher(publisher, mockEvent);
    }

    @Test
    void testEquals_shouldReturnFalseForDifferentIdEvenWithSameName() {
        ConcretePublisher p1 = new ConcretePublisher("SameName");
        ConcretePublisher p2 = new ConcretePublisher("SameName");

        assertNotEquals(p1, p2); // IDs are different
    }

    @Test
    void testHashCode_shouldBeConsistentForSameObject() {
        ConcretePublisher publisher = new ConcretePublisher("P1");
        int hash1 = publisher.hashCode();
        int hash2 = publisher.hashCode();

        assertEquals(hash1, hash2);
    }

    @Test
    void testToString_shouldContainNameAndId() {
        ConcretePublisher publisher = new ConcretePublisher("ToStringTest");
        String result = publisher.toString();

        assertTrue(result.contains("ToStringTest"));
        assertTrue(result.contains(publisher.getId()));
    }

    @Test
    void testEqualsAndHashCode_shouldWorkInHashSet() {
        ConcretePublisher publisher1 = new ConcretePublisher("X");
        ConcretePublisher publisher2 = new ConcretePublisher("X");

        HashSet<ConcretePublisher> set = new HashSet<>();
        set.add(publisher1);
        set.add(publisher2); // Should be added because UUIDs differ

        assertEquals(2, set.size());
    }
}
