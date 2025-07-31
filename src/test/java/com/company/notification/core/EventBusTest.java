package com.company.notification.core;

import com.company.notification.event.Event;
import com.company.notification.filters.EventFilter;
import com.company.notification.model.publisher.Publisher;
import com.company.notification.model.subscriber.AdminSubscriber;
import com.company.notification.model.subscriber.Subscriber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventBusTest {

    private EventBus eventBus;
    private Publisher mockPublisher;
    private Subscriber mockSubscriber;
    private AdminSubscriber mockAdmin;
    private Event mockEvent;
    private EventHistory mockEventHistory;
    private EventFilter mockFilter;

    @BeforeEach
    void setup() {
        mockEventHistory = mock(EventHistory.class);
        eventBus = new EventBus(mockEventHistory);

        mockPublisher = mock(Publisher.class);
        mockSubscriber = mock(Subscriber.class);
        mockAdmin = mock(AdminSubscriber.class);
        mockEvent = mock(Event.class);
        mockFilter = mock(EventFilter.class);

        when(mockSubscriber.getName()).thenReturn("TestSubscriber");
        when(mockAdmin.getName()).thenReturn("Admin");


        mockPublisher = mock(Publisher.class);
        when(mockPublisher.getName()).thenReturn("TestPublisher");
        when(mockPublisher.getId()).thenReturn("pub-123");

    }


    @Test
    void testRegisterPublisher() {
        eventBus.registerPublisher(mockPublisher);
        assertTrue(eventBus.getAllPublishers().contains(mockPublisher));
    }

    @Test
    void testSubscribeAndPublishEventToSubscriber() {
        EventFilter filter = e -> true;

        eventBus.subscribe(mockSubscriber, mockPublisher, filter);
        eventBus.publishFromPublisher(mockPublisher, mockEvent);

        verify(mockSubscriber, times(1)).enqueue(mockEvent);
    }

    @Test
    void testUnsubscribeRemovesSubscriber() {
        eventBus.subscribe(mockSubscriber, mockPublisher, e -> true);
        eventBus.unsubscribe(mockSubscriber, mockPublisher);
        eventBus.publishFromPublisher(mockPublisher, mockEvent);

        verify(mockSubscriber, never()).enqueue(mockEvent);
    }

    @Test
    void testEventFilteredOut() {
        eventBus.subscribe(mockSubscriber, mockPublisher, e -> false);
        eventBus.publishFromPublisher(mockPublisher, mockEvent);

        verify(mockSubscriber, never()).enqueue(mockEvent);
    }



    @Test
    void testSubscribeAndPublishEventMatchingFilter() {
        eventBus.registerPublisher(mockPublisher );
        when(mockFilter.shouldProcess(mockEvent)).thenReturn(true);

        eventBus.subscribe(mockSubscriber, mockPublisher, mockFilter);
        eventBus.publishFromPublisher(mockPublisher, mockEvent);

        verify(mockSubscriber).enqueue(mockEvent);
    }

    @Test
    void testSubscribeAndPublishEventNotMatchingFilter() {
        eventBus.registerPublisher(mockPublisher);
        when(mockFilter.shouldProcess(mockEvent)).thenReturn(false);

        eventBus.subscribe(mockSubscriber, mockPublisher, mockFilter);
        eventBus.publishFromPublisher(mockPublisher, mockEvent);

        verify(mockSubscriber, never()).enqueue(any());
    }







    @Test
    void testUpdateFilterChangesBehavior() {
        EventFilter denyAll = e -> false;
        EventFilter allowAll = e -> true;

        eventBus.subscribe(mockSubscriber, mockPublisher, denyAll);
        eventBus.updateFilter(mockSubscriber, allowAll);

        eventBus.publishFromPublisher(mockPublisher, mockEvent);
        verify(mockSubscriber, times(1)).enqueue(mockEvent);
    }

    @Test
    void testNullSubscriberOrPublisherThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> eventBus.subscribe(null, mockPublisher, e -> true));
        assertThrows(IllegalArgumentException.class, () -> eventBus.subscribe(mockSubscriber, null, e -> true));
        assertThrows(IllegalArgumentException.class, () -> eventBus.unsubscribe(null, mockPublisher));
    }

    @Test
    void testNullPublisherOrEventOnPublishThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> eventBus.publishFromPublisher(null, mockEvent));
        assertThrows(IllegalArgumentException.class, () -> eventBus.publishFromPublisher(mockPublisher, null));
    }

    @Test
    void testGetPublishersForSubscriber() {
        eventBus.subscribe(mockSubscriber, mockPublisher, mockFilter);

        Set<Publisher> publishers = eventBus.getPublishersForSubscriber(mockSubscriber);
        assertEquals(1, publishers.size());
        assertTrue(publishers.contains(mockPublisher));
    }

    @Test
    void testGetSubscribersForPublisher() {
        eventBus.subscribe(mockSubscriber, mockPublisher, mockFilter);

        Set<Subscriber> subscribers = eventBus.getSubscribers(mockPublisher);
        assertEquals(1, subscribers.size());
        assertTrue(subscribers.contains(mockSubscriber));
    }



    @Test
    void testUpdateFilterWithNulls() {
        assertThrows(IllegalArgumentException.class, () -> eventBus.updateFilter(null, mockFilter));
        assertThrows(IllegalArgumentException.class, () -> eventBus.updateFilter(mockSubscriber, null));
    }

    @Test
    void testDummyAdminFilterAlwaysTrue() {
        AdminSubscriber dummy = (AdminSubscriber) eventBus.getDummyAdmin();
        assertNotNull(dummy);
        assertTrue(dummy.getFilter().shouldProcess(mockEvent));
    }


    @Test
    void testSameSubscriberMultiplePublishers() {
        Publisher mockPublisher2 = mock(Publisher.class);
        when(mockPublisher2.getName()).thenReturn("Pub2");
        when(mockPublisher2.getId()).thenReturn("id2");

        eventBus.registerPublisher(mockPublisher);
        eventBus.registerPublisher(mockPublisher2);

        eventBus.subscribe(mockSubscriber, mockPublisher, mockFilter);
        eventBus.subscribe(mockSubscriber, mockPublisher2, mockFilter);

        Set<Publisher> publishers = eventBus.getPublishersForSubscriber(mockSubscriber);
        assertEquals(2, publishers.size());
    }



    @Test
    void testGetAllPublishers() {
        eventBus.registerPublisher(mockPublisher);

        Set<Publisher> publishers = eventBus.getAllPublishers();
        assertEquals(1, publishers.size());
        assertTrue(publishers.contains(mockPublisher));
    }




}
