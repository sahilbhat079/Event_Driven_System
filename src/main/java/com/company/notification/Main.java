package com.company.notification;

import com.company.notification.core.EventBus;
import com.company.notification.core.EventHistory;
import com.company.notification.core.SchedulerManager;
import com.company.notification.menu.AdminMenu;
import com.company.notification.menu.PublisherMenu;
import com.company.notification.menu.SubscriberMenu;
import com.company.notification.model.publisher.ConcretePublisher;
import com.company.notification.model.publisher.Publisher;
import com.company.notification.model.subscriber.AdminSubscriber;
import com.company.notification.model.subscriber.Subscriber;
import com.company.notification.model.subscriber.UserSubscriber;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Main {
        private static final Logger logger =  LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) {

        logger.info("Starting notification system...");
        System.out.println("Starting Notification System...");

        Map<String, Publisher> publisherMap = new HashMap<>();
        Map<String, Subscriber> subscriberMap = new HashMap<>();
        Map<String, AdminSubscriber> adminMap = new HashMap<>();



        Scanner scanner = new Scanner(System.in);
        EventHistory eventHistory = new EventHistory();
        EventBus eventBus = new EventBus(eventHistory);
        SchedulerManager schedulerManager = new SchedulerManager(eventBus);


        //upon initialization, register dummy admin
        AdminSubscriber dummyAdmin = eventBus.getDummyAdmin();
        adminMap.put(dummyAdmin.getName(), dummyAdmin);

        while (true) {
            System.out.println("\n=== Notification System Login ===");
            System.out.println("1. Login as Publisher");
            System.out.println("2. Login as Subscriber");
            System.out.println("3. Login as Admin");
            System.out.println("4. Exit");
            System.out.print("Select option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    System.out.print("Enter publisher name: ");
                    String name = scanner.nextLine().trim();
                    if (name.isEmpty()) {
                        System.out.println("Publisher name cannot be empty.");
                        break;
                    }
                    Publisher publisher = publisherMap.computeIfAbsent(name, n -> {
                        Publisher p = new ConcretePublisher(n);
                        eventBus.registerPublisher(p);
                        return p;
                    });
                    new PublisherMenu(eventBus, publisher, schedulerManager, scanner).show();
                }
                case "2" -> {
                    System.out.print("Enter subscriber name: ");
                    String name = scanner.nextLine().trim();
                    if (name.isEmpty()) {
                        System.out.println("Subscriber name cannot be empty.");
                        break;
                    }
                    Subscriber subscriber = subscriberMap.computeIfAbsent(name, n -> new UserSubscriber(n, event -> true));
                    new SubscriberMenu(eventBus, subscriber, scanner).display();
                }

            case "3" -> {
                    System.out.print("Enter admin name: ");
                    String name = scanner.nextLine().trim();
                    if (name.isEmpty()) {
                        System.out.println("Admin name cannot be empty.");
                        break;
                    }
                    AdminSubscriber admin = adminMap.computeIfAbsent(name, n -> {
                        AdminSubscriber a = new AdminSubscriber(n, event -> true);
                        eventBus.registerAdminSubscriber(a, a.getFilter());
                        return a;
                    });
                    new AdminMenu(eventBus, admin, eventHistory).display();
                }
                case "4" -> {
                    schedulerManager.shutdownAllSchedulers();
                    System.out.println("Exiting system. Goodbye.");
                    return;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }

    }



}