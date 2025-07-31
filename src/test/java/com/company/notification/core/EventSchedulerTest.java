package com.company.notification.core;

import com.company.notification.model.publisher.Publisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventSchedulerTest {

    private EventBus mockEventBus;
    private Publisher mockPublisher;
    private EventScheduler scheduler;

    @BeforeEach
    void setUp() {
        mockEventBus = mock(EventBus.class);
        mockPublisher = mock(Publisher.class);

        when(mockPublisher.getName()).thenReturn("TestPublisher");
        when(mockPublisher.getId()).thenReturn("PUB123");

        scheduler = new EventScheduler(mockEventBus, mockPublisher, 1); // 1 sec for testing
    }

    @Test
    void start_shouldScheduleHeartbeatWhenSubscribersExist() throws InterruptedException {
        when(mockEventBus.hasSubscribers(mockPublisher)).thenReturn(true);

        scheduler.start();

        // Wait enough for one heartbeat (2 seconds)
        TimeUnit.SECONDS.sleep(2);

        verify(mockEventBus, atLeastOnce()).publishFromPublisher(eq(mockPublisher), any());
        scheduler.shutdown();
    }

    @Test
    void start_shouldShutdownWhenNoSubscribers() throws InterruptedException {
        when(mockEventBus.hasSubscribers(mockPublisher)).thenReturn(false);

        scheduler.start();
        TimeUnit.SECONDS.sleep(2); // Let it run once

        assertTrue(scheduler.isShutdown(), "Scheduler should shut down if no subscribers");
    }

    @Test
    void start_shouldNotStartIfAlreadyRunning() {
        when(mockEventBus.hasSubscribers(mockPublisher)).thenReturn(true);
        scheduler.start();
        scheduler.start(); // second call

        // only one scheduler should be created and started
        verify(mockEventBus, timeout(2000).atLeastOnce()).publishFromPublisher(any(), any());
        scheduler.shutdown();
    }

    @Test
    void shutdown_shouldStopScheduler() {
        when(mockEventBus.hasSubscribers(mockPublisher)).thenReturn(true);
        scheduler.start();
        scheduler.shutdown();
        assertTrue(scheduler.isShutdown(), "Scheduler should be shut down");
    }

    @Test
    void isShutdown_shouldReturnTrueWhenSchedulerNullOrShutdown() {
        assertTrue(scheduler.isShutdown(), "Should be shutdown before start");

        when(mockEventBus.hasSubscribers(mockPublisher)).thenReturn(true);
        scheduler.start();
        scheduler.shutdown();

        assertTrue(scheduler.isShutdown(), "Should be shutdown after manual shutdown");
    }

    @Test
    void constructor_shouldThrowExceptionForInvalidArgs() {
        assertThrows(IllegalArgumentException.class, () -> new EventScheduler(null, mockPublisher, 5));
        assertThrows(IllegalArgumentException.class, () -> new EventScheduler(mockEventBus, null, 5));
        assertThrows(IllegalArgumentException.class, () -> new EventScheduler(mockEventBus, mockPublisher, 0));
        assertThrows(IllegalArgumentException.class, () -> new EventScheduler(mockEventBus, mockPublisher, -1));
    }
}
