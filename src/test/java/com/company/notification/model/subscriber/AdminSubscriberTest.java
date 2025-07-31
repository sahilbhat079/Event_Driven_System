package com.company.notification.model.subscriber;

import com.company.notification.event.Event;
import com.company.notification.event.Priority;
import com.company.notification.event.TaskEvent;
import com.company.notification.filters.EventFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;

class AdminSubscriberTest {

    private TaskEvent sampleEvent;

    @BeforeEach
    void setup() {
        sampleEvent = new TaskEvent(
                "Database Backup",
                "Daily backup of production database.",
                "publisher-123",
                Priority.HIGH
        );
    }

    @Test
    void constructorShouldThrowWhenFilterIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new AdminSubscriber("Admin", null));
    }

    @Test
    void constructorShouldInitializeFieldsProperly() throws Exception {
        EventFilter filter = event -> true;
        AdminSubscriber subscriber = new AdminSubscriber("AdminUser", filter);

        // Check queue via reflection
        Field queueField = AdminSubscriber.class.getDeclaredField("queue");
        queueField.setAccessible(true);
        Object queueObj = queueField.get(subscriber);
        assertNotNull(queueObj);
        assertTrue(queueObj instanceof LinkedList);

        // Check filter
        assertEquals(filter, subscriber.getFilter());
    }

    @Test
    void enqueueShouldIgnoreNullEvent() {
        EventFilter filter = event -> true;
        AdminSubscriber subscriber = new AdminSubscriber("Admin", filter);

        assertDoesNotThrow(() -> subscriber.enqueue(null));
    }

    @Test
    void enqueueShouldAddEventWhenFilterIsTrue() throws Exception {
        EventFilter filter = event -> true;
        AdminSubscriber subscriber = new AdminSubscriber("AdminAdder", filter);

        subscriber.enqueue(sampleEvent);

        Field queueField = AdminSubscriber.class.getDeclaredField("queue");
        queueField.setAccessible(true);
        Queue<Event> queue = (Queue<Event>) queueField.get(subscriber);

        assertEquals(1, queue.size());
        assertEquals(sampleEvent, queue.peek());
    }

    @Test
    void enqueueShouldNotAddEventWhenFilterIsFalse() throws Exception {
        EventFilter filter = event -> false;
        AdminSubscriber subscriber = new AdminSubscriber("AdminRejector", filter);

        subscriber.enqueue(sampleEvent);

        Field queueField = AdminSubscriber.class.getDeclaredField("queue");
        queueField.setAccessible(true);
        Queue<Event> queue = (Queue<Event>) queueField.get(subscriber);

        assertTrue(queue.isEmpty());
    }

    @Test
    void processQueueShouldHandleEmptyQueueGracefully() {
        EventFilter filter = event -> true;
        AdminSubscriber subscriber = new AdminSubscriber("EmptyProcessor", filter);

        assertDoesNotThrow(subscriber::processQueue); // Should print "No events to process"
    }

    @Test
    void processQueueShouldProcessAndClearEvents() throws Exception {
        EventFilter filter = event -> true;
        AdminSubscriber subscriber = new AdminSubscriber("Processor", filter);

        subscriber.enqueue(sampleEvent);
        subscriber.processQueue(); // Should print event

        // Ensure queue is now empty
        Field queueField = AdminSubscriber.class.getDeclaredField("queue");
        queueField.setAccessible(true);
        Queue<Event> queue = (Queue<Event>) queueField.get(subscriber);

        assertTrue(queue.isEmpty());

        // Second call should again be safe
        subscriber.processQueue();
    }

    @Test
    void testEqualsAndHashCode() {
        EventFilter filter = event -> true;
        AdminSubscriber a1 = new AdminSubscriber("Admin1", filter);
        AdminSubscriber a2 = new AdminSubscriber("Admin2", filter);

        assertNotEquals(a1, a2); // Different IDs
        assertEquals(a1, a1);    // Reflexivity
        assertEquals(a1.hashCode(), a1.hashCode()); // Consistency
    }

    @Test
    void testToStringContainsNameAndClass() {
        AdminSubscriber subscriber = new AdminSubscriber("TestAdmin", event -> true);
        String result = subscriber.toString();

        assertTrue(result.contains("TestAdmin"));
        assertTrue(result.contains("AdminSubscriber"));
    }
}
