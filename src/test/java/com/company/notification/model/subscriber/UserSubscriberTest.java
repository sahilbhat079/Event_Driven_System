package com.company.notification.model.subscriber;

import com.company.notification.event.*;
import com.company.notification.filters.EventFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserSubscriberTest {

    private EventFilter mockFilter;
    private UserSubscriber subscriber;
    private TaskEvent mockTaskEvent;

    @BeforeEach
    void setUp() {
        mockFilter = mock(EventFilter.class);
        subscriber = new UserSubscriber("Alice", mockFilter);

        mockTaskEvent = mock(TaskEvent.class);
        when(mockTaskEvent.getTaskName()).thenReturn("MockTask");
        when(mockTaskEvent.getTaskDescription()).thenReturn("Mock Description");
    }

    @Test
    void testConstructorInitializesCorrectly() {
        assertEquals("Alice", subscriber.getName());
        assertEquals(mockFilter, subscriber.getFilter());
    }





    @Test
    void testEnqueue_NullEvent_ShouldBeIgnored() {
        // Capture output
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        subscriber.enqueue(null);

        String printed = out.toString();
        assertTrue(printed.contains("Received null event. Ignored."));
    }


    @Test
    void testEnqueueFilteredEventNotAccepted() {
        Event event = new TaskEvent(
                "Sample Task",
                "Description",
                "publisher-123",
                Priority.MEDIUM
        );
        when(mockFilter.shouldProcess(event)).thenReturn(false);

        subscriber.enqueue(event);

        // Should not enqueue due to filter rejection
        subscriber.processQueue(); // Should log "No events to process"
    }

    @Test
    void testProcessMultipleEventTypes() {
        when(mockFilter.shouldProcess(any())).thenReturn(true);

        subscriber.enqueue(new TaskEvent(
                "Task A",
                "Description A",
                "pub-1",
                Priority.MEDIUM
        ));

        subscriber.enqueue(new PriorityEvent(
                "Urgent Task",
                Priority.HIGH,
                "Urgent task description",
                "pub-2"
        ));

        subscriber.enqueue(new HeartBeatEvent(
                "pub-3",
                Priority.LOW,
                "Heartbeat Check",
                "Regular health signal"
        ));

        subscriber.processQueue();  // Should process all 3 types
    }




    @Test
    void testEnqueueAcceptedEvent() {
        TaskEvent event = new TaskEvent(
                "Sample Task",
                "Description",
                "publisher-123",
                Priority.HIGH
        );
        when(mockFilter.shouldProcess(event)).thenReturn(true);

        subscriber.enqueue(event);
        subscriber.processQueue(); // Should process and print
    }






    @Test
    void testEnqueue_WithPassingFilter_ShouldAddEvent() {
        when(mockFilter.shouldProcess(mockTaskEvent)).thenReturn(true);
        subscriber.enqueue(mockTaskEvent);

        // Capture processQueue output
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        subscriber.processQueue();

        String output = out.toString();
        assertTrue(output.contains("MockTask"));
        assertTrue(output.contains("Mock Description"));
    }

    @Test
    void testEnqueue_WithFailingFilter_ShouldNotAddEvent() {
        when(mockFilter.shouldProcess(mockTaskEvent)).thenReturn(false);
        subscriber.enqueue(mockTaskEvent);

        // Capture processQueue output
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        subscriber.processQueue();

        String output = out.toString();
        assertTrue(output.contains("No events to process"));
    }









    @Test
    void testEnqueue_NoFilter_ShouldAddEvent() {
        UserSubscriber subWithoutFilter = new UserSubscriber("Bob", null);
        subWithoutFilter.enqueue(mockTaskEvent);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        subWithoutFilter.processQueue();

        String output = out.toString();
        assertTrue(output.contains("MockTask"));
    }

    @Test
    void testEqualsAndHashCode() {
        UserSubscriber another = new UserSubscriber("Alice", mockFilter);

        // should not be equal because UUIDs are different
        assertNotEquals(subscriber, another);
        assertNotEquals(subscriber.hashCode(), another.hashCode());
    }

    @Test
    void testToStringDoesNotCrash() {
        assertNotNull(subscriber.toString());
        assertTrue(subscriber.toString().contains("UserSubscriber"));
    }
}
