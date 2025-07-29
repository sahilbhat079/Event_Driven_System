package com.company.notification.event;

import org.junit.jupiter.api.Test;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class TaskEventTest {

    private final String validTaskName = "Notify Admin";
    private final String validTaskDescription = "Send email about server status.";
    private final String validPublisherId = "publisher-001";
    private final Priority validPriority = Priority.HIGH;

    @Test
    void shouldCreateTaskEventWithValidData() {
        TaskEvent event = new TaskEvent(validTaskName, validTaskDescription, validPublisherId, validPriority);

        assertEquals(validTaskName, event.getTaskName());
        assertEquals(validTaskDescription, event.getTaskDescription());
        assertEquals(validPublisherId, event.getSourcePublisherId());
        assertEquals(validPriority, event.getPriority());
        assertEquals(EventTypes.TASK, event.getType());
        assertNotNull(event.getTimeStamp());
        assertEquals(event.getTimeStamp(), event.getDateTime());
    }

    @Test
    void shouldThrowExceptionForNullTaskDescription() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new TaskEvent(validTaskName, null, validPublisherId, validPriority));
        assertEquals("Task description cannot be null or empty", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForEmptyTaskDescription() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new TaskEvent(validTaskName, "", validPublisherId, validPriority));
        assertEquals("Task description cannot be null or empty", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForNullTaskName() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new TaskEvent(null, validTaskDescription, validPublisherId, validPriority));
        assertEquals("Task name cannot be null or empty", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForEmptyTaskName() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new TaskEvent("", validTaskDescription, validPublisherId, validPriority));
        assertEquals("Task name cannot be null or empty", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForNullPublisherId() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new TaskEvent(validTaskName, validTaskDescription, null, validPriority));
        assertEquals("Source publisher ID cannot be null or empty", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForEmptyPublisherId() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new TaskEvent(validTaskName, validTaskDescription, "", validPriority));
        assertEquals("Source publisher ID cannot be null or empty", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForNullPriority() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new TaskEvent(validTaskName, validTaskDescription, validPublisherId, null));
        assertEquals("Priority cannot be null", exception.getMessage());
    }

    @Test
    void toStringShouldContainTaskNameAndDescription() {
        TaskEvent event = new TaskEvent(validTaskName, validTaskDescription, validPublisherId, validPriority);
        String result = event.toString();

        assertTrue(result.contains("taskName='" + validTaskName + "'"));
        assertTrue(result.contains("taskDescription='" + validTaskDescription + "'"));
    }

    @Test
    void equalsAndHashCodeShouldWorkCorrectly() {
        TaskEvent event1 = new TaskEvent("Notify Admin", "Send email about server status.", "publisher1", Priority.HIGH);
        TaskEvent event2 = new TaskEvent("Notify Admin", "Send email about server status.", "publisher2", Priority.HIGH);

        // Should be NOT equal because sourcePublisherId is different
        assertNotEquals(event1, event2);

        // Same reference => equal
        assertEquals(event1, event1);

        // Hash consistency
        assertEquals(event1.hashCode(), event1.hashCode());

        // Should not be equal to unrelated type
        assertNotEquals(event1, "random string");
    }

    @Test
    void hashCodeShouldBeStable() {
        TaskEvent event = new TaskEvent(validTaskName, validTaskDescription, validPublisherId, validPriority);
        int hash1 = event.hashCode();
        int hash2 = event.hashCode();
        assertEquals(hash1, hash2);
    }

    @Test
    void canBeStoredInHashSet() {
        TaskEvent event = new TaskEvent(validTaskName, validTaskDescription, validPublisherId, validPriority);
        HashSet<TaskEvent> set = new HashSet<>();
        set.add(event);
        assertTrue(set.contains(event));
    }
}