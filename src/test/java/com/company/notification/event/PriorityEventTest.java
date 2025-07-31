package com.company.notification.event;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PriorityEventTest {

    @Test
    void shouldCreateValidPriorityEvent() {
        PriorityEvent event = new PriorityEvent("Alert", Priority.MEDIUM, "Handle urgent issue", "pub-001");
        assertEquals("Handle urgent issue", event.getTaskDescription());
        assertEquals(Priority.MEDIUM, event.getPriority());
        assertEquals("pub-001", event.getSourcePublisherId());
        assertEquals(EventTypes.PRIORITY, event.getType());
    }

    @Test
    void shouldThrowForNullFields() {
        assertThrows(IllegalArgumentException.class, () -> new PriorityEvent("Alert", null, "msg", "id"));
        assertThrows(IllegalArgumentException.class, () -> new PriorityEvent("Alert", Priority.HIGH, null, "id"));
        assertThrows(IllegalArgumentException.class, () -> new PriorityEvent("Alert", Priority.HIGH, "", "id"));
        assertThrows(IllegalArgumentException.class, () -> new PriorityEvent("Alert", Priority.HIGH, "msg", null));
    }

    @Test
    void equalsAndHashCodeTest() throws InterruptedException {
        PriorityEvent event1 = new PriorityEvent("T1", Priority.HIGH, "Alert", "pub-123");

        // Delay to ensure different timestamps
        Thread.sleep(10);

        PriorityEvent event2 = new PriorityEvent("T1", Priority.HIGH, "Alert", "pub-123");

        assertNotEquals(event1, event2, "Events with different timestamps should not be equal");
        assertNotEquals(event1.hashCode(), event2.hashCode(), "Hash codes should differ due to different timestamps");
    }


    @Test
    void toStringTest() {
        PriorityEvent event = new PriorityEvent("Notify", Priority.LOW, "Disk nearly full", "pub-09");
        String result = event.toString();
        assertTrue(result.contains("Notify"));
        assertTrue(result.contains("Disk nearly full"));
    }
}
