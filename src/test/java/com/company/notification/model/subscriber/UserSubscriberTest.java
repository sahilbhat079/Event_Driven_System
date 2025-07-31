package com.company.notification.model.subscriber;

import com.company.notification.event.Event;
import com.company.notification.event.Priority;
import com.company.notification.event.TaskEvent;
import com.company.notification.filters.EventFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.PriorityQueue;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;

class UserSubscriberTest {

    private TaskEvent sampleEvent;

    @BeforeEach
    void setup() {
        sampleEvent = new TaskEvent(
                "Refactor Code",
                "Improve system performance",
                "pub-001",
                Priority.HIGH
        );
    }

    @Test
    void constructorShouldInitializeQueueAndFilterProperly() throws Exception {
        EventFilter filter = event -> true;
        UserSubscriber subscriber = new UserSubscriber("DevUser", filter);

        // Reflection check for queue
        Field queueField = UserSubscriber.class.getDeclaredField("queue");
        queueField.setAccessible(true);
        Object queueObj = queueField.get(subscriber);
        assertNotNull(queueObj);
        assertTrue(queueObj instanceof PriorityQueue);

        // Check filter
        assertEquals(filter, subscriber.getFilter());
    }

    @Test
    void enqueueShouldIgnoreNullEvent() {
        EventFilter filter = event -> true;
        UserSubscriber subscriber = new UserSubscriber("NullHandler", filter);

        assertDoesNotThrow(() -> subscriber.enqueue(null));
    }

    @Test
    void enqueueShouldAddEventWhenFilterIsTrue() throws Exception {
        EventFilter filter = event -> true;
        UserSubscriber subscriber = new UserSubscriber("QueueAdder", filter);

        subscriber.enqueue(sampleEvent);

        Field queueField = UserSubscriber.class.getDeclaredField("queue");
        queueField.setAccessible(true);
        Queue<Event> queue = (Queue<Event>) queueField.get(subscriber);

        assertEquals(1, queue.size());
        assertEquals(sampleEvent, queue.peek());
    }

    @Test
    void enqueueShouldNotAddEventWhenFilterIsFalse() throws Exception {
        EventFilter filter = event -> false;
        UserSubscriber subscriber = new UserSubscriber("Rejector", filter);

        subscriber.enqueue(sampleEvent);

        Field queueField = UserSubscriber.class.getDeclaredField("queue");
        queueField.setAccessible(true);
        Queue<Event> queue = (Queue<Event>) queueField.get(subscriber);

        assertTrue(queue.isEmpty());
    }

    @Test
    void processQueueShouldHandleEmptyQueueGracefully() {
        EventFilter filter = event -> true;
        UserSubscriber subscriber = new UserSubscriber("EmptyProcessor", filter);

        assertDoesNotThrow(subscriber::processQueue);
    }


    @Test
    void constructorShouldThrowWhenPriorityIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new TaskEvent("T", "D", "pub", null));
    }





    @Test
    void processQueueShouldProcessEventsCorrectly_UsingReflection() throws Exception {
        EventFilter filter = event -> true;
        UserSubscriber subscriber = new UserSubscriber("Processor", filter);

        // Step 1: Enqueue event
        subscriber.enqueue(sampleEvent);

        // Step 2: Check queue has 1 item before processing
        Field queueField = UserSubscriber.class.getDeclaredField("queue");
        queueField.setAccessible(true);
        Queue<Event> queueBefore = (Queue<Event>) queueField.get(subscriber);
        assertEquals(1, queueBefore.size(), "Queue should contain 1 event before processing");

        // Step 3: Process the queue
        subscriber.processQueue();  // This should print and clear the queue

        // Step 4: Check queue is empty after processing
        Queue<Event> queueAfter = (Queue<Event>) queueField.get(subscriber);
        assertTrue(queueAfter.isEmpty(), "Queue should be empty after processing");

        // Step 5: Process again to confirm graceful handling of empty queue
        assertDoesNotThrow(subscriber::processQueue);
    }

    @Test
    void testEqualsAndHashCode() {
        EventFilter filter = event -> true;
        UserSubscriber u1 = new UserSubscriber("UserA", filter);
        UserSubscriber u2 = new UserSubscriber("UserB", filter);

        assertNotEquals(u1, u2);
        assertEquals(u1, u1);
        assertEquals(u1.hashCode(), u1.hashCode());
    }

    @Test
    void testToStringContainsName() {
        UserSubscriber subscriber = new UserSubscriber("StringTester", event -> true);
        String result = subscriber.toString();

        assertTrue(result.contains("StringTester"));
        assertTrue(result.contains("UserSubscriber"));
    }
}
