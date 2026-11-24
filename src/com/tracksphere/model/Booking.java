package com.tracksphere.model;

/**
 * Booking.java
 *
 * Represents a complete booking transaction record.
 * Connects Ticket details, timestamp, payment mode, and financial status.
 *
 * Evaluation: Programming in Java (25CSE0202) - Lectures 1 to 5, 6 to 9.
 */
public class Booking {

    private String bookingId;
    private Ticket ticket;
    private String bookingTime;
    private String paymentMode;
    private String paymentStatus;

    /**
     * Parameterized constructor.
     *
     * @param bookingId     Unique transaction ID (e.g. BK-1001)
     * @param ticket        The issued Ticket object
     * @param bookingTime   Timestamp of transaction
     * @param paymentMode   Mode of payment (UPI, Credit Card, Debit Card, Net Banking)
     * @param paymentStatus Payment outcome (SUCCESS, REFUNDED)
     */
    public Booking(String bookingId, Ticket ticket, String bookingTime, String paymentMode, String paymentStatus) {
        this.bookingId = bookingId;
        this.ticket = ticket;
        this.bookingTime = bookingTime;
        this.paymentMode = paymentMode;
        this.paymentStatus = paymentStatus;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public String getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(String bookingTime) {
        this.bookingTime = bookingTime;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    /**
     * Displays a clean booking receipt for the user.
     */
    public void displayBookingReceipt() {
        System.out.println("\n----------------- BOOKING RECEIPT -----------------");
        System.out.println("Booking Reference : " + bookingId);
        System.out.println("Booking Time      : " + bookingTime);
        System.out.println("Payment Mode      : " + paymentMode);
        System.out.println("Payment Status    : " + paymentStatus);
        System.out.println("PNR Assigned      : " + ticket.getPnr());
        System.out.println("Passenger Name    : " + ticket.getPassenger().getName());
        System.out.println("Train             : " + ticket.getTrain().getTrainNumber() + " - " + ticket.getTrain().getTrainName());
        System.out.println("Status            : " + ticket.getFormattedStatus());
        System.out.printf("Fare Charged      : Rs. %.2f\n", ticket.getFare());
        System.out.println("---------------------------------------------------");
    }

    @Override
    public String toString() {
        return "Booking[" + bookingId + "] PNR: " + ticket.getPnr() +
                " | Passenger: " + ticket.getPassenger().getName() +
                " | Status: " + ticket.getFormattedStatus() +
                " | Mode: " + paymentMode +
                " | Paid: Rs." + ticket.getFare();
    }
}