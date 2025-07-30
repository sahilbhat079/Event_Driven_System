Event-Driven In-Memory Notification System
------------------------------------------

Overview:
This Java project implements an in-memory event-driven notification system based on the publisher-subscriber pattern. It supports role-based user access and custom filtering of events, all driven through a centralized EventBus. Events are not lost even if a subscriber is temporarily offline due to an internal PriorityQueue.

User Roles:
- SUBSCRIBER: Subscribes to publishers and receives events that pass custom filters.
- PUBLISHER: Creates and publishes TaskEvents and PriorityEvents.
- ADMIN: Has global visibility and can view/report all events.

Core Concepts:
- EventBus: Central engine responsible for managing subscriptions and dispatching events.
- Event: Each event (e.g., TaskEvent, HeartbeatEvent, PriorityEvent) includes a timestamp and a priority.
- Filter: Each subscriber has a filter (PriorityFilter, TimeWindowFilter, AlwaysTrueFilter) that determines which events to accept.
- PriorityQueue: Stores accepted events per subscriber in order of priority.
- SchedulerManager: Periodically generates heartbeat events using a fixed-rate scheduler.

How It Works:
1. The application starts with initializing the EventBus and registering default publishers (e.g., system, hr, tech).
2. A heartbeat scheduler emits recurring HeartbeatEvents from the system publisher.
3. The user logs in or registers with a specific role.
4. Based on their role, a CLI menu is shown:
   - PUBLISHER: Can publish new events (task or priority).
   - SUBSCRIBER: Can view and process their filtered events from a priority queue.
   - ADMIN: Can view global event logs and perform analytics.
5. When an event is published, the EventBus dispatches it to all subscribers of that publisher.
6. Each subscriber’s filter determines if the event should be accepted. If yes, it is added to their queue.
7. Subscribers can manually process events from their queue.

How to Run:
- Open the project in your Java IDE (e.g., IntelliJ, Eclipse).
- Compile the project (Java 17+ recommended).
- Run the MainApp.java class.
- Follow the prompts to log in as SUBSCRIBER, PUBLISHER, or ADMIN.
- Use the CLI menus to interact with the system (publish events, view notifications, etc.).
- The system gracefully shuts down all schedulers on exit.

Technologies Used:
- Java 17+
- Maven for build management
- java.util collections (Map, Set, PriorityQueue)
- java.time for timestamps and scheduling
- OOP principles: Interface, Abstract Classes, Builder Pattern

Key Features:
- Multiple publishers and subscribers
- Custom filters on events
- In-memory message queues (PriorityQueue)
- Heartbeat event generation via scheduler
- CLI-based interaction per user role
- Cleanly layered architecture

Author: Sahil
