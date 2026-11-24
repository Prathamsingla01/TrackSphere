package com.tracksphere;

import com.tracksphere.concurrency.BookingManager;
import com.tracksphere.exception.InvalidBookingException;
import com.tracksphere.exception.SeatNotAvailableException;
import com.tracksphere.model.Booking;
import com.tracksphere.model.Passenger;
import com.tracksphere.model.Ticket;
import com.tracksphere.model.Train;
import com.tracksphere.service.BookingService;
import com.tracksphere.service.CancellationService;
import com.tracksphere.service.PassengerService;
import com.tracksphere.service.SeatAllocationService;
import com.tracksphere.service.TrainService;
import com.tracksphere.service.WaitlistService;
import com.tracksphere.util.InputValidator;
import com.tracksphere.util.RailwayData;

import java.util.List;
import java.util.Scanner;

/**
 * Main.java
 *
 * Master Console Application for "TrackSphere - IRCTC Railway Reservation
 * Simulator".
 * Developed for University Evaluation 1 (Course: 25CSE0202 - Programming in
 * Java).
 *
 * Coordinates service, model, concurrency, exception, and utility classes.
 * Streamlined 15-option menu adhering strictly to Lectures 1-31.
 *
 * Evaluation: Programming in Java (25CSE0202).
 */
public class Main {

    // Service layers
    private static TrainService trainService;
    private static PassengerService passengerService;
    private static SeatAllocationService seatAllocationService;
    private static WaitlistService waitlistService;
    private static BookingService bookingService;
    private static CancellationService cancellationService;
    private static BookingManager bookingManager;

    public static void main(String[] args) {
        // Initialize all service layers
        trainService = new TrainService();
        passengerService = new PassengerService();
        seatAllocationService = new SeatAllocationService();
        waitlistService = new WaitlistService();
        bookingService = new BookingService(trainService, passengerService, seatAllocationService, waitlistService);
        cancellationService = new CancellationService();
        bookingManager = new BookingManager();

        // Preload sample realistic train routes and passengers
        RailwayData.initializeTrains(trainService, seatAllocationService);
        RailwayData.initializeSamplePassengers(passengerService);

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out
                .println("==========================================================================================");
        System.out
                .println("              WELCOME TO TRACKSPHERE - IRCTC RAILWAY RESERVATION SIMULATOR                ");
        System.out
                .println("                   Course: Programming in Java (25CSE0202) - Evaluation 1                 ");
        System.out
                .println("==========================================================================================");
        System.out.println("[System Ready] 10 Express Trains and 5 Initial Passengers Loaded in Memory.");

        while (running) {
            displayMainMenu();
            int choice = InputValidator.readValidInteger(scanner, "Enter your choice: ", 1, 15);

            switch (choice) {
                case 1:
                    handleRegisterPassenger(scanner);
                    break;
                case 2:
                    handleViewRegisteredPassengers();
                    break;
                case 3:
                    handleSearchTrains(scanner);
                    break;
                case 4:
                    handleDisplayAllTrains(scanner);
                    break;
                case 5:
                    handleBookTicket(scanner);
                    break;
                case 6:
                    handleCheckTicketUsingPNR(scanner);
                    break;
                case 7:
                    handleViewBookingHistory();
                    break;
                case 8:
                    handleViewSeatAvailability(scanner);
                    break;
                case 9:
                    handleCancelTicket(scanner);
                    break;
                case 10:
                    handleViewWaitingList(scanner);
                    break;
                case 11:
                    handleSimulateConcurrentBookings();
                    break;
                case 12:
                    handleDemonstrateRaceCondition();
                    break;
                case 13:
                    handleDemonstrateSynchronization();
                    break;
                case 14:
                    handleDemonstrateThreadLifecycle();
                    break;
                case 15:
                    running = false;
                    System.out.println(
                            "\n==========================================================================================");
                    System.out.println(
                            " Thank you for using TrackSphere IRCTC Simulator! System shutting down cleanly. Goodbye! ");
                    System.out.println(
                            "==========================================================================================");
                    break;
                default:
                    System.out.println("Invalid selection. Please choose between 1 and 15.");
                    break;
            }

            if (running) {
                System.out.print("\nPress ENTER to return to Main Menu...");
                scanner.nextLine();
            }
        }

        scanner.close();
    }

    /**
     * Displays the streamlined main menu.
     */
    private static void displayMainMenu() {
        System.out.println("\n==================================================");
        System.out.println("          IRCTC RAILWAY RESERVATION               ");
        System.out.println("==================================================");
        System.out.println("1.  Register Passenger");
        System.out.println("2.  View Registered Passengers");
        System.out.println("3.  Search Trains");
        System.out.println("4.  Display All Trains");
        System.out.println("5.  Book Ticket");
        System.out.println("6.  Check Ticket Using PNR");
        System.out.println("7.  View Booking History");
        System.out.println("8.  View Seat Availability");
        System.out.println("9.  Cancel Ticket");
        System.out.println("10. View Waiting List");
        System.out.println("11. Simulate Concurrent Bookings");
        System.out.println("12. Demonstrate Race Condition");
        System.out.println("13. Demonstrate Synchronization");
        System.out.println("14. Demonstrate Thread Lifecycle");
        System.out.println("15. Exit");
        System.out.println("==================================================");
    }

    /**
     * OPTION 1: Register Passenger
     */
    private static void handleRegisterPassenger(Scanner scanner) {
        System.out.println("\n--- PASSENGER REGISTRATION ---");
        String name = InputValidator.readValidPassengerName(scanner, "Enter Passenger Full Name: ");
        int age = InputValidator.readValidInteger(scanner, "Enter Age (1 - 120): ", 1, 120);

        System.out.println("Select Gender: 1. Male  2. Female  3. Transgender");
        int genderChoice = InputValidator.readValidInteger(scanner, "Enter choice (1-3): ", 1, 3);
        String gender = (genderChoice == 1) ? "Male" : (genderChoice == 2 ? "Female" : "Transgender");

        String mobile = InputValidator.readValidMobile(scanner, "Enter 10-digit Mobile Number: ");

        Passenger passenger = passengerService.registerPassenger(name, age, gender, mobile);
        System.out.println("\n[Success] Passenger Registered Successfully!");
        passenger.displayDetails();
    }

    /**
     * OPTION 2: View Registered Passengers
     */
    private static void handleViewRegisteredPassengers() {
        passengerService.displayAllPassengers();
    }

    /**
     * OPTION 3: Search Trains by Route or Train Number
     */
    private static void handleSearchTrains(Scanner scanner) {
        System.out.println("\n--- SEARCH TRAINS ---");
        System.out.println("1. Search by Source and Destination City");
        System.out.println("2. Search by Train Number");
        int searchChoice = InputValidator.readValidInteger(scanner, "Enter choice (1-2): ", 1, 2);

        if (searchChoice == 1) {
            String source = InputValidator.readValidString(scanner,
                    "Enter Departure City (e.g. Delhi, Chandigarh, Mumbai): ");
            String dest = InputValidator.readValidString(scanner,
                    "Enter Destination City (e.g. Bhopal, Jaipur, Kolkata): ");

            List<Train> results = trainService.searchTrains(source, dest);
            if (results.isEmpty()) {
                System.out.printf("[Notice] No trains currently scheduled from %s to %s.\n", source, dest);
            } else {
                System.out.printf("\n[Found %d Train(s) matching '%s' to '%s']\n", results.size(), source, dest);
                System.out.println(
                        "----------------------------------------------------------------------------------------------------------------------------------------");
                for (Train t : results) {
                    t.displayTrainInfo();
                }
                System.out.println(
                        "----------------------------------------------------------------------------------------------------------------------------------------");
            }
        } else {
            int trainNo = InputValidator.readValidInteger(scanner, "Enter 5-digit Train Number (e.g. 12002): ", 10000,
                    99999);
            Train train = trainService.findTrainByNumber(trainNo);
            if (train != null) {
                System.out.println("\n[Train Details]");
                train.displayTrainInfo();
                train.displayCoachLayout();
            } else {
                System.out.println("[Error] No train found with number #" + trainNo);
            }
        }
    }

    /**
     * OPTION 4: Display All Trains
     */
    private static void handleDisplayAllTrains(Scanner scanner) {
        System.out.println("\nChoose Display & Sorting Mode:");
        System.out.println("1. Display in Natural Order (Train Number)");
        System.out.println("2. Sort by Lowest Ticket Fare (Ascending)");
        System.out.println("3. Sort by Departure Time (Earliest First)");
        int sortChoice = InputValidator.readValidInteger(scanner, "Enter choice (1-3): ", 1, 3);

        if (sortChoice == 2) {
            trainService.sortByFare();
            System.out.println("\n[Sorted by Ticket Fare - Lowest to Highest]");
        } else if (sortChoice == 3) {
            trainService.sortByDepartureTime();
            System.out.println("\n[Sorted by Departure Time - Earliest First]");
        } else {
            trainService.sortByTrainNumber();
            System.out.println("\n[Sorted by Train Number]");
        }

        trainService.displayAllTrains();
    }

    /**
     * OPTION 5: Book Ticket
     */
    private static void handleBookTicket(Scanner scanner) {
        System.out.println("\n--- BOOK RAILWAY TICKET ---");

        // Step 1: Passenger Selection
        System.out.println("1. Select existing registered passenger");
        System.out.println("2. Register and book new passenger now");
        int pChoice = InputValidator.readValidInteger(scanner, "Enter choice (1-2): ", 1, 2);

        String passengerId;
        if (pChoice == 1) {
            passengerService.displayAllPassengers();
            passengerId = InputValidator.readValidString(scanner, "Enter Passenger ID (e.g. PS-101): ");
            if (passengerService.getPassengerById(passengerId) == null) {
                System.out.println("[Error] Passenger ID not found. Aborting booking.");
                return;
            }
        } else {
            String name = InputValidator.readValidPassengerName(scanner, "Enter Passenger Full Name: ");
            int age = InputValidator.readValidInteger(scanner, "Enter Age (1 - 120): ", 1, 120);
            System.out.println("Select Gender: 1. Male  2. Female  3. Transgender");
            int gChoice = InputValidator.readValidInteger(scanner, "Enter choice (1-3): ", 1, 3);
            String gender = (gChoice == 1) ? "Male" : (gChoice == 2 ? "Female" : "Transgender");
            String mobile = InputValidator.readValidMobile(scanner, "Enter 10-digit Mobile Number: ");
            Passenger newP = passengerService.registerPassenger(name, age, gender, mobile);
            passengerId = newP.getPassengerId();
            System.out.println("[Registered] Assigned ID: " + passengerId);
        }

        // Step 2: Train Selection
        trainService.displayAllTrains();
        int trainNumber = InputValidator.readValidInteger(scanner, "Enter Train Number to Book (e.g. 12002): ", 10000,
                99999);
        Train train = trainService.findTrainByNumber(trainNumber);
        if (train == null) {
            System.out.println("[Error] Invalid train number. Aborting booking.");
            return;
        }

        // Step 3: Berth Preference Selection
        System.out.println("\nSelect Preferred Berth Type:");
        System.out.println("1. LOWER Berth (Automatic priority for senior citizens)");
        System.out.println("2. MIDDLE Berth");
        System.out.println("3. UPPER Berth");
        System.out.println("4. SIDE_LOWER Berth");
        System.out.println("5. SIDE_UPPER Berth");
        System.out.println("6. NO PREFERENCE (First available)");
        int bChoice = InputValidator.readValidInteger(scanner, "Enter berth preference (1-6): ", 1, 6);
        String[] berthOptions = { "LOWER", "MIDDLE", "UPPER", "SIDE_LOWER", "SIDE_UPPER", "NO_PREFERENCE" };
        String preferredBerth = berthOptions[bChoice - 1];

        // Step 4: Payment Mode Selection
        System.out.println("\nSelect Payment Mode:");
        System.out.println("1. UPI (Google Pay / PhonePe / Paytm)");
        System.out.println("2. Debit Card");
        System.out.println("3. Credit Card");
        System.out.println("4. Net Banking");
        int payChoice = InputValidator.readValidInteger(scanner, "Enter payment choice (1-4): ", 1, 4);
        String[] payModes = { "UPI", "DEBIT_CARD", "CREDIT_CARD", "NET_BANKING" };
        String paymentMode = payModes[payChoice - 1];

        // Step 5: Execute Booking
        try {
            System.out.println("\n[Processing Reservation Request...]");
            Booking booking = bookingService.bookTicket(passengerId, trainNumber, preferredBerth, paymentMode);

            System.out.println("\n>>> RESERVATION SUCCESSFUL! <<<");
            booking.displayBookingReceipt();
            System.out.println(booking.getTicket().generateTicketSummary());

        } catch (InvalidBookingException e) {
            System.out.println("\n[BOOKING REJECTED - Business Rule Violation]");
            System.out.println("Reason: " + e.getMessage());
        } catch (SeatNotAvailableException e) {
            System.out.println("\n[BOOKING REJECTED - Quota Exhausted]");
            System.out.println("Reason: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\n[System Error] Unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * OPTION 6: Check Ticket Using PNR
     */
    private static void handleCheckTicketUsingPNR(Scanner scanner) {
        System.out.println("\n--- PNR STATUS ENQUIRY ---");
        String pnr = InputValidator.readValidString(scanner, "Enter 10-digit PNR Number: ");
        Ticket ticket = bookingService.getTicketByPNR(pnr);
        if (ticket != null) {
            System.out.println(ticket.generateTicketSummary());
        } else {
            System.out.println("[Error] No ticket found matching PNR #" + pnr + ". Please verify the number.");
        }
    }

    /**
     * OPTION 7: View Booking History
     */
    private static void handleViewBookingHistory() {
        bookingService.displayBookingHistory();
    }

    /**
     * OPTION 8: View Seat Availability and Coach Layout
     */
    private static void handleViewSeatAvailability(Scanner scanner) {
        System.out.println("\n--- CHECK SEAT AVAILABILITY ---");
        int trainNo = InputValidator.readValidInteger(scanner, "Enter Train Number (e.g. 12002): ", 10000, 99999);
        trainService.displaySeatAvailability(trainNo);

        Train train = trainService.findTrainByNumber(trainNo);
        if (train != null) {
            System.out.print("Do you wish to inspect the 2D Coach Seating Layout? (1. Yes / 2. No): ");
            int layoutChoice = InputValidator.readValidInteger(scanner, "", 1, 2);
            if (layoutChoice == 1) {
                train.displayCoachLayout();
            }
        }
    }

    /**
     * OPTION 9: Cancel Ticket
     */
    private static void handleCancelTicket(Scanner scanner) {
        System.out.println("\n--- TICKET CANCELLATION & REFUND ---");
        String pnr = InputValidator.readValidString(scanner, "Enter 10-digit PNR Number to Cancel: ");

        try {
            cancellationService.cancelTicket(pnr, bookingService, trainService, seatAllocationService, waitlistService);
        } catch (InvalidBookingException e) {
            System.out.println("\n[Cancellation Failed] " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\n[System Error] Unexpected error during cancellation: " + e.getMessage());
        }
    }

    /**
     * OPTION 10: View Waiting List
     */
    private static void handleViewWaitingList(Scanner scanner) {
        System.out.println("\n--- WAITING LIST PASSENGER QUEUE ---");
        int trainNo = InputValidator.readValidInteger(scanner, "Enter Train Number (e.g. 12002): ", 10000, 99999);
        waitlistService.displayWaitingList(trainNo);
    }

    /**
     * OPTION 11: Simulate Concurrent Bookings
     */
    private static void handleSimulateConcurrentBookings() {
        bookingManager.simulateConcurrentBookings();
    }

    /**
     * OPTION 12: Demonstrate Race Condition (Unsafe)
     */
    private static void handleDemonstrateRaceCondition() {
        bookingManager.demonstrateRaceCondition();
    }

    /**
     * OPTION 13: Demonstrate Synchronization (Safe Solution)
     */
    private static void handleDemonstrateSynchronization() {
        bookingManager.demonstrateSynchronization();
    }

    /**
     * OPTION 14: Demonstrate Thread Lifecycle and States
     */
    private static void handleDemonstrateThreadLifecycle() {
        bookingManager.demonstrateThreadLifecycle();
    }
}