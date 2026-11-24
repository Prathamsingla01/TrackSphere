package com.tracksphere.concurrency;

import java.util.ArrayList;
import java.util.List;

/**
 * SharedSeatInventory.java
 *
 * Maintains shared seat inventory accessed concurrently by multiple booking threads.
 * Demonstrates:
 *  1. Race condition demonstration (unsynchronized method bookSeatUnsafe).
 *  2. Synchronization solution (synchronized method bookSeatSafe) using intrinsic lock (Lecture 30-31).
 *  3. StringBuffer for thread-safe mutable string logging (Lecture 6-9).
 *  4. Critical section protection and prevention of duplicate seat allocation.
 *
 * Evaluation: Programming in Java (25CSE0202).
 */
public class SharedSeatInventory {

    private int totalSeats;
    private int availableSeats;
    private int racSeats;
    private int waitingListSeats;

    // Next seat number to allot (1-indexed)
    private int nextSeatNumber;

    // Track allocated seat numbers to verify duplicate allocations
    private List<Integer> allocatedSeatNumbers;

    // Thread-safe log using StringBuffer (Lecture 6-9)
    private StringBuffer auditLog;

    /**
     * Parameterized constructor.
     *
     * @param totalSeats       Total confirmed seats available
     * @param racSeats         RAC capacity
     * @param waitingListSeats Waiting list capacity
     */
    public SharedSeatInventory(int totalSeats, int racSeats, int waitingListSeats) {
        this.totalSeats = totalSeats;
        this.availableSeats = totalSeats;
        this.racSeats = racSeats;
        this.waitingListSeats = waitingListSeats;
        this.nextSeatNumber = 1;
        this.allocatedSeatNumbers = new ArrayList<Integer>();
        this.auditLog = new StringBuffer();
    }

    /**
     * UNSAFE BOOKING METHOD (Intentionally Unsynchronized)
     * Demonstrates a classic Race Condition:
     * Multiple threads check availableSeats, find it > 0, sleep, and then both
     * decrement the count and allocate the SAME seat number!
     *
     * @param passengerName Name of booking passenger
     * @param threadName    Name of thread performing booking
     * @return true if booked, false otherwise
     */
    public boolean bookSeatUnsafe(String passengerName, String threadName) {
        // STEP 1: Check availability (Non-atomic read)
        if (availableSeats > 0) {
            try {
                // Controlled delay simulating network latency and context switching
                // During this delay, another thread enters and also passes (availableSeats > 0)!
                Thread.sleep(120);
            } catch (InterruptedException e) {
                // Handled
            }

            // STEP 2: Non-atomic modification of shared critical section
            int assignedSeat = nextSeatNumber;
            nextSeatNumber++;
            availableSeats--;

            allocatedSeatNumbers.add(assignedSeat);

            String logMsg = String.format("   [UNSAFE] %-18s by %-18s --> Allocated Seat #%d (Remaining: %d)\n",
                    passengerName, threadName, assignedSeat, availableSeats);
            auditLog.append(logMsg);
            System.out.print(logMsg);
            return true;
        } else {
            String logMsg = String.format("   [UNSAFE FAILED] %-18s by %-18s --> No seats remaining!\n",
                    passengerName, threadName);
            auditLog.append(logMsg);
            System.out.print(logMsg);
            return false;
        }
    }

    /**
     * SAFE BOOKING METHOD (Synchronized on intrinsic lock)
     * Demonstrates thread-safe critical section protection (Lecture 30-31).
     * Only ONE thread can execute this method at any time on this SharedSeatInventory object.
     *
     * @param passengerName Name of booking passenger
     * @param threadName    Name of thread performing booking
     * @return true if booked (Confirmed, RAC, or WL), false if completely exhausted
     */
    public synchronized boolean bookSeatSafe(String passengerName, String threadName) {
        // CRITICAL SECTION START - Mutex guaranteed by synchronized keyword
        try {
            // Small simulated delay inside critical section
            Thread.sleep(40);

            if (availableSeats > 0) {
                // Confirmed Seat Allocation
                int assignedSeat = nextSeatNumber;
                nextSeatNumber++;
                availableSeats--;

                allocatedSeatNumbers.add(assignedSeat);

                String logMsg = String.format("   [CONFIRMED] %-16s by %-18s --> Seat #%-2d | Remaining Confirmed: %d\n",
                        passengerName, threadName, assignedSeat, availableSeats);
                auditLog.append(logMsg);
                System.out.print(logMsg);
                return true;

            } else if (racSeats > 0) {
                // RAC Allocation
                racSeats--;
                String logMsg = String.format("   [RAC]       %-16s by %-18s --> RAC Slot Allotted | Remaining RAC: %d\n",
                        passengerName, threadName, racSeats);
                auditLog.append(logMsg);
                System.out.print(logMsg);
                return true;

            } else if (waitingListSeats > 0) {
                // Waiting List Allocation
                waitingListSeats--;
                String logMsg = String.format("   [WL]        %-16s by %-18s --> WL Slot Allotted  | Remaining WL: %d\n",
                        passengerName, threadName, waitingListSeats);
                auditLog.append(logMsg);
                System.out.print(logMsg);
                return true;

            } else {
                // All Quotas Exhausted
                String logMsg = String.format("   [REJECTED]  %-16s by %-18s --> ALL SEATS EXHAUSTED\n",
                        passengerName, threadName);
                auditLog.append(logMsg);
                System.out.print(logMsg);
                return false;
            }

        } catch (InterruptedException e) {
            System.out.println("   [Error] Thread interrupted during safe booking.");
            return false;
        }
        // CRITICAL SECTION END - Lock automatically released
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public int getRacSeats() {
        return racSeats;
    }

    public int getWaitingListSeats() {
        return waitingListSeats;
    }

    public List<Integer> getAllocatedSeatNumbers() {
        return allocatedSeatNumbers;
    }

    public String getAuditLog() {
        return auditLog.toString();
    }
}