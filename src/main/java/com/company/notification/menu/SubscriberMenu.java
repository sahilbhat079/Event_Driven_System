package com.company.notification.menu;

import com.company.notification.core.EventBus;
import com.company.notification.filters.AlwaysTrueFilter;
import com.company.notification.filters.EventFilter;
import com.company.notification.filters.PriorityFilter;
import com.company.notification.filters.TimeWindowFilter;
import com.company.notification.model.publisher.Publisher;
import com.company.notification.model.subscriber.Subscriber;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class SubscriberMenu {
    private final EventBus eventBus;
    private final Subscriber subscriber;
    private final Scanner scanner;

    public SubscriberMenu(EventBus eventBus, Subscriber subscriber, Scanner scanner) {
        this.eventBus = Objects.requireNonNull(eventBus, "EventBus must not be null");
        this.subscriber = Objects.requireNonNull(subscriber, "Subscriber must not be null");
        this.scanner = Objects.requireNonNull(scanner, "Scanner must not be null");
    }

    public void display() {
        while (true) {
            System.out.println("\n==== 📋 Subscriber Menu ====");
            System.out.println("1. Subscribe to Publisher");
            System.out.println("2. Unsubscribe from Publisher");
            System.out.println("3. Change My Filter");
            System.out.println("4. View and Process My Queue");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            int choice = readIntInput();
            switch (choice) {
                case 1 -> subscribe();
                case 2 -> unsubscribe();
                case 3 -> changeFilter();
                case 4 -> subscriber.processQueue();
                case 5 -> {
                    System.out.println("Exiting Subscriber Menu...");
                    return;
                }
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }

    private void subscribe() {
        List<Publisher> publishers = eventBus.getAllPublishers().stream().toList();
        if (publishers.isEmpty()) {
            System.out.println("No publishers available.");
            return;
        }

        System.out.println("Available Publishers:");
        for (int i = 0; i < publishers.size(); i++) {
            System.out.println((i + 1) + ". " + publishers.get(i).getName());
        }
        System.out.print("Select publisher number: ");
        int idx = readIntInput() - 1;

        if (idx < 0 || idx >= publishers.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        Publisher publisher = publishers.get(idx);
        EventFilter filter = getFilterFromUser();
        eventBus.subscribe(subscriber, publisher, filter);
        System.out.println("Subscribed to " + publisher.getName());
    }

    private void unsubscribe() {
        List<Publisher> subscriptions = eventBus.getPublishersForSubscriber(subscriber).stream().toList();

        if (subscriptions.isEmpty()) {
            System.out.println("You are not subscribed to any publishers.");
            return;
        }

        System.out.println("Your Subscriptions:");
        for (int i = 0; i < subscriptions.size(); i++) {
            System.out.println((i + 1) + ". " + subscriptions.get(i).getName());
        }
        System.out.println("0. Cancel");
        System.out.print("Select publisher to unsubscribe from (0 to cancel): ");

        int idx = readIntInput() - 1;

        if (idx == -1) {
            System.out.println("Unsubscription cancelled.");
            return;
        }

        if (idx < 0 || idx >= subscriptions.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        Publisher publisher = subscriptions.get(idx);
        eventBus.unsubscribe(subscriber, publisher);
        System.out.println("Unsubscribed from " + publisher.getName());
    }

    private void changeFilter() {
        List<Publisher> subscriptions = eventBus.getPublishersForSubscriber(subscriber).stream().toList();
        if (subscriptions.isEmpty()) {
            System.out.println("You are not subscribed to any publishers.");
            return;
        }

        System.out.println("Change filter for which subscription?");
        for (int i = 0; i < subscriptions.size(); i++) {
            System.out.println((i + 1) + ". " + subscriptions.get(i).getName());
        }
        System.out.print("Enter publisher number: ");
        int idx = readIntInput() - 1;

        if (idx < 0 || idx >= subscriptions.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        Publisher publisher = subscriptions.get(idx);
        eventBus.unsubscribe(subscriber, publisher);
        EventFilter newFilter = getFilterFromUser();
        eventBus.subscribe(subscriber, publisher, newFilter);
        System.out.println("Filter updated for " + publisher.getName());
    }

    private EventFilter getFilterFromUser() {
        System.out.println("Select Filter:");
        System.out.println("1. High Priority Only");
        System.out.println("2. Low Priority Only");
        System.out.println("3. Time Window");
        System.out.println("4. No Filter (All Events)");
        System.out.print("Your choice: ");
        int choice = readIntInput();

        return switch (choice) {
            case 1 -> new PriorityFilter(com.company.notification.event.Priority.HIGH);
            case 2 -> new PriorityFilter(com.company.notification.event.Priority.LOW);
            case 3 -> {
                try {
                    System.out.print("Enter start time (HH:mm): ");
                    String startStr = scanner.nextLine().trim();
                    System.out.print("Enter end time (HH:mm): ");
                    String endStr = scanner.nextLine().trim();
                    if (startStr.isEmpty() || endStr.isEmpty()) throw new IllegalArgumentException("Time inputs cannot be empty.");

                    LocalTime start = LocalTime.parse(startStr);
                    LocalTime end = LocalTime.parse(endStr);
                    yield new TimeWindowFilter(start, end);
                } catch (DateTimeParseException | IllegalArgumentException e) {
                    System.out.println("Invalid time input. Using default filter.");
//                    you want to return a value, you must use yield.
                    yield new AlwaysTrueFilter();
                }
            }
            case 4 -> new AlwaysTrueFilter();
            default -> {
                System.out.println("Invalid choice. Using default filter.");
                yield new AlwaysTrueFilter();
            }
        };
    }

    private int readIntInput() {
        try {
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) return -1;
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
