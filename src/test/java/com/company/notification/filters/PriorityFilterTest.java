package com.company.notification.filters;

import com.company.notification.event.Event;
import com.company.notification.event.Priority;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PriorityFilterTest {

    @Test
    void shouldProcessEventWithMatchingPriority() {
        PriorityFilter filter = new PriorityFilter(Priority.HIGH);

        Event mockEvent = mock(Event.class);
        when(mockEvent.getPriority()).thenReturn(Priority.HIGH);

        assertTrue(filter.shouldProcess(mockEvent));
    }

    @Test
    void shouldNotProcessEventWithDifferentPriority() {
        PriorityFilter filter = new PriorityFilter(Priority.HIGH);

        Event mockEvent = mock(Event.class);
        when(mockEvent.getPriority()).thenReturn(Priority.LOW);

        assertFalse(filter.shouldProcess(mockEvent));
    }

    @Test
    void toStringShouldContainPriorityInfo() {
        PriorityFilter filter = new PriorityFilter(Priority.MEDIUM);

        String result = filter.toString();
        assertTrue(result.contains("priority=MEDIUM"));
    }
}
