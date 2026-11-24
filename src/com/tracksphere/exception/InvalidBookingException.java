package com.tracksphere.exception;

/**
 * InvalidBookingException.java
 *
 * Custom checked exception thrown when an invalid booking operation is attempted.
 * For example:
 *  - Booking during closed Tatkal window.
 *  - Invalid PNR during cancellation or search.
 *  - Duplicate cancellation request.
 *  - Invalid passenger age or mobile number.
 *
 * Demonstrates: Exception Handling, Custom Exceptions, Exception hierarchy (Lecture 10-15).
 */
public class InvalidBookingException extends Exception {

    /**
     * Constructor accepting error description.
     *
     * @param message Detailed explanation of the error
     */
    public InvalidBookingException(String message) {
        super(message);
    }

    /**
     * Constructor accepting error description and underlying cause.
     * Demonstrates exception chaining/propagation.
     *
     * @param message Detailed explanation of the error
     * @param cause   Underlying exception that triggered this error
     */
    public InvalidBookingException(String message, Throwable cause) {
        super(message, cause);
    }
}