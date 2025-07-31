package com.company.notification.event;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HeartBeatEventTest {

    @Test
    void shouldCreateDefaultHeartbeatEvent() {
        HeartBeatEvent event = new HeartBeatEvent("pub-001", Priority.LOW);
        assertEquals("Heart Beat", event.getTaskName());
        assertEquals("Reminder Event", event.getTaskDescription());
        assertEquals("pub-001", event.getSourcePublisherId());
        assertEquals(EventTypes.HEARTBEAT, event.getType());
    }

    @Test
    void shouldCreateCustomHeartbeatEvent() {
        HeartBeatEvent event = new HeartBeatEvent("pub-002", Priority.MEDIUM, "System Check", "Ping sent");
        assertEquals("System Check", event.getTaskName());
        assertEquals("Ping sent", event.getTaskDescription());
    }

    @Test
    void equalityTestWithDifferentTime() {
        HeartBeatEvent e1 = new HeartBeatEvent("pub", Priority.HIGH);
        HeartBeatEvent e2 = new HeartBeatEvent("pub", Priority.HIGH);
        assertNotEquals(e1, e2); // different timestamp
        assertNotEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    void toStringTest() {
        HeartBeatEvent event = new HeartBeatEvent("pub", Priority.HIGH, "HB", "Everything OK");
        String output = event.toString();
        assertTrue(output.contains("HB"));
        assertTrue(output.contains("Everything OK"));
    }

    //lets do null checks
    @Test
    void nullChecks() {
        assertThrows(IllegalArgumentException.class, () -> new HeartBeatEvent(null, Priority.HIGH));
    }


}
