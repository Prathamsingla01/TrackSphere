package com.tracksphere.concurrency;

/**
 * BookingTask.java
 *
 * Implements Runnable to execute a single passenger's booking request inside a dedicated thread.
 * Demonstrates:
 *  1. Runnable interface implementation (Lecture 26-27).
 *  2. Multithreading execution via run() method.
 *  3. Simulated latency using Thread.sleep().
 *  4. Safe vs Unsafe booking dispatch based on demonstration mode.
 *
 * Evaluation: Programming in Java (25CSE0202).
 */
public class BookingTask implements Runnable {

    private String passengerName;
    private String passengerId;
    private int trainNumber;
    private SharedSeatInventory inventory;
    private boolean isUnsafeDemo;

    /**
     * Parameterized constructor.
     *
     * @param passengerName Passenger's full name
     * @param passengerId   Unique passenger identifier
     * @param trainNumber   Target train number
     * @param inventory     Shared inventory object containing seat pool
     * @param isUnsafeDemo  true to trigger race condition demonstration, false for synchronized booking
     */
    public BookingTask(String passengerName, String passengerId, int trainNumber,
                       SharedSeatInventory inventory, boolean isUnsafeDemo) {
        this.passengerName = passengerName;
        this.passengerId = passengerId;
        this.trainNumber = trainNumber;
        this.inventory = inventory;
        this.isUnsafeDemo = isUnsafeDemo;
    }

    /**
     * Entry point for thread execution.
     * Demonstrates Thread.currentThread().getName() and execution flow.
     */
    @Override
    public void run() {
        String threadName = Thread.currentThread().getName();

        try {
            // Small simulated delay before booking attempt
            Thread.sleep(60);

            if (isUnsafeDemo) {
                // Execute without synchronization to demonstrate race condition
                inventory.bookSeatUnsafe(passengerName, threadName);
            } else {
                // Execute with thread-safe synchronized locking
                inventory.bookSeatSafe(passengerName, threadName);
            }

        } catch (InterruptedException e) {
            System.out.println("   [Thread Interrupted] " + threadName + " for " + passengerName + " was interrupted.");
        }
    }

    public String getPassengerName() {
        return passengerName;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public int getTrainNumber() {
        return trainNumber;
    }
}