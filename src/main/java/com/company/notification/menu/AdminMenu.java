package com.company.notification.menu;

import com.company.notification.core.EventBus;
import com.company.notification.core.EventHistory;
import com.company.notification.model.subscriber.Subscriber;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class AdminMenu {
    private final EventBus eventBus;
    private final Subscriber admin;
    private final EventHistory eventHistory;
    private final Scanner scanner = new Scanner(System.in);

    public AdminMenu(EventBus eventBus, Subscriber admin, EventHistory eventHistory) {
        this.eventBus = Objects.requireNonNull(eventBus, "EventBus must not be null");
        this.admin = Objects.requireNonNull(admin, "Admin subscriber must not be null");
        this.eventHistory = Objects.requireNonNull(eventHistory, "EventHistory must not be null");
    }

    public void display() {
        while (true) {
            System.out.println("\n==== Admin Menu ====");
            System.out.println("1. Process Admin Queue");
            System.out.println("2. View All Events");
            System.out.println("3. View Events by Type");
            System.out.println("4. View Events by Publisher");
            System.out.println("5. View Events in Last Hour");
            System.out.println("6. View Events Between Two Times");
            System.out.println("7. Count Events by Type");
            System.out.println("8. Clear Event History");
            System.out.println("9. Exit to Main Menu");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> admin.processQueue();
                case "2" -> viewAllEvents();
                case "3" -> viewEventsByType();
                case "4" -> viewEventsByPublisher();
                case "5" -> viewEventsInLastHour();
                case "6" -> viewEventsBetween();
                case "7" -> countEventsByType();
                case "8" -> clearHistory();
                case "9" -> {
                    System.out.println("Returning to Main Menu...");
                    return;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void viewAllEvents() {
        System.out.println("\nAll Events:");
        Optional.ofNullable(eventHistory.getAllEvents())
                .filter(list -> !list.isEmpty())
                .ifPresentOrElse(
                        list -> list.forEach(System.out::println),
                        () -> System.out.println("No events found.")
                );
    }

    private void viewEventsByType() {
        System.out.println("Choose Event Type to View:");
        System.out.println("1. TASK");
        System.out.println("2. HEARTBEAT");
        System.out.println("3. PRIORITY");
        System.out.print("Enter your choice (1-3): ");

        String choice = scanner.nextLine().trim();
        String eventType = switch (choice) {
            case "1" -> "TASK";
            case "2" -> "HEARTBEAT";
            case "3" -> "PRIORITY";
            default -> null;
        };

        if (eventType == null) {
            System.out.println("Invalid choice. Please select 1, 2, or 3.");
            return;
        }

        Optional.ofNullable(eventHistory.getEventsByType(eventType))
                .filter(list -> !list.isEmpty())
                .ifPresentOrElse(
                        list -> {
                            System.out.println("Events of type " + eventType + ":");
                            list.forEach(System.out::println);
                        },
                        () -> System.out.println("No events found for type: " + eventType)
                );
    }

    private void viewEventsByPublisher() {
        System.out.print("Enter publisher ID: ");
        String pubId = scanner.nextLine().trim();
        if (pubId.isEmpty()) {
            System.out.println("Publisher ID cannot be empty.");
            return;
        }

        Optional.ofNullable(eventHistory.getEventsByPublisher(pubId))
                .filter(list -> !list.isEmpty())
                .ifPresentOrElse(
                        list -> list.forEach(System.out::println),
                        () -> System.out.println("No events found for publisher ID: " + pubId)
                );
    }

    private void viewEventsInLastHour() {
        System.out.println("\nEvents from Last Hour:");
        Optional.ofNullable(eventHistory.getEventsInLastHour())
                .filter(list -> !list.isEmpty())
                .ifPresentOrElse(
                        list -> list.forEach(System.out::println),
                        () -> System.out.println("No events found in the last hour.")
                );
    }

    private void viewEventsBetween() {
        try {
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            System.out.print("Enter start time (HH:mm): ");
            String startInput = scanner.nextLine().trim();

            System.out.print("Enter end time (HH:mm): ");
            String endInput = scanner.nextLine().trim();

            if (startInput.isEmpty() || endInput.isEmpty()) {
                System.out.println("Time inputs cannot be empty.");
                return;
            }

            LocalTime startTime = LocalTime.parse(startInput, timeFormatter);
            LocalTime endTime = LocalTime.parse(endInput, timeFormatter);

            if (startTime.isAfter(endTime)) {
                System.out.println("Start time must be before or equal to end time.");
                return;
            }

            LocalDate today = LocalDate.now();
            LocalDateTime startDateTime = LocalDateTime.of(today, startTime);
            LocalDateTime endDateTime = LocalDateTime.of(today, endTime);

            Instant startInstant = startDateTime.atZone(ZoneId.systemDefault()).toInstant();
            Instant endInstant = endDateTime.atZone(ZoneId.systemDefault()).toInstant();

            Optional.ofNullable(eventHistory.getEventsBetween(startInstant, endInstant))
                    .filter(list -> !list.isEmpty())
                    .ifPresentOrElse(
                            list -> {
                                System.out.println("\nEvents between " + startInput + " and " + endInput + " today:");
                                list.forEach(System.out::println);
                            },
                            () -> System.out.println("No events found in the selected time range.")
                    );

        } catch (DateTimeParseException e) {
            System.out.println("Invalid format. Please enter time in HH:mm format (e.g., 09:00 or 17:30).");
        }
    }

    private void countEventsByType() {
        System.out.println("\nEvent Counts by Type:");

        Optional.ofNullable(eventHistory.countEventsByType())
                .filter(map -> !map.isEmpty())
                .ifPresentOrElse(
                        map -> map.forEach((type, count) -> System.out.println(type + ": " + count)),
                        () -> System.out.println("No events have been published yet.")
                );
    }

    private void clearHistory() {
        System.out.print("Are you sure you want to clear all event history? (yes/no): ");
        String input = scanner.nextLine().trim();
        if (input.equalsIgnoreCase("yes")) {
            eventHistory.clear();
            System.out.println("Event history cleared.");
        } else {
            System.out.println("Clear operation aborted.");
        }
    }
}
