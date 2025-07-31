package com.company.notification.menu;

import com.company.notification.core.EventBus;
import com.company.notification.core.SchedulerManager;
import com.company.notification.event.HeartBeatEvent;
import com.company.notification.event.Priority;
import com.company.notification.event.PriorityEvent;
import com.company.notification.event.TaskEvent;
import com.company.notification.model.publisher.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class PublisherMenu {

    private static final Logger logger = LoggerFactory.getLogger(PublisherMenu.class);

    private final EventBus eventBus;
    private final Publisher publisher;
    private final SchedulerManager schedulerManager;
    private final Scanner scanner;

    public PublisherMenu(EventBus eventBus, Publisher publisher, SchedulerManager schedulerManager, Scanner scanner) {
        if (eventBus == null) {
            throw new IllegalArgumentException("EventBus must not be null");
        }
        if (publisher == null) {
            throw new IllegalArgumentException("Publisher must not be null");
        }
        if (schedulerManager == null) {
            throw new IllegalArgumentException("SchedulerManager must not be null");
        }
        if (scanner == null) {
            throw new IllegalArgumentException("Scanner must not be null");
        }

        this.eventBus = eventBus;
        this.publisher = publisher;
        this.schedulerManager = schedulerManager;
        this.scanner = scanner;
    }

    public void show() {
        while (true) {
            System.out.println("\nPublisher Menu - " + publisher.getName());
            System.out.println("1. Publish Task Event");
            System.out.println("2. Start Reminder (Scheduled Task)");
            System.out.println("3. Stop Reminder");
            System.out.println("4. Exit");

            System.out.print("Choose option: ");
            String input = scanner.nextLine();

            switch (input) {
                case "1" -> publishTaskEvent();
                case "2" -> startReminder();
                case "3" -> stopReminder();
                case "4" -> {
                    logger.info("Exiting Publisher Menu for publisher: {}", publisher.getName());
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void publishTaskEvent() {
        System.out.print("Enter Task Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter Task Description: ");
        String desc = scanner.nextLine();

        if (name == null || name.isBlank() || desc == null || desc.isBlank()) {
            logger.warn("Task name or description was empty");
            System.out.println("Task name and description cannot be empty.");
            return;
        }

        Priority priority = askPriority();

        if (priority == null) {
            logger.warn("User did not select a valid priority");
            System.out.println("Priority cannot be null.");
            return;
        }

        switch (priority) {
            case HIGH -> {
                PriorityEvent event = new PriorityEvent(name, priority, desc, publisher.getId());
                eventBus.publishFromPublisher(publisher, event);
                logger.info("Published HIGH priority event from publisher: {}", publisher.getName());
            }
            case MEDIUM -> {
                HeartBeatEvent event = new HeartBeatEvent(publisher.getId(), priority, name, desc);
                publisher.publish(eventBus, event);
                logger.info("Published MEDIUM priority heartbeat event from publisher: {}", publisher.getName());
            }
            case LOW -> {
                TaskEvent event = new TaskEvent(name, desc, publisher.getId(), priority);
                eventBus.publishFromPublisher(publisher, event);
                logger.info("Published LOW priority task event from publisher: {}", publisher.getName());
            }
        }

        System.out.println("Task event published successfully.");
    }

    private void startReminder() {
        if (!eventBus.hasSubscribers(publisher)) {
            logger.warn("Cannot start reminder — no subscribers for publisher: {}", publisher.getName());
            return;
        }

        System.out.print("Enter reminder interval (seconds): ");
        try {
            String input = scanner.nextLine();
            if (input == null || input.isBlank()) {
                System.out.println("Interval cannot be empty.");
                return;
            }

            long interval = Long.parseLong(input);
            schedulerManager.registerScheduler(publisher, interval);
            logger.info("Started reminder scheduler for publisher: {} with interval: {}s", publisher.getName(), interval);
            System.out.println("Reminder started for every " + interval + " seconds.");
        } catch (NumberFormatException e) {
            logger.error("Invalid interval entered by user: not a number");
            System.out.println("Invalid interval.");
        }
    }

    private Priority askPriority() {
        Priority[] priorities = Priority.values();

        System.out.println("Select Priority:");
        for (int i = 0; i < priorities.length; i++) {
            System.out.println((i + 1) + ". " + priorities[i]);
        }

        while (true) {
            System.out.print("Enter priority (1-" + priorities.length + " or name): ");
            String input = scanner.nextLine();

            if (input == null || input.trim().isEmpty()) {
                System.out.println("Priority input cannot be empty or null.");
                continue;
            }

            input = input.trim();

            try {
                int choice = Integer.parseInt(input);
                if (choice >= 1 && choice <= priorities.length) {
                    return priorities[choice - 1];
                } else {
                    System.out.println("Invalid number. Please enter between 1 and " + priorities.length + ".");
                    continue;
                }
            } catch (NumberFormatException ignored) {
            }

            try {
                return Priority.valueOf(input.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid priority name. Try again.");
            }
        }
    }

    private void stopReminder() {
        //only stop if scheduler is there otherwise do nothing
        if (!schedulerManager.hasScheduler(publisher)) {
            logger.info("Scheduler does not exist for publisher: {}", publisher.getName());
            return;
        }
        schedulerManager.shutdownScheduler(publisher);
        logger.info("Stopped reminder scheduler for publisher: {}", publisher.getName());
    }
}
