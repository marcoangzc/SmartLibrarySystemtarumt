/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package core;
import model.*;
import exception.*;
import java.time.LocalDate;
import java.util.ArrayList;
/**
 *
 * @author marcoang
 */


public class LibrarySystem {
    
    // AGGREGATION: LibrarySystem "has-a" collection of Users, Items, and Transactions.
    // They can conceptually exist independently (e.g., loaded from a file).
    private ArrayList<User> users;
    private ArrayList<LibraryItem> items;
    private ArrayList<Transaction> transactions;

    // COMPOSITION: LibrarySystem completely owns and controls the lifecycle of StudyRooms.
    // If the LibrarySystem is destroyed, the StudyRooms are conceptually destroyed.
    private ArrayList<StudyRoom> studyRooms;

    public LibrarySystem() {
        this.users = new ArrayList<>();
        this.items = new ArrayList<>();
        this.transactions = new ArrayList<>();
        
        // Implementing Composition: The System creates its own parts internally.
        this.studyRooms = new ArrayList<>();
        this.studyRooms.add(new StudyRoom("SR-101", 4));
        this.studyRooms.add(new StudyRoom("SR-102", 6));
        this.studyRooms.add(new StudyRoom("SR-103", 2));
    }

    // --- AGGREGATION LIST GETTERS/SETTERS (Used by FileHandler later) ---
    public ArrayList<User> getUsers() { return users; }
    public void setUsers(ArrayList<User> users) { this.users = users; }

    public ArrayList<LibraryItem> getItems() { return items; }
    public void setItems(ArrayList<LibraryItem> items) { this.items = items; }

    public ArrayList<Transaction> getTransactions() { return transactions; }
    public void setTransactions(ArrayList<Transaction> transactions) { this.transactions = transactions; }

    public ArrayList<StudyRoom> getStudyRooms() { return studyRooms; }

    // --- CRUD: CREATE ---
    public void registerUser(User user) {
        users.add(user);
    }

    public void addLibraryItem(LibraryItem item) {
        items.add(item);
    }

    // --- CRUD: READ (With Custom Exceptions) ---
    public User findUserById(String userId) throws EntityNotFoundException {
        for (User u : users) {
            if (u.getId().equalsIgnoreCase(userId)) {
                return u;
            }
        }
        throw new EntityNotFoundException("Error: User with ID '" + userId + "' was not found.");
    }

    public LibraryItem findItemById(String itemId) throws EntityNotFoundException {
        for (LibraryItem item : items) {
            if (item.getId().equalsIgnoreCase(itemId)) {
                return item;
            }
        }
        throw new EntityNotFoundException("Error: Item with ID '" + itemId + "' was not found in the catalog.");
    }

    // --- BUSINESS LOGIC: BORROW ITEM ---
    // Notice the 'throws' keyword. We force the caller (Main UI) to handle the errors!
     public void borrowItem(String userId, String itemId) throws EntityNotFoundException, ItemNotAvailableException, LimitExceededException {
        User user = findUserById(userId);
        LibraryItem item = findItemById(itemId);

        if (!item.isAvailable()) {
            throw new ItemNotAvailableException("Sorry, the item '" + item.getTitle() + "' is currently on loan.");
        }

        // --- NEW LOGIC: ENFORCE BORROW LIMIT ---
        int activeLoans = 0;
        for (Transaction txn : transactions) {
            if (txn.getUser().getId().equalsIgnoreCase(userId) && !txn.isReturned()) {
                activeLoans++;
            }
        }
        
        if (activeLoans >= user.getMaxBorrowLimit()) {
            throw new LimitExceededException("User " + user.getName() + " has reached their maximum limit of " + user.getMaxBorrowLimit() + " items.");
        }
        // ----------------------------------------

        // Logic: Update item status
        item.setAvailable(false);

        // Logic: Create Transaction (Proper Object Association)
        String txnId = "TXN-" + System.currentTimeMillis(); 
        LocalDate issueDate = LocalDate.now();
        LocalDate dueDate = issueDate.plusDays(14); 

        Transaction newTxn = new Transaction(txnId, user, item, issueDate, dueDate, false);
        transactions.add(newTxn);
    }

    // --- BUSINESS LOGIC: RETURN ITEM ---
    public void returnItem(String txnId) throws EntityNotFoundException {
        for (Transaction txn : transactions) {
            if (txn.getTransactionId().equalsIgnoreCase(txnId) && !txn.isReturned()) {
                // Update transaction status
                txn.setReturned(true);
                
                // Update item availability (Accessing actual object via Association)
                txn.getItem().setAvailable(true);
                
                // Fine calculation logic (Mock implementation: RM 1.50 per day late)
                LocalDate today = LocalDate.now();
                if (today.isAfter(txn.getDueDate())) {
                    long daysLate = java.time.temporal.ChronoUnit.DAYS.between(txn.getDueDate(), today);
                    double fine = daysLate * 1.50;
                    User user = txn.getUser();
                    user.setFineBalance(user.getFineBalance() + fine);
                }
                return; // Exit successfully
            }
        }
        throw new EntityNotFoundException("Error: Active Transaction ID '" + txnId + "' not found.");
    }
    // --- HELPER METHOD: FIND ROOM ---
    public StudyRoom findRoomByNumber(String roomNumber) throws EntityNotFoundException {
        for (StudyRoom room : studyRooms) {
            if (room.getRoomNumber().equalsIgnoreCase(roomNumber)) {
                return room;
            }
        }
        throw new EntityNotFoundException("Error: Study Room '" + roomNumber + "' does not exist.");
    }

    // --- BUSINESS LOGIC: BOOK A ROOM ---
    public void bookStudyRoom(String userId, String roomNumber) throws EntityNotFoundException, RoomUnavailableException {
        User user = findUserById(userId);
        StudyRoom room = findRoomByNumber(roomNumber);

        if (!room.isAvailable()) {
            throw new RoomUnavailableException("Sorry, room " + roomNumber + " is already booked by another user.");
        }

        // Logic: Associate the user with the room
        room.setBookedBy(user);
    }
    
    // --- BUSINESS LOGIC: CANCEL ROOM BOOKING ---
    public void cancelStudyRoomBooking(String roomNumber) throws EntityNotFoundException, InvalidOperationException {
        // 1. Find the room
        StudyRoom room = findRoomByNumber(roomNumber);

        // 2. Check if it is actually booked
        if (room.isAvailable()) {
            throw new InvalidOperationException("Cannot cancel: Room " + roomNumber + " is already available.");
        }

        // 3. Revert to normal status by removing the User association (setting it to null)
        room.setBookedBy(null); 
    }
}