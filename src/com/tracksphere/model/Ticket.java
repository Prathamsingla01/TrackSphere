package com.tracksphere.model;

/**
 * Ticket.java
 *
 * Represents an issued railway ticket with PNR, passenger, train, seat, fare, and status.
 * Demonstrates:
 *  1. Association with Passenger, Train, and Seat.
 *  2. StringBuilder for ticket formatting (Lecture 6-9).
 *
 * Evaluation: Programming in Java (25CSE0202).
 */
public class Ticket {

    private String pnr; // 10-digit unique PNR
    private Passenger passenger;
    private Train train;
    private Seat seat; // May be null if status is RAC or WAITING_LIST
    private String berthType;
    private double fare;
    private String bookingStatus; // "CONFIRMED", "RAC", "WAITING_LIST", "CANCELLED"
    private String bookingDate;
    private int waitlistOrRacPosition; // Position if RAC or Waiting List

    /**
     * Parameterized constructor.
     */
    public Ticket(String pnr, Passenger passenger, Train train, Seat seat,
                  String berthType, double fare, String bookingStatus,
                  String bookingDate, int waitlistOrRacPosition) {
        this.pnr = pnr;
        this.passenger = passenger;
        this.train = train;
        this.seat = seat;
        this.berthType = berthType;
        this.fare = fare;
        this.bookingStatus = bookingStatus;
        this.bookingDate = bookingDate;
        this.waitlistOrRacPosition = waitlistOrRacPosition;
    }

    // Getters and Setters
    public String getPnr() {
        return pnr;
    }

    public void setPnr(String pnr) {
        this.pnr = pnr;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public Train getTrain() {
        return train;
    }

    public void setTrain(Train train) {
        this.train = train;
    }

    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }

    public String getBerthType() {
        return berthType;
    }

    public void setBerthType(String berthType) {
        this.berthType = berthType;
    }

    public double getFare() {
        return fare;
    }

    public void setFare(double fare) {
        this.fare = fare;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public int getWaitlistOrRacPosition() {
        return waitlistOrRacPosition;
    }

    public void setWaitlistOrRacPosition(int waitlistOrRacPosition) {
        this.waitlistOrRacPosition = waitlistOrRacPosition;
    }

    /**
     * Generates a beautifully formatted boarding ticket using StringBuilder.
     * Demonstrates string handling and formatting (Lecture 6-9).
     *
     * @return Formatted multi-line ticket string
     */
    public String generateTicketSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("======================================================================\n");
        sb.append("                 INDIAN RAILWAYS PASSENGER RESERVATION                \n");
        sb.append("                           TRACKSPHERE IRCTC                          \n");
        sb.append("======================================================================\n");
        sb.append(String.format(" PNR NUMBER      : %-15s   QUOTA        : GENERAL (GN)\n", pnr));
        sb.append(String.format(" STATUS          : %-15s   BOOKING DATE : %s\n", getFormattedStatus(), bookingDate));
        sb.append("----------------------------------------------------------------------\n");
        sb.append(String.format(" TRAIN NUMBER    : %-8d          TRAIN NAME   : %s\n", train.getTrainNumber(), train.getTrainName()));
        sb.append(String.format(" ROUTE           : %s -> %s\n", train.getSource(), train.getDestination()));
        sb.append(String.format(" DEPARTURE TIME  : %-15s   ARRIVAL TIME : %s\n", train.getDepartureTime(), train.getArrivalTime()));
        sb.append("----------------------------------------------------------------------\n");
        sb.append(String.format(" PASSENGER ID    : %-15s   NAME         : %s\n", passenger.getPassengerId(), passenger.getName()));
        sb.append(String.format(" AGE / GENDER    : %d / %-10s     CATEGORY     : %s\n", passenger.getAge(), passenger.getGender(), passenger.getPassengerType()));
        sb.append(String.format(" MOBILE NUMBER   : %s\n", passenger.getMobileNumber()));
        sb.append("----------------------------------------------------------------------\n");
        if ("CONFIRMED".equalsIgnoreCase(bookingStatus)) {
            int seatNo = (seat != null) ? seat.getSeatNumber() : 0;
            sb.append(String.format(" SEAT NUMBER     : %-15d   BERTH TYPE   : %s\n", seatNo, berthType));
        } else if ("RAC".equalsIgnoreCase(bookingStatus)) {
            sb.append(String.format(" RAC STATUS      : RAC-%-11d   BERTH TYPE   : %s (Shared Berth)\n", waitlistOrRacPosition, berthType));
        } else if ("WAITING_LIST".equalsIgnoreCase(bookingStatus)) {
            sb.append(String.format(" WL STATUS       : WL-%-12d   BERTH TYPE   : NO BERTH ALLOTTED\n", waitlistOrRacPosition));
        } else {
            sb.append(" TICKET STATUS   : CANCELLED (Refund processed)\n");
        }
        sb.append(String.format(" TOTAL FARE PAID : Rs. %.2f\n", fare));
        sb.append("======================================================================\n");
        return sb.toString();
    }

    /**
     * Returns readable ticket status including queue position if applicable.
     */
    public String getFormattedStatus() {
        if ("RAC".equalsIgnoreCase(bookingStatus)) {
            return "RAC-" + waitlistOrRacPosition;
        } else if ("WAITING_LIST".equalsIgnoreCase(bookingStatus)) {
            return "WL-" + waitlistOrRacPosition;
        }
        return bookingStatus;
    }

    @Override
    public String toString() {
        return "PNR: " + pnr + " | " + passenger.getName() + " | Train #" + train.getTrainNumber() + " | Status: " + getFormattedStatus();
    }
}