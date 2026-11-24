package com.tracksphere.exception;

/**
 * SeatNotAvailableException.java
 *
 * Custom checked exception thrown when no seats are available in the requested category
 * (Confirmed, RAC, or Waiting List).
 *
 * Demonstrates: Exception Handling, Custom Exceptions (Lecture 10-15).
 */
public class SeatNotAvailableException extends Exception {

    /**
     * Constructor accepting error description.
     *
     * @param message Detailed explanation of why the seat cannot be allotted
     */
    public SeatNotAvailableException(String message) {
        super(message);
    }
}