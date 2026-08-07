/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;
import core.LibrarySystem;
import model.*;
import java.io.*;
import java.time.LocalDate;

/**
 *
 * @author marcoang
 */



public class FileHandler {

    private static final String USERS_FILE = "users.txt";
    private static final String ITEMS_FILE = "items.txt";
    private static final String TXN_FILE = "transactions.txt";

   
    // SAVE DATA OPERATIONS

    public static void saveAllData(LibrarySystem system) {
        saveUsers(system);
        saveItems(system);
        saveTransactions(system);
    }

    private static void saveUsers(LibrarySystem system) {
        // try-with-resources automatically closes the PrintWriter
        try (PrintWriter pw = new PrintWriter(new FileWriter(USERS_FILE))) {
            for (User u : system.getUsers()) {
                pw.println(u.toCSVString()); // Polymorphism in action!
            }
        } catch (IOException e) {
            System.out.println("Error saving users: " + e.getMessage());
        }
    }

    private static void saveItems(LibrarySystem system) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ITEMS_FILE))) {
            for (LibraryItem item : system.getItems()) {
                pw.println(item.toCSVString()); // Polymorphism in action!
            }
        } catch (IOException e) {
            System.out.println("Error saving items: " + e.getMessage());
        }
    }

    private static void saveTransactions(LibrarySystem system) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(TXN_FILE))) {
            for (Transaction txn : system.getTransactions()) {
                pw.println(txn.toCSVString());
            }
        } catch (IOException e) {
            System.out.println("Error saving transactions: " + e.getMessage());
        }
    }

    
    // LOAD DATA OPERATIONS
   
    public static void loadAllData(LibrarySystem system) {
        loadUsers(system);
        loadItems(system);
        loadTransactions(system);
    }

    private static void loadUsers(LibrarySystem system) {
        File file = new File(USERS_FILE);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                String type = data[0];
                String id = data[1];
                String name = data[2];
                String email = data[3];
                double fine = Double.parseDouble(data[4]);

                // Instantiating the correct subclass
                if (type.equals("Student")) {
                    system.registerUser(new Student(id, name, email, fine, data[5]));
                } else if (type.equals("Faculty")) {
                    system.registerUser(new Faculty(id, name, email, fine, data[5]));
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading users: " + e.getMessage());
        }
    }

    private static void loadItems(LibrarySystem system) {
        File file = new File(ITEMS_FILE);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                String type = data[0];
                String id = data[1];
                String title = data[2];
                boolean isAvail = Boolean.parseBoolean(data[3]);

                if (type.equals("Book")) {
                    system.addLibraryItem(new Book(id, title, isAvail, data[4], data[5], data[6]));
                } else if (type.equals("DigitalResource")) {
                    system.addLibraryItem(new DigitalResource(id, title, isAvail, data[4], Double.parseDouble(data[5])));
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading items: " + e.getMessage());
        }
    }

    private static void loadTransactions(LibrarySystem system) {
        File file = new File(TXN_FILE);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                String txnId = data[0];
                String userId = data[1];
                String itemId = data[2];
                LocalDate issueDate = LocalDate.parse(data[3]);
                LocalDate dueDate = LocalDate.parse(data[4]);
                boolean isReturned = Boolean.parseBoolean(data[5]);

                // CRITICAL OOP STEP: Rebuilding Object Association
                try {
                    User user = system.findUserById(userId);
                    LibraryItem item = system.findItemById(itemId);
                    
                    Transaction txn = new Transaction(txnId, user, item, issueDate, dueDate, isReturned);
                    system.getTransactions().add(txn);
                } catch (exception.EntityNotFoundException e) {
                    System.out.println("Warning: Could not rebuild transaction " + txnId + " due to missing records.");
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading transactions: " + e.getMessage());
        }
    }
}
