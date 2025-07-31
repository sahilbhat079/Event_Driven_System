package com.company.notification.filters;

import com.company.notification.event.Event;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

//functional interface
class AlwaysTrueFilterTest {

    @Test
    void shouldAlwaysReturnTrue() {
        Event mockEvent = mock(Event.class);
        AlwaysTrueFilter filter = new AlwaysTrueFilter();

        assertTrue(filter.shouldProcess(mockEvent));
    }
}
