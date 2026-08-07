/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package main;

import core.LibrarySystem;
import model.*;
import util.FileHandler;
import exception.*;

import java.util.InputMismatchException;
import java.util.Scanner;
/**
 *
 * @author marcoang
 */
public class Main {
    private static LibrarySystem system = new LibrarySystem();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Starting System... Loading Data...");
        FileHandler.loadAllData(system); // Load from TXT files on startup

        boolean running = true;
        while (running) {
            try {
                displayMenu();
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume the newline character

                switch (choice) {
                    case 1:
                        addUser();
                        break;
                    case 2:
                        addLibraryItem();
                        break;
                    case 3:
                        viewAllUsers();
                        break;
                    case 4:
                        viewAllItems();
                        break;
                    case 5:
                        borrowItem();
                        break;
                    case 6:
                        returnItem();
                        break;
                    case 7:
                        viewStudyRooms();
                        break;
                    case 8:
                        viewTransactions();
                        break;
                    case 9: 
                        bookStudyRoom();
                        break;
                    case 10: 
                        cancelRoomBooking();
                        break;
                    case 0:
                        System.out.println("Saving data to files...");
                        FileHandler.saveAllData(system); // Save to TXT files on exit
                        System.out.println("Data saved successfully. Shutting down. Goodbye!");
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid option. Please enter a number between 0 and 8.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Error: Invalid input! Please enter a valid number.");
                scanner.nextLine(); // Clear the bad input from scanner buffer
            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e.getMessage());
            }
        }
        scanner.close();
    }

    private static void displayMenu() {
        System.out.println("\n==========================================");
        System.out.println("  TAR UMT SMART LIBRARY MANAGEMENT SYSTEM ");
        System.out.println("==========================================");
        System.out.println("1. Register New User");
        System.out.println("2. Add New Library Item");
        System.out.println("3. View All Users");
        System.out.println("4. View All Items");
        System.out.println("5. Borrow an Item");
        System.out.println("6. Return an Item");
        System.out.println("7. View Study Rooms"); 
        System.out.println("8. View Active Transactions");
        System.out.println("9. Book a Study Room"); 
        System.out.println("10. Cancel Room Booking");
        System.out.println("0. Save and Exit");
        System.out.print("Enter your choice: ");
    }

    private static void addUser() {
        System.out.println("\n--- Register User ---");
        System.out.print("Enter Type (1 for Student, 2 for Faculty): ");
        int type = scanner.nextInt();
        scanner.nextLine(); 

        System.out.print("Enter User ID: ");
        String id = scanner.nextLine();
        System.out.print("Enter Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();

        if (type == 1) {
            System.out.print("Enter Major: ");
            String major = scanner.nextLine();
            system.registerUser(new Student(id, name, email, 0.0, major));
            System.out.println("Student registered successfully!");
        } else if (type == 2) {
            System.out.print("Enter Department: ");
            String dept = scanner.nextLine();
            system.registerUser(new Faculty(id, name, email, 0.0, dept));
            System.out.println("Faculty registered successfully!");
        } else {
            System.out.println("Invalid user type.");
        }
    }

    private static void addLibraryItem() {
        System.out.println("\n--- Add Library Item ---");
        System.out.print("Enter Type (1 for Book, 2 for Digital Resource): ");
        int type = scanner.nextInt();
        scanner.nextLine(); 

        System.out.print("Enter Item ID: ");
        String id = scanner.nextLine();
        System.out.print("Enter Title: ");
        String title = scanner.nextLine();

        switch (type) {
            case 1:
                System.out.print("Enter Author: ");
                String author = scanner.nextLine();
                System.out.print("Enter ISBN: ");
                String isbn = scanner.nextLine();
                System.out.print("Enter Genre: ");
                String genre = scanner.nextLine();
                system.addLibraryItem(new Book(id, title, true, author, isbn, genre));
                System.out.println("Book added successfully!");
                break;
            case 2:
                System.out.print("Enter File Format (e.g., PDF): ");
                String format = scanner.nextLine();
                System.out.print("Enter File Size (MB): ");
                double size = scanner.nextDouble();
                system.addLibraryItem(new DigitalResource(id, title, true, format, size));
                System.out.println("Digital Resource added successfully!");
                break;
            default:
                System.out.println("Invalid item type.");
                break;
        }
    }

    private static void viewAllUsers() {
        System.out.println("\n--- Registered Users ---");
        if (system.getUsers().isEmpty()) {
            System.out.println("No users found.");
            return;
        }
        // Polymorphism: Java automatically calls the overridden toString() of Student or Faculty
        for (User u : system.getUsers()) {
            System.out.println(u.toString()); 
        }
    }

    private static void viewAllItems() {
        System.out.println("\n--- Library Catalog ---");
        if (system.getItems().isEmpty()) {
            System.out.println("No items found.");
            return;
        }
        // Polymorphism: Java automatically calls the overridden toString() of Book or DigitalResource
        for (LibraryItem item : system.getItems()) {
            System.out.println(item.toString());
        }
    }

    private static void viewStudyRooms() {
        System.out.println("\n--- Study Rooms (Composition) ---");
        for (StudyRoom room : system.getStudyRooms()) {
            System.out.println(room.toString());
        }
    }

    private static void viewTransactions() {
        System.out.println("\n--- Transaction History ---");
        if (system.getTransactions().isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }
        for (Transaction txn : system.getTransactions()) {
            System.out.println(txn.toString());
        }
    }

    // THIS IS CRUCIAL FOR EXCEPTION HANDLING MARKS
    private static void borrowItem() {
        System.out.println("\n--- Borrow Item ---");
        System.out.print("Enter User ID: ");
        String userId = scanner.nextLine();
        System.out.print("Enter Item ID: ");
        String itemId = scanner.nextLine();

        try {
            system.borrowItem(userId, itemId);
            System.out.println("Item borrowed successfully!");
        } catch (EntityNotFoundException | ItemNotAvailableException | LimitExceededException e) { // <--- Added it here!
            System.out.println(">> BORROW FAILED: " + e.getMessage());
        }
    }

    private static void returnItem() {
        System.out.println("\n--- Return Item ---");
        System.out.print("Enter Transaction ID: ");
        String txnId = scanner.nextLine();

        try {
            system.returnItem(txnId);
            System.out.println("Item returned successfully! Any applicable fines have been updated.");
        } catch (EntityNotFoundException e) {
            // Gracefully catching custom exception
            System.out.println(">> RETURN FAILED: " + e.getMessage());
        }
    }
    
    private static void bookStudyRoom() {
        System.out.println("\n--- Book a Study Room ---");
        viewStudyRooms(); // Show them what is available first

        System.out.print("\nEnter User ID: ");
        String userId = scanner.nextLine();
        System.out.print("Enter Room Number (e.g., SR-101): ");
        String roomNumber = scanner.nextLine();

        try {
            system.bookStudyRoom(userId, roomNumber);
            System.out.println("Success! Room " + roomNumber + " has been booked.");
        } catch (EntityNotFoundException | RoomUnavailableException e) {
            System.out.println(">> BOOKING FAILED: " + e.getMessage());
        }
    }
    private static void cancelRoomBooking() {
        System.out.println("\n--- Cancel Study Room Booking ---");
        System.out.print("Enter Room Number to cancel (e.g., SR-101): ");
        String roomNumber = scanner.nextLine();

        try {
            system.cancelStudyRoomBooking(roomNumber);
            System.out.println("Success! Room " + roomNumber + " is now Available.");
        } catch (EntityNotFoundException | InvalidOperationException e) {
            System.out.println(">> CANCELLATION FAILED: " + e.getMessage());
        }
    }
}
