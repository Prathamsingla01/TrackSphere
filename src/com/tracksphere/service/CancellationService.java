package com.tracksphere.service;

import com.tracksphere.exception.InvalidBookingException;
import com.tracksphere.model.Booking;
import com.tracksphere.model.Ticket;
import com.tracksphere.model.Train;

/**
 * CancellationService.java
 *
 * Manages ticket cancellations, status updates, seat releases,
 * automatic waitlist promotions, and simulated refund calculations.
 *
 * Demonstrates:
 * 1. Custom Exception Handling (InvalidBookingException) (Lecture 10-15).
 * 2. Cross-service coordination (TrainService, SeatAllocationService,
 * WaitlistService).
 * 3. Accurate refund policy implementation based on ticket status.
 *
 * Evaluation: Programming in Java (25CSE0202).
 */
public class CancellationService {

    public CancellationService() {
    }

    /**
     * Cancels an existing ticket identified by its 10-digit PNR.
     *
     * @param pnr                   10-digit PNR number
     * @param bookingService        BookingService instance containing records
     * @param trainService          TrainService instance
     * @param seatAllocationService SeatAllocationService to release seats
     * @param waitlistService       WaitlistService to promote waiting passengers
     * @return Itemized refund amount
     * @throws InvalidBookingException If PNR is invalid or ticket is already
     *                                 cancelled
     */
    public double cancelTicket(String pnr, BookingService bookingService, TrainService trainService,
            SeatAllocationService seatAllocationService, WaitlistService waitlistService)
            throws InvalidBookingException {

        if (pnr == null || pnr.trim().length() != 10) {
            throw new InvalidBookingException("Cancellation Failed: PNR must be a valid 10-digit number.");
        }

        Ticket ticket = bookingService.getTicketByPNR(pnr.trim());
        if (ticket == null) {
            throw new InvalidBookingException("Cancellation Failed: No ticket found matching PNR #" + pnr);
        }

        if ("CANCELLED".equalsIgnoreCase(ticket.getBookingStatus())) {
            throw new InvalidBookingException(
                    "Cancellation Failed: Ticket with PNR #" + pnr + " is ALREADY CANCELLED.");
        }

        Train train = ticket.getTrain();
        String originalStatus = ticket.getBookingStatus();
        double paidFare = ticket.getFare();
        double refundAmount = 0.0;
        double cancellationFee = 0.0;

        // IRCTC Cancellation and Refund Rules:
        if ("CONFIRMED".equalsIgnoreCase(originalStatus)) {
            // Confirmed: 20% cancellation deduction, 80% refund
            cancellationFee = paidFare * 0.20;
            refundAmount = paidFare - cancellationFee;
        } else if ("RAC".equalsIgnoreCase(originalStatus)) {
            // RAC: 10% clerkage fee, 90% refund
            cancellationFee = paidFare * 0.10;
            refundAmount = paidFare - cancellationFee;
        } else if ("WAITING_LIST".equalsIgnoreCase(originalStatus)) {
            // Waiting List: 5% minimal clerkage fee, 95% refund
            cancellationFee = paidFare * 0.05;
            refundAmount = paidFare - cancellationFee;
        }

        // Update Ticket status
        ticket.setBookingStatus("CANCELLED");

        // Release physical seat or remove from queue
        String promotionMessage = "";
        if ("CONFIRMED".equalsIgnoreCase(originalStatus)) {
            // Release physical seat
            seatAllocationService.releaseSeat(train, ticket.getSeat());
            ticket.setSeat(null);

            // Promote next eligible RAC / Waiting List passenger!
            promotionMessage = waitlistService.promoteNextPassenger(train, seatAllocationService);
        } else if ("RAC".equalsIgnoreCase(originalStatus)) {
            waitlistService.removeFromRAC(train.getTrainNumber(), pnr);
            train.setRacSeats(train.getRacSeats() + 1);
        } else if ("WAITING_LIST".equalsIgnoreCase(originalStatus)) {
            waitlistService.removeFromWL(train.getTrainNumber(), pnr);
            train.setWaitingListSeats(train.getWaitingListSeats() + 1);
        }

        // Update corresponding Booking object
        for (Booking b : bookingService.getBookingHistory()) {
            if (b.getTicket().getPnr().equalsIgnoreCase(pnr)) {
                b.setPaymentStatus("REFUNDED (Rs." + String.format("%.2f", refundAmount) + ")");
                break;
            }
        }

        // Display refund breakdown
        displayCancellationReceipt(ticket, paidFare, cancellationFee, refundAmount, originalStatus, promotionMessage);

        return refundAmount;
    }

    /**
     * Displays a clean cancellation and refund breakdown slip.
     */
    private void displayCancellationReceipt(Ticket ticket, double paidFare, double fee, double refund,
            String originalStatus, String promotionMsg) {
        System.out.println("\n======================================================================");
        System.out.println("                 IRCTC TICKET CANCELLATION & REFUND SLIP              ");
        System.out.println("======================================================================");
        System.out.printf(" PNR NUMBER        : %-15s   TICKET QUOTA : GENERAL\n", ticket.getPnr());
        System.out.printf(" PASSENGER NAME    : %-15s   TRAIN        : #%d (%s)\n",
                ticket.getPassenger().getName(), ticket.getTrain().getTrainNumber(), ticket.getTrain().getTrainName());
        System.out.printf(" ORIGINAL STATUS   : %-15s   NEW STATUS   : CANCELLED\n", originalStatus);
        System.out.println("----------------------------------------------------------------------");
        System.out.printf(" Original Fare Paid: Rs. %10.2f\n", paidFare);
        System.out.printf(" Cancellation Fee  : Rs. %10.2f\n", fee);
        System.out.printf(" Total Refund Due  : Rs. %10.2f\n", refund);
        System.out.println("----------------------------------------------------------------------");
        if (promotionMsg != null && !promotionMsg.isEmpty()) {
            System.out.println(" AUTOMATIC QUEUE PROMOTION REPORT:");
            System.out.print(promotionMsg);
        }
        System.out.println(" Refund has been credited back to original payment method.");
        System.out.println("======================================================================\n");
    }
}