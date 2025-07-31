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

    // ANSI Colors
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String CYAN = "\u001B[36m";
    private static final String BOLD = "\u001B[1m";

    public AdminMenu(EventBus eventBus, Subscriber admin, EventHistory eventHistory) {
        this.eventBus = Objects.requireNonNull(eventBus, "EventBus must not be null");
        this.admin = Objects.requireNonNull(admin, "Admin subscriber must not be null");
        this.eventHistory = Objects.requireNonNull(eventHistory, "EventHistory must not be null");
    }

    public void display() {
        while (true) {
            System.out.println(BOLD + BLUE + "\n========= Admin Menu =========" + RESET);
            System.out.println("1. Process Admin Queue");
            System.out.println("2. View All Events");
            System.out.println("3. View Events by Type");
            System.out.println("4. View Events by Publisher");
            System.out.println("5. View Events in Last Hour");
            System.out.println("6. View Events Between Two Times");
            System.out.println("7. Count Events by Type");
            System.out.println("8. Clear Event History");
            System.out.println("9. Exit to Main Menu");
            System.out.print(YELLOW + "Enter choice: " + RESET);

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
                    System.out.println(GREEN + "Returning to Main Menu..." + RESET);
                    return;
                }
                default -> System.out.println(RED + "Invalid choice. Try again." + RESET);
            }
        }
    }

    private void viewAllEvents() {
        System.out.println(BOLD + CYAN + "\n--- All Events ---" + RESET);
        Optional.ofNullable(eventHistory.getAllEvents())
                .filter(list -> !list.isEmpty())
                .ifPresentOrElse(
                        list -> list.forEach(System.out::println),
                        () -> System.out.println(YELLOW + "No events found." + RESET)
                );
    }

    private void viewEventsByType() {
        System.out.println(BOLD + CYAN + "\n--- View Events by Type ---" + RESET);
        System.out.println("1. TASK");
        System.out.println("2. HEARTBEAT");
        System.out.println("3. PRIORITY");
        System.out.print(YELLOW + "Enter your choice (1-3): " + RESET);

        String choice = scanner.nextLine().trim();
        String eventType = switch (choice) {
            case "1" -> "TASK";
            case "2" -> "HEARTBEAT";
            case "3" -> "PRIORITY";
            default -> null;
        };

        if (eventType == null) {
            System.out.println(RED + "Invalid choice. Please select 1, 2, or 3." + RESET);
            return;
        }

        Optional.ofNullable(eventHistory.getEventsByType(eventType))
                .filter(list -> !list.isEmpty())
                .ifPresentOrElse(
                        list -> {
                            System.out.println(GREEN + "Events of type " + eventType + ":" + RESET);
                            list.forEach(System.out::println);
                        },
                        () -> System.out.println(YELLOW + "No events found for type: " + eventType + RESET)
                );
    }

    private void viewEventsByPublisher() {
        System.out.print(CYAN + "Enter publisher ID: " + RESET);
        String pubId = scanner.nextLine().trim();
        if (pubId.isEmpty()) {
            System.out.println(RED + "Publisher ID cannot be empty." + RESET);
            return;
        }

        Optional.ofNullable(eventHistory.getEventsByPublisher(pubId))
                .filter(list -> !list.isEmpty())
                .ifPresentOrElse(
                        list -> list.forEach(System.out::println),
                        () -> System.out.println(YELLOW + "No events found for publisher ID: " + pubId + RESET)
                );
    }

    private void viewEventsInLastHour() {
        System.out.println(BOLD + CYAN + "\n--- Events in Last Hour ---" + RESET);
        Optional.ofNullable(eventHistory.getEventsInLastHour())
                .filter(list -> !list.isEmpty())
                .ifPresentOrElse(
                        list -> list.forEach(System.out::println),
                        () -> System.out.println(YELLOW + "No events found in the last hour." + RESET)
                );
    }

    private void viewEventsBetween() {
        try {
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            System.out.print(YELLOW + "Enter start time (HH:mm): " + RESET);
            String startInput = scanner.nextLine().trim();

            System.out.print(YELLOW + "Enter end time (HH:mm): " + RESET);
            String endInput = scanner.nextLine().trim();

            if (startInput.isEmpty() || endInput.isEmpty()) {
                System.out.println(RED + "Time inputs cannot be empty." + RESET);
                return;
            }

            LocalTime startTime = LocalTime.parse(startInput, timeFormatter);
            LocalTime endTime = LocalTime.parse(endInput, timeFormatter);

            if (startTime.isAfter(endTime)) {
                System.out.println(RED + "Start time must be before or equal to end time." + RESET);
                return;
            }

            LocalDate today = LocalDate.now();
            Instant startInstant = LocalDateTime.of(today, startTime).atZone(ZoneId.systemDefault()).toInstant();
            Instant endInstant = LocalDateTime.of(today, endTime).atZone(ZoneId.systemDefault()).toInstant();

            Optional.ofNullable(eventHistory.getEventsBetween(startInstant, endInstant))
                    .filter(list -> !list.isEmpty())
                    .ifPresentOrElse(
                            list -> {
                                System.out.println(GREEN + "\nEvents between " + startInput + " and " + endInput + ":" + RESET);
                                list.forEach(System.out::println);
                            },
                            () -> System.out.println(YELLOW + "No events found in the selected time range." + RESET)
                    );

        } catch (DateTimeParseException e) {
            System.out.println(RED + "Invalid format. Please enter time in HH:mm format (e.g., 09:00 or 17:30)." + RESET);
        }
    }

    private void countEventsByType() {
        System.out.println(BOLD + CYAN + "\n--- Event Counts by Type ---" + RESET);
        Optional.ofNullable(eventHistory.countEventsByType())
                .filter(map -> !map.isEmpty())
                .ifPresentOrElse(
                        map -> map.forEach((type, count) ->
                                System.out.println(GREEN + type + ": " + count + RESET)),
                        () -> System.out.println(YELLOW + "No events have been published yet." + RESET)
                );
    }

    private void clearHistory() {
        System.out.print(YELLOW + "Are you sure you want to clear all event history? (yes/no): " + RESET);
        String input = scanner.nextLine().trim();
        if (input.equalsIgnoreCase("yes")) {
            eventHistory.clear();
            System.out.println(RED + "Event history cleared." + RESET);
        } else {
            System.out.println(GREEN + "Clear operation aborted." + RESET);
        }
    }
}
