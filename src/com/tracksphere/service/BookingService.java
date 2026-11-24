package com.tracksphere.service;

import com.tracksphere.exception.InvalidBookingException;
import com.tracksphere.exception.SeatNotAvailableException;
import com.tracksphere.model.Booking;
import com.tracksphere.model.Passenger;
import com.tracksphere.model.Seat;
import com.tracksphere.model.SeniorCitizenPassenger;
import com.tracksphere.model.Ticket;
import com.tracksphere.model.Train;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * BookingService.java
 *
 * Central coordinator for ticket reservations, PNR generation, and booking
 * records.
 * Demonstrates:
 * 1. HashMap for O(1) PNR-to-Ticket mapping.
 * 2. HashSet for ensuring guaranteed unique 10-digit PNR numbers.
 * 3. ArrayList for historical booking records.
 * 4. LinkedHashSet for maintaining unique chronological booking transaction
 * IDs.
 * 5. Method Overloading for multiple booking variations (Lecture 1-5).
 * 6. Custom Exceptions: InvalidBookingException, SeatNotAvailableException
 * (Lecture 10-15).
 *
 * Evaluation: Programming in Java (25CSE0202).
 */
public class BookingService {

    // Maps 10-digit PNR to Ticket object
    private Map<String, Ticket> ticketMap;

    // Set of all issued PNRs to guarantee uniqueness
    private Set<String> pnrSet;

    // Ordered chronological booking history
    private List<Booking> bookingHistory;

    // LinkedHashSet preserving insertion order of unique booking IDs
    private Set<String> activeBookingIds;

    // Injected Service dependencies
    private TrainService trainService;
    private PassengerService passengerService;
    private SeatAllocationService seatAllocationService;
    private WaitlistService waitlistService;

    // Random generator for realistic 10-digit PNRs
    private Random random;
    private int bookingIdCounter;

    public BookingService(TrainService trainService, PassengerService passengerService,
            SeatAllocationService seatAllocationService, WaitlistService waitlistService) {
        this.trainService = trainService;
        this.passengerService = passengerService;
        this.seatAllocationService = seatAllocationService;
        this.waitlistService = waitlistService;

        this.ticketMap = new HashMap<String, Ticket>();
        this.pnrSet = new HashSet<String>();
        this.bookingHistory = new ArrayList<Booking>();
        this.activeBookingIds = new LinkedHashSet<String>();
        this.random = new Random();
        this.bookingIdCounter = 1001;
    }

    /**
     * Generates a guaranteed-unique 10-digit PNR number.
     * Uses StringBuilder and HashSet.contains() to prevent collisions.
     * Demonstrates String Handling and HashSet (Lecture 6-9, 16-25).
     *
     * @return 10-digit numeric PNR string
     */
    public synchronized String generateUniquePNR() {
        String pnr;
        do {
            StringBuilder sb = new StringBuilder();
            // IRCTC PNRs typically begin with 2, 4, 6, or 8 (representing railway zones)
            int zonePrefix = 2 + (random.nextInt(4) * 2); // 2, 4, 6, 8
            sb.append(zonePrefix);
            // Append 9 random digits
            for (int i = 0; i < 9; i++) {
                sb.append(random.nextInt(10));
            }
            pnr = sb.toString();
        } while (pnrSet.contains(pnr)); // Ensure uniqueness via HashSet

        pnrSet.add(pnr);
        return pnr;
    }

    /**
     * Overloaded method: Books ticket with default payment mode (UPI).
     */
    public Booking bookTicket(String passengerId, int trainNumber, String berthPreference)
            throws InvalidBookingException, SeatNotAvailableException {
        return bookTicket(passengerId, trainNumber, berthPreference, "UPI");
    }

    /**
     * Books a railway ticket with specified payment mode.
     * Handles Confirmed, RAC, and Waiting List allocation.
     *
     * @param passengerId     ID of registered passenger
     * @param trainNumber     Train number
     * @param berthPreference Preferred berth (LOWER, MIDDLE, UPPER, SIDE_LOWER,
     *                        SIDE_UPPER)
     * @param paymentMode     Payment method (UPI, CARD, NET_BANKING)
     * @return Newly created Booking object
     * @throws InvalidBookingException   If inputs or conditions are invalid
     * @throws SeatNotAvailableException If all quotas (Confirmed, RAC, WL) are
     *                                   exhausted
     */
    public Booking bookTicket(String passengerId, int trainNumber, String berthPreference, String paymentMode)
            throws InvalidBookingException, SeatNotAvailableException {

        // Validate passenger
        Passenger passenger = passengerService.getPassengerById(passengerId);
        if (passenger == null) {
            throw new InvalidBookingException(
                    "Booking Error: Passenger ID [" + passengerId + "] not found in registered records.");
        }

        // Validate train
        Train train = trainService.findTrainByNumber(trainNumber);
        if (train == null) {
            throw new InvalidBookingException("Booking Error: Train #" + trainNumber + " does not exist.");
        }

        // Calculate Fare (with Senior Citizen concession if applicable)
        double finalFare = train.getBaseFare();
        if (passenger instanceof SeniorCitizenPassenger) {
            // Polymorphism: Senior citizen concession applied
            SeniorCitizenPassenger senior = (SeniorCitizenPassenger) passenger;
            finalFare = senior.calculateDiscountedFare(finalFare);
        }

        // Allocation hierarchy: 1. Confirmed -> 2. RAC -> 3. Waiting List
        Seat allocatedSeat = null;
        String status = "CONFIRMED";
        int queuePosition = 0;
        String finalBerth = berthPreference;

        // Check confirmed seat availability
        if (train.getAvailableSeats() > 0) {
            // Allocate physical seat
            allocatedSeat = seatAllocationService.allocateSeat(train, passenger, berthPreference);
            if (allocatedSeat != null) {
                finalBerth = allocatedSeat.getBerthType();
                status = "CONFIRMED";
            }
        }

        // If no confirmed seat was allotted
        if (allocatedSeat == null) {
            // Check RAC availability
            if (train.getRacSeats() > 0) {
                status = "RAC";
                finalBerth = "SIDE_LOWER (Shared)";
            } else if (train.getWaitingListSeats() > 0) {
                status = "WAITING_LIST";
                finalBerth = "NONE";
            } else {
                throw new SeatNotAvailableException(
                        "Regret: All tickets (Confirmed, RAC, and Waiting List) are FULL for Train #" +
                                trainNumber + " (" + train.getTrainName() + ").");
            }
        }

        // Generate unique 10-digit PNR
        String pnr = generateUniquePNR();
        String bookingDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        // Create Ticket
        Ticket ticket = new Ticket(pnr, passenger, train, allocatedSeat, finalBerth, finalFare, status,
                bookingDate, queuePosition);

        // If RAC or Waiting List, enqueue into WaitlistService
        if ("RAC".equalsIgnoreCase(status)) {
            queuePosition = waitlistService.addToRAC(train, ticket);
        } else if ("WAITING_LIST".equalsIgnoreCase(status)) {
            queuePosition = waitlistService.addToWaitingList(train, ticket);
        }

        // Store Ticket in Map
        ticketMap.put(pnr, ticket);

        // Create Booking transaction record
        String bookingId = "BK-" + bookingIdCounter++;
        Booking booking = new Booking(bookingId, ticket, bookingDate, paymentMode, "SUCCESS");

        bookingHistory.add(booking);
        activeBookingIds.add(bookingId);

        return booking;
    }

    /**
     * Retrieves a Ticket by PNR in O(1) time using HashMap.
     *
     * @param pnr 10-digit PNR string
     * @return Ticket object or null
     */
    public Ticket getTicketByPNR(String pnr) {
        if (pnr == null) {
            return null;
        }
        return ticketMap.get(pnr.trim());
    }

    public List<Booking> getBookingHistory() {
        return bookingHistory;
    }

    public Map<String, Ticket> getTicketMap() {
        return ticketMap;
    }

    /**
     * Displays all booking history entries.
     */
    public void displayBookingHistory() {
        if (bookingHistory.isEmpty()) {
            System.out.println("[Notice] No booking transactions recorded yet.");
            return;
        }

        System.out.println(
                "\n=========================================================================================================");
        System.out.println(
                "                                      TRACKSPHERE BOOKING HISTORY                                        ");
        System.out.println(
                "=========================================================================================================");
        System.out.printf("| %-8s | %-10s | %-18s | %-6s | %-16s | %-10s | %-10s |\n",
                "BOOK ID", "PNR", "PASSENGER", "TRAIN", "STATUS", "PAY MODE", "AMOUNT");
        System.out.println(
                "---------------------------------------------------------------------------------------------------------");

        for (Booking b : bookingHistory) {
            Ticket t = b.getTicket();
            System.out.printf("| %-8s | %-10s | %-18s | #%-5d | %-16s | %-10s | Rs.%-7.2f |\n",
                    b.getBookingId(), t.getPnr(), t.getPassenger().getName(),
                    t.getTrain().getTrainNumber(), t.getFormattedStatus(), b.getPaymentMode(), t.getFare());
        }
        System.out.println(
                "=========================================================================================================");
    }
}