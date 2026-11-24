package com.tracksphere.service;

import com.tracksphere.model.Seat;
import com.tracksphere.model.Ticket;
import com.tracksphere.model.Train;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * WaitlistService.java
 *
 * Manages RAC (Reservation Against Cancellation) and Waiting List queues.
 * Demonstrates:
 * 1. LinkedList as a FIFO Queue (Lecture 16-25) using addLast() and
 * removeFirst().
 * 2. Dynamic promotion:
 * - When a Confirmed ticket is cancelled, RAC-1 is promoted to CONFIRMED.
 * - Then, WL-1 is promoted to RAC.
 * - All remaining positions are updated automatically.
 * 3. ListIterator for safe position updates.
 *
 * Evaluation: Programming in Java (25CSE0202).
 */
public class WaitlistService {

    // Maps Train Number to its RAC queue (FIFO using LinkedList)
    private Map<Integer, LinkedList<Ticket>> racQueueMap;

    // Maps Train Number to its Waiting List queue (FIFO using LinkedList)
    private Map<Integer, LinkedList<Ticket>> wlQueueMap;

    public WaitlistService() {
        this.racQueueMap = new HashMap<Integer, LinkedList<Ticket>>();
        this.wlQueueMap = new HashMap<Integer, LinkedList<Ticket>>();
    }

    private LinkedList<Ticket> getOrCreateRACQueue(int trainNumber) {
        LinkedList<Ticket> queue = racQueueMap.get(trainNumber);
        if (queue == null) {
            queue = new LinkedList<Ticket>();
            racQueueMap.put(trainNumber, queue);
        }
        return queue;
    }

    private LinkedList<Ticket> getOrCreateWLQueue(int trainNumber) {
        LinkedList<Ticket> queue = wlQueueMap.get(trainNumber);
        if (queue == null) {
            queue = new LinkedList<Ticket>();
            wlQueueMap.put(trainNumber, queue);
        }
        return queue;
    }

    /**
     * Adds a ticket to the RAC queue if RAC seats are available.
     *
     * @param train  Target train
     * @param ticket Ticket being allotted RAC
     * @return Assigned RAC position (1, 2, ...) or -1 if RAC quota is exhausted
     */
    public int addToRAC(Train train, Ticket ticket) {
        if (train.getRacSeats() <= 0) {
            return -1; // RAC full
        }

        LinkedList<Ticket> racQueue = getOrCreateRACQueue(train.getTrainNumber());
        int assignedPosition = racQueue.size() + 1;

        ticket.setBookingStatus("RAC");
        ticket.setWaitlistOrRacPosition(assignedPosition);
        ticket.setBerthType("SIDE_LOWER (Shared)");

        racQueue.addLast(ticket); // FIFO enqueue
        train.setRacSeats(train.getRacSeats() - 1);

        return assignedPosition;
    }

    /**
     * Adds a ticket to the Waiting List queue if WL seats are available.
     *
     * @param train  Target train
     * @param ticket Ticket being allotted Waiting List
     * @return Assigned WL position (1, 2, ...) or -1 if WL quota is exhausted
     */
    public int addToWaitingList(Train train, Ticket ticket) {
        if (train.getWaitingListSeats() <= 0) {
            return -1; // Waiting list full
        }

        LinkedList<Ticket> wlQueue = getOrCreateWLQueue(train.getTrainNumber());
        int assignedPosition = wlQueue.size() + 1;

        ticket.setBookingStatus("WAITING_LIST");
        ticket.setWaitlistOrRacPosition(assignedPosition);
        ticket.setBerthType("NONE");

        wlQueue.addLast(ticket); // FIFO enqueue
        train.setWaitingListSeats(train.getWaitingListSeats() - 1);

        return assignedPosition;
    }

    /**
     * Handles FIFO promotion upon cancellation of a confirmed seat.
     * 1. If RAC queue has passengers, promote RAC-1 to CONFIRMED and allocate seat.
     * 2. If WL queue has passengers, promote WL-1 to RAC.
     * 3. Recalculate remaining queue positions.
     *
     * @param train                 Train where cancellation occurred
     * @param seatAllocationService Seat allocation service to physically allocate
     *                              berth
     * @return Description of promotions performed
     */
    public String promoteNextPassenger(Train train, SeatAllocationService seatAllocationService) {
        int trainNo = train.getTrainNumber();
        LinkedList<Ticket> racQueue = getOrCreateRACQueue(trainNo);
        LinkedList<Ticket> wlQueue = getOrCreateWLQueue(trainNo);

        StringBuilder promotionReport = new StringBuilder();

        // Step 1: Promote head of RAC to CONFIRMED
        if (!racQueue.isEmpty()) {
            Ticket promotedToConfirmed = racQueue.removeFirst(); // FIFO dequeue

            // Physically allocate seat
            Seat newSeat = seatAllocationService.allocateSeat(train, promotedToConfirmed.getPassenger(), "LOWER");

            promotedToConfirmed.setBookingStatus("CONFIRMED");
            promotedToConfirmed.setSeat(newSeat);
            promotedToConfirmed.setBerthType((newSeat != null) ? newSeat.getBerthType() : "CONFIRMED");
            promotedToConfirmed.setWaitlistOrRacPosition(0);

            train.setRacSeats(train.getRacSeats() + 1); // Free 1 RAC slot

            promotionReport.append("   [Promotion] RAC Ticket (PNR: ").append(promotedToConfirmed.getPnr())
                    .append(", Passenger: ").append(promotedToConfirmed.getPassenger().getName())
                    .append(") promoted to CONFIRMED (Seat #")
                    .append(newSeat != null ? newSeat.getSeatNumber() : "Allocated").append(").\n");

            // Renumber remaining RAC tickets
            renumberQueue(racQueue, "RAC");

            // Step 2: Promote head of Waiting List to RAC
            if (!wlQueue.isEmpty()) {
                Ticket promotedToRAC = wlQueue.removeFirst(); // FIFO dequeue
                promotedToRAC.setBookingStatus("RAC");
                promotedToRAC.setBerthType("SIDE_LOWER (Shared)");
                promotedToRAC.setWaitlistOrRacPosition(racQueue.size() + 1);

                racQueue.addLast(promotedToRAC);
                train.setRacSeats(train.getRacSeats() - 1);
                train.setWaitingListSeats(train.getWaitingListSeats() + 1);

                promotionReport.append("   [Promotion] WL Ticket (PNR: ").append(promotedToRAC.getPnr())
                        .append(", Passenger: ").append(promotedToRAC.getPassenger().getName())
                        .append(") promoted to RAC-").append(promotedToRAC.getWaitlistOrRacPosition()).append(".\n");

                // Renumber remaining WL tickets
                renumberQueue(wlQueue, "WL");
            }
        } else {
            promotionReport.append("   [Notice] RAC queue is empty. Seat remains available in general pool.\n");
        }

        return promotionReport.toString();
    }

    /**
     * Renumbers the queue positions after a promotion or cancellation.
     * Demonstrates ListIterator traversal (Lecture 20-22).
     */
    private void renumberQueue(LinkedList<Ticket> queue, String queueType) {
        int position = 1;
        ListIterator<Ticket> iterator = queue.listIterator();
        while (iterator.hasNext()) {
            Ticket ticket = iterator.next();
            ticket.setWaitlistOrRacPosition(position);
            position++;
        }
    }

    /**
     * Removes a ticket directly from the RAC queue (e.g. if passenger cancels RAC
     * ticket).
     */
    public boolean removeFromRAC(int trainNumber, String pnr) {
        LinkedList<Ticket> queue = getOrCreateRACQueue(trainNumber);
        Iterator<Ticket> it = queue.iterator();
        while (it.hasNext()) {
            Ticket t = it.next();
            if (t.getPnr().equalsIgnoreCase(pnr)) {
                it.remove();
                renumberQueue(queue, "RAC");
                return true;
            }
        }
        return false;
    }

    /**
     * Removes a ticket directly from the Waiting List queue.
     */
    public boolean removeFromWL(int trainNumber, String pnr) {
        LinkedList<Ticket> queue = getOrCreateWLQueue(trainNumber);
        Iterator<Ticket> it = queue.iterator();
        while (it.hasNext()) {
            Ticket t = it.next();
            if (t.getPnr().equalsIgnoreCase(pnr)) {
                it.remove();
                renumberQueue(queue, "WL");
                return true;
            }
        }
        return false;
    }

    /**
     * Displays all tickets currently on the Waiting List for a train.
     */
    public void displayWaitingList(int trainNumber) {
        LinkedList<Ticket> queue = getOrCreateWLQueue(trainNumber);
        System.out.println("\n--- Current Waiting List for Train #" + trainNumber + " ---");
        if (queue.isEmpty()) {
            System.out.println("   [Empty] No passengers currently on Waiting List.");
            return;
        }
        for (Ticket ticket : queue) {
            System.out.printf("   Position: WL-%-2d  | PNR: %-10s | Passenger: %-15s | Mobile: %s\n",
                    ticket.getWaitlistOrRacPosition(), ticket.getPnr(), ticket.getPassenger().getName(),
                    ticket.getPassenger().getMobileNumber());
        }
    }

    public LinkedList<Ticket> getRACQueue(int trainNumber) {
        return getOrCreateRACQueue(trainNumber);
    }

    public LinkedList<Ticket> getWLQueue(int trainNumber) {
        return getOrCreateWLQueue(trainNumber);
    }
}