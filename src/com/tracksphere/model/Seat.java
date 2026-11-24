package com.tracksphere.model;

/**
 * Seat.java
 *
 * Represents an individual physical seat/berth inside a train coach.
 * Demonstrates encapsulation and state management.
 *
 * Evaluation: Programming in Java (25CSE0202) - Lectures 1 to 5.
 */
public class Seat {

    private int seatNumber;
    private String berthType; // "LOWER", "MIDDLE", "UPPER", "SIDE_LOWER", "SIDE_UPPER"
    private boolean isBooked;
    private String allocatedPassengerId;

    /**
     * Parameterized constructor.
     *
     * @param seatNumber Physical seat number (e.g. 1, 2, 3...)
     * @param berthType  Berth type classification
     */
    public Seat(int seatNumber, String berthType) {
        this.seatNumber = seatNumber;
        this.berthType = berthType;
        this.isBooked = false;
        this.allocatedPassengerId = null;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getBerthType() {
        return berthType;
    }

    public void setBerthType(String berthType) {
        this.berthType = berthType;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public void setBooked(boolean booked) {
        isBooked = booked;
    }

    public String getAllocatedPassengerId() {
        return allocatedPassengerId;
    }

    public void setAllocatedPassengerId(String allocatedPassengerId) {
        this.allocatedPassengerId = allocatedPassengerId;
    }

    /**
     * Books this seat for a specified passenger.
     *
     * @param passengerId ID of the passenger booking the seat
     * @return true if seat was successfully booked, false if already taken
     */
    public boolean bookSeat(String passengerId) {
        if (!this.isBooked) {
            this.isBooked = true;
            this.allocatedPassengerId = passengerId;
            return true;
        }
        return false;
    }

    /**
     * Releases this seat upon ticket cancellation.
     */
    public void releaseSeat() {
        this.isBooked = false;
        this.allocatedPassengerId = null;
    }

    @Override
    public String toString() {
        return "Seat{" +
                "seatNumber=" + seatNumber +
                ", berthType='" + berthType + '\'' +
                ", isBooked=" + isBooked +
                ", passengerId='" + allocatedPassengerId + '\'' +
                '}';
    }
}