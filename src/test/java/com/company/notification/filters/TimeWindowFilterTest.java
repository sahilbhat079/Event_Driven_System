package com.company.notification.filters;

import com.company.notification.event.Event;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TimeWindowFilterTest {

    @Test
    void shouldAcceptEventExactlyAtStartTime() {
        LocalTime start = LocalTime.of(10, 0);
        LocalTime end = LocalTime.of(12, 0);
        TimeWindowFilter filter = new TimeWindowFilter(start, end);

        Event event = mock(Event.class);
        when(event.getDateTime()).thenReturn(LocalDateTime.of(2025, 7, 30, 10, 0));

        assertTrue(filter.shouldProcess(event));
    }

    @Test
    void shouldAcceptEventExactlyAtEndTime() {
        LocalTime start = LocalTime.of(10, 0);
        LocalTime end = LocalTime.of(12, 0);
        TimeWindowFilter filter = new TimeWindowFilter(start, end);

        Event event = mock(Event.class);
        when(event.getDateTime()).thenReturn(LocalDateTime.of(2025, 7, 30, 12, 0));

        assertTrue(filter.shouldProcess(event));
    }

    @Test
    void shouldAcceptEventWithinWindow() {
        TimeWindowFilter filter = new TimeWindowFilter(LocalTime.of(9, 0), LocalTime.of(17, 0));
        Event event = mock(Event.class);
        when(event.getDateTime()).thenReturn(LocalDateTime.of(2025, 7, 30, 12, 30));

        assertTrue(filter.shouldProcess(event));
    }

    @Test
    void shouldRejectEventBeforeStart() {
        TimeWindowFilter filter = new TimeWindowFilter(LocalTime.of(10, 0), LocalTime.of(12, 0));
        Event event = mock(Event.class);
        when(event.getDateTime()).thenReturn(LocalDateTime.of(2025, 7, 30, 9, 59));

        assertFalse(filter.shouldProcess(event));
    }

    @Test
    void shouldRejectEventAfterEnd() {
        TimeWindowFilter filter = new TimeWindowFilter(LocalTime.of(10, 0), LocalTime.of(12, 0));
        Event event = mock(Event.class);
        when(event.getDateTime()).thenReturn(LocalDateTime.of(2025, 7, 30, 12, 1));

        assertFalse(filter.shouldProcess(event));
    }

    @Test
    void shouldThrowExceptionIfStartTimeIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new TimeWindowFilter(null, LocalTime.NOON)
        );
        assertEquals("Start time cannot be null", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionIfEndTimeIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new TimeWindowFilter(LocalTime.NOON, null)
        );
        assertEquals("End time cannot be null", exception.getMessage());
    }
}
