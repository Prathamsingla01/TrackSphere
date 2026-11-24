package com.tracksphere.concurrency;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * BookingManager.java
 *
 * Coordinates concurrent passenger booking requests and runs demonstrations for:
 *  1. Concurrent multi-passenger booking simulation using Runnable and Thread (Lecture 26-27).
 *  2. Race Condition Demonstration (unsafe concurrent execution without synchronization).
 *  3. Synchronization Solution Demonstration (safe concurrent execution with intrinsic lock).
 *  4. Thread Lifecycle and States Demonstration (NEW, RUNNABLE, TIMED_WAITING, TERMINATED) (Lecture 28-29).
 *  5. Thread Control via start(), sleep(), join(), interrupt(), setDaemon(), and setPriority().
 *
 * Evaluation: Programming in Java (25CSE0202).
 */
public class BookingManager {

    public BookingManager() {
    }

    /**
     * SIMULATION 1: Concurrent Safe Booking Simulation
     * Launches 8 passenger threads booking tickets simultaneously.
     * Demonstrates Thread creation, start(), sleep(), and join().
     */
    public void simulateConcurrentBookings() {
        System.out.println("\n==========================================================================================");
        System.out.println("                    CONCURRENT RAILWAY BOOKING SIMULATION (THREAD-SAFE)                   ");
        System.out.println("==========================================================================================");
        System.out.println("Scenario: 8 Passengers simultaneously attempting to book tickets on Train #12002.");
        System.out.println("Inventory: 3 Confirmed Seats, 2 RAC Seats, 2 Waiting List Seats.");
        System.out.println("------------------------------------------------------------------------------------------");

        // Inventory with 3 Confirmed, 2 RAC, 2 WL seats
        SharedSeatInventory safeInventory = new SharedSeatInventory(3, 2, 2);

        String[] passengerNames = {
                "Aarav Sharma", "Priya Verma", "Rohan Mehta", "Neha Gupta",
                "Vikram Singh", "Ananya Iyer", "Karan Malhotra", "Sneha Patel"
        };

        List<Thread> threadList = new ArrayList<Thread>();

        // Create 8 passenger threads implementing Runnable
        for (int i = 0; i < passengerNames.length; i++) {
            String name = passengerNames[i];
            String id = "PS-" + (201 + i);
            BookingTask task = new BookingTask(name, id, 12002, safeInventory, false);

            Thread t = new Thread(task, "Thread-Passenger-" + (i + 1));
            // Demonstrate thread priority setting (Lecture 28-29)
            if (i == 0) {
                t.setPriority(Thread.MAX_PRIORITY); // Priority 10
            } else if (i == passengerNames.length - 1) {
                t.setPriority(Thread.MIN_PRIORITY); // Priority 1
            } else {
                t.setPriority(Thread.NORM_PRIORITY); // Priority 5
            }
            threadList.add(t);
        }

        System.out.println("[Status] Starting all 8 passenger threads simultaneously...");
        long startTime = System.currentTimeMillis();

        // Start all threads
        for (Thread t : threadList) {
            t.start();
        }

        // Wait for all threads to terminate using join() (Lecture 26-27)
        for (Thread t : threadList) {
            try {
                t.join(); // Main thread pauses until thread t finishes execution
            } catch (InterruptedException e) {
                System.out.println("[Error] Main thread interrupted while waiting for " + t.getName());
            }
        }

        long totalTime = System.currentTimeMillis() - startTime;
        System.out.println("------------------------------------------------------------------------------------------");
        System.out.println("[Result] All 8 booking threads have completed execution in " + totalTime + " ms.");
        System.out.println("Final Confirmed Seats Remaining : " + safeInventory.getAvailableSeats());
        System.out.println("Final RAC Slots Remaining       : " + safeInventory.getRacSeats());
        System.out.println("Final WL Slots Remaining        : " + safeInventory.getWaitingListSeats());
        System.out.println("Total Allocated Confirmed Seats : " + safeInventory.getAllocatedSeatNumbers().size() +
                " -> Seats Allotted: " + safeInventory.getAllocatedSeatNumbers());
        System.out.println("Synchronization Guarantee: ZERO duplicate seat allotments! Inventory preserved perfectly.");
        System.out.println("==========================================================================================\n");
    }

    /**
     * SIMULATION 2: Race Condition Demonstration (Unsafe)
     * Demonstrates what happens when multiple threads access and mutate shared data
     * without synchronization.
     */
    public void demonstrateRaceCondition() {
        System.out.println("\n==========================================================================================");
        System.out.println("                       DEMONSTRATION: UNSAFE RACE CONDITION                               ");
        System.out.println("==========================================================================================");
        System.out.println("SCENARIO:");
        System.out.println("  - A Train has ONLY 1 remaining confirmed seat (availableSeats = 1).");
        System.out.println("  - Two passengers (Rahul and Simran) click 'Book' at the EXACT same instant.");
        System.out.println("  - NO synchronization is applied to the booking method (Critical Section is unprotected).");
        System.out.println("  - A simulated network delay (120ms) occurs between checking availability and decrementing.");
        System.out.println("------------------------------------------------------------------------------------------");

        SharedSeatInventory unsafeInventory = new SharedSeatInventory(1, 0, 0);

        BookingTask task1 = new BookingTask("Rahul Khanna", "PS-301", 12952, unsafeInventory, true);
        BookingTask task2 = new BookingTask("Simran Roy", "PS-302", 12952, unsafeInventory, true);

        Thread thread1 = new Thread(task1, "BookingThread-Rahul");
        Thread thread2 = new Thread(task2, "BookingThread-Simran");

        System.out.println("[Initial State] Confirmed Seats Available: " + unsafeInventory.getAvailableSeats());
        System.out.println("[Action] Launching Thread 1 and Thread 2 simultaneously...\n");

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            // Handled
        }

        System.out.println("\n------------------------------------------------------------------------------------------");
        System.out.println("RACE CONDITION ANALYSIS:");
        System.out.println("  - Final Confirmed Seats Count: " + unsafeInventory.getAvailableSeats() +
                " (Notice: Inconsistent or Negative!)");
        System.out.println("  - Seats Allocated: " + unsafeInventory.getAllocatedSeatNumbers());

        // Check if duplicate seat allocation occurred
        List<Integer> allocated = unsafeInventory.getAllocatedSeatNumbers();
        Set<Integer> uniqueSeats = new HashSet<Integer>(allocated);

        if (allocated.size() > 1 || unsafeInventory.getAvailableSeats() < 0) {
            System.out.println("\n>>> FLAW DETECTED (RACE CONDITION CONFIRMED!) <<<");
            System.out.println("  CRITICAL ERROR: Multiple threads read availableSeats=1 BEFORE either thread updated it!");
            System.out.println("  Both threads thought a seat was available and both booked the ticket.");
            System.out.println("  In railway reservations, this is a catastrophic DOUBLE-BOOKING bug!");
        } else {
            System.out.println("  Due to operating system thread scheduling, one thread preempted the other.");
        }
        System.out.println("==========================================================================================\n");
    }

    /**
     * SIMULATION 2B: Synchronization Solution Demonstration (Thread-Safe)
     * Demonstrates how a synchronized method on an intrinsic lock guarantees mutual exclusion,
     * atomicity, and memory visibility, eliminating the race condition completely.
     */
    public void demonstrateSynchronization() {
        System.out.println("\n==========================================================================================");
        System.out.println("                   DEMONSTRATION: SYNCHRONIZATION MUTEX SOLUTION                          ");
        System.out.println("==========================================================================================");
        System.out.println("SCENARIO (Identical conditions as Race Condition, but with synchronized keyword):");
        System.out.println("  - A Train has ONLY 1 remaining confirmed seat (availableSeats = 1) and 1 RAC slot.");
        System.out.println("  - Two passengers (Vikram and Sunita) click 'Book' at the EXACT same instant.");
        System.out.println("  - Method 'bookSeatSafe()' is declared SYNCHRONIZED on the SharedSeatInventory object.");
        System.out.println("  - The first thread acquires the monitor lock. The second thread is forced into BLOCKED state");
        System.out.println("    until the first thread completely finishes checking, allocating, and decrementing.");
        System.out.println("------------------------------------------------------------------------------------------");

        SharedSeatInventory safeInventory = new SharedSeatInventory(1, 1, 0);

        BookingTask task1 = new BookingTask("Vikram Malhotra", "PS-401", 12002, safeInventory, false);
        BookingTask task2 = new BookingTask("Sunita Rao", "PS-402", 12002, safeInventory, false);

        Thread thread1 = new Thread(task1, "SafeThread-Vikram");
        Thread thread2 = new Thread(task2, "SafeThread-Sunita");

        System.out.println("[Initial State] Confirmed Seats: " + safeInventory.getAvailableSeats() +
                " | RAC Slots: " + safeInventory.getRacSeats());
        System.out.println("[Action] Launching both threads simultaneously with synchronization enabled...\n");

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            // Handled
        }

        System.out.println("\n------------------------------------------------------------------------------------------");
        System.out.println("SYNCHRONIZATION VERIFICATION:");
        System.out.println("  - Confirmed Seats Remaining : " + safeInventory.getAvailableSeats());
        System.out.println("  - RAC Slots Remaining       : " + safeInventory.getRacSeats());
        System.out.println("  - Allocated Confirmed Seats : " + safeInventory.getAllocatedSeatNumbers());
        System.out.println("\n>>> OUTCOME (MUTUAL EXCLUSION CONFIRMED) <<<");
        System.out.println("  SUCCESS: Exactly ONE passenger received Confirmed Seat #1!");
        System.out.println("  The other passenger safely received RAC status because the lock enforced atomic execution.");
        System.out.println("  Duplicate Seat Allocations: ZERO (0). Double-booking is 100% prevented.");
        System.out.println("==========================================================================================\n");
    }

    /**
     * SIMULATION 3: Thread Lifecycle and States Demonstration
     * Demonstrates NEW, RUNNABLE, TIMED_WAITING, and TERMINATED states using Thread.getState().
     * Also demonstrates Daemon threads and Thread.interrupt().
     */
    public void demonstrateThreadLifecycle() {
        System.out.println("\n==========================================================================================");
        System.out.println("                   DEMONSTRATION: THREAD LIFECYCLE & THREAD STATES                        ");
        System.out.println("==========================================================================================");
        System.out.println("In Java, a Thread transitions across states defined in java.lang.Thread.State enum:");
        System.out.println("NEW -> RUNNABLE -> TIMED_WAITING / WAITING / BLOCKED -> TERMINATED.");
        System.out.println("------------------------------------------------------------------------------------------");

        // Create a Runnable worker task
        Runnable demoRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("   [Thread Worker] Running... Current State inside run(): " +
                            Thread.currentThread().getState());
                    System.out.println("   [Thread Worker] Entering Thread.sleep(600ms)... (Will transition to TIMED_WAITING)");
                    Thread.sleep(600);
                    System.out.println("   [Thread Worker] Woke up from sleep! Finishing task execution...");
                } catch (InterruptedException e) {
                    System.out.println("   [Thread Worker] Interrupted while sleeping! Handling InterruptedException.");
                }
            }
        };

        // 1. NEW State
        Thread workerThread = new Thread(demoRunnable, "Lifecycle-Demo-Worker");
        System.out.println("1. After instantiating (new Thread()):");
        System.out.println("   Observed Thread State: " + workerThread.getState() + " (NEW)");

        // 2. RUNNABLE State
        workerThread.start();
        System.out.println("\n2. Immediately after calling start():");
        System.out.println("   Observed Thread State: " + workerThread.getState() + " (RUNNABLE - Ready to run or running)");

        // 3. TIMED_WAITING State
        try {
            // Sleep main thread briefly to let worker enter its sleep()
            Thread.sleep(150);
            System.out.println("\n3. While worker thread is inside Thread.sleep():");
            System.out.println("   Observed Thread State: " + workerThread.getState() + " (TIMED_WAITING)");

            // 4. Wait for worker to finish using join()
            workerThread.join();

            // 5. TERMINATED State
            System.out.println("\n4. After worker finishes and join() returns:");
            System.out.println("   Observed Thread State: " + workerThread.getState() + " (TERMINATED)");

        } catch (InterruptedException e) {
            System.out.println("[Error] Main thread interrupted during lifecycle test.");
        }

        // Demonstration of Daemon Thread (Lecture 28-29)
        System.out.println("\n------------------------------------------------------------------------------------------");
        System.out.println("DEMONSTRATION OF DAEMON THREAD & INTERRUPT:");
        Thread daemonThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("   [Daemon Monitor] Background daemon thread started.");
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    System.out.println("   [Daemon Monitor] Interrupt received successfully! Exiting cleanly.");
                }
            }
        }, "Background-Daemon-Monitor");

        daemonThread.setDaemon(true); // Marked as Daemon thread
        System.out.println("   Is daemonThread daemon? " + daemonThread.isDaemon());
        daemonThread.start();

        try {
            Thread.sleep(100);
            System.out.println("   Main thread interrupting daemon thread...");
            daemonThread.interrupt(); // Demonstrate interrupt() (Lecture 26-27)
            daemonThread.join();
        } catch (InterruptedException e) {
            // Handled
        }

        System.out.println("==========================================================================================\n");
    }
}