package com.company.notification.core;

import com.company.notification.model.publisher.Publisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SchedulerManagerTest {

    private EventBus eventBus;
    private SchedulerManager schedulerManager;
    private Publisher mockPublisher;

    @BeforeEach
    void setup() {
        eventBus = mock(EventBus.class);
        schedulerManager = new SchedulerManager(eventBus);
        mockPublisher = mock(Publisher.class);
        when(mockPublisher.getName()).thenReturn("TestPublisher");
        when(mockPublisher.getId()).thenReturn("publisher-1");
    }

    @Test
    void testRegisterSchedulerSuccessfully() {
        when(eventBus.hasSubscribers(mockPublisher)).thenReturn(true);

        schedulerManager.registerScheduler(mockPublisher, 1);
        assertTrue(schedulerManager.hasScheduler(mockPublisher));
    }

    @Test
    void testShutdownScheduler() {
        when(eventBus.hasSubscribers(mockPublisher)).thenReturn(true);

        schedulerManager.registerScheduler(mockPublisher, 1);
        schedulerManager.shutdownScheduler(mockPublisher);

        assertFalse(schedulerManager.hasScheduler(mockPublisher));
    }

    @Test
    void testRemoveScheduler() {
        when(eventBus.hasSubscribers(mockPublisher)).thenReturn(true);

        schedulerManager.registerScheduler(mockPublisher, 1);
        schedulerManager.removeScheduler(mockPublisher);

        assertFalse(schedulerManager.hasScheduler(mockPublisher));
    }

    @Test
    void testShutdownAllSchedulers() {
        when(eventBus.hasSubscribers(mockPublisher)).thenReturn(true);

        schedulerManager.registerScheduler(mockPublisher, 1);
        schedulerManager.shutdownAllSchedulers();

        assertFalse(schedulerManager.hasScheduler(mockPublisher));
    }
}
