package com.tracksphere.service;

import com.tracksphere.model.Passenger;
import com.tracksphere.model.Seat;
import com.tracksphere.model.SeniorCitizenPassenger;
import com.tracksphere.model.Train;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SeatAllocationService.java
 *
 * Manages physical seat allocations and berth preferences.
 * Demonstrates:
 * 1. HashMap storing train-to-seat mappings (Map<Integer, List<Seat>>).
 * 2. Senior citizen lower berth priority logic.
 * 3. Prevention of duplicate seat allocations.
 *
 * Evaluation: Programming in Java (25CSE0202).
 */
public class SeatAllocationService {

    // Maps Train Number to its physical list of seats
    private Map<Integer, List<Seat>> trainSeatsMap;

    public SeatAllocationService() {
        this.trainSeatsMap = new HashMap<Integer, List<Seat>>();
    }

    /**
     * Initializes physical seats for a train.
     * Distributes berths evenly: LOWER, MIDDLE, UPPER, SIDE_LOWER, SIDE_UPPER.
     *
     * @param train The train whose seats are to be created
     */
    public void initializeSeatsForTrain(Train train) {
        int totalSeats = train.getTotalSeats();
        List<Seat> seats = new ArrayList<Seat>();

        String[] berthRotation = { "LOWER", "MIDDLE", "UPPER", "LOWER", "MIDDLE", "UPPER", "SIDE_LOWER", "SIDE_UPPER" };

        for (int i = 1; i <= totalSeats; i++) {
            String berth = berthRotation[(i - 1) % berthRotation.length];
            Seat seat = new Seat(i, berth);
            seats.add(seat);
        }

        trainSeatsMap.put(train.getTrainNumber(), seats);
    }

    /**
     * Allocates an available seat according to berth preference and passenger type.
     * If preferred berth is not available, falls back to any available berth.
     * Senior citizens are given priority for LOWER berth.
     *
     * @param train          The train on which seat is booked
     * @param passenger      The passenger
     * @param preferredBerth User-selected berth (LOWER, MIDDLE, UPPER, SIDE_LOWER,
     *                       SIDE_UPPER)
     * @return Allocated Seat or null if no confirmed seat is available
     */
    public Seat allocateSeat(Train train, Passenger passenger, String preferredBerth) {
        List<Seat> seats = trainSeatsMap.get(train.getTrainNumber());
        if (seats == null) {
            initializeSeatsForTrain(train);
            seats = trainSeatsMap.get(train.getTrainNumber());
        }

        // Senior citizen preference override
        String targetBerth = preferredBerth;
        if (passenger instanceof SeniorCitizenPassenger) {
            targetBerth = "LOWER"; // Automatically prioritize Lower Berth for seniors
        }

        Seat fallbackSeat = null;

        // Step 1: Attempt to find a seat matching preferred berth
        for (Seat seat : seats) {
            if (!seat.isBooked()) {
                if (seat.getBerthType().equalsIgnoreCase(targetBerth)) {
                    seat.bookSeat(passenger.getPassengerId());
                    train.setAvailableSeats(train.getAvailableSeats() - 1);
                    return seat;
                }
                if (fallbackSeat == null) {
                    fallbackSeat = seat; // Store first available seat as fallback
                }
            }
        }

        // Step 2: If preferred berth was not available, allot the fallback seat
        if (fallbackSeat != null) {
            fallbackSeat.bookSeat(passenger.getPassengerId());
            train.setAvailableSeats(train.getAvailableSeats() - 1);
            return fallbackSeat;
        }

        // No seats available
        return null;
    }

    /**
     * Releases a seat upon ticket cancellation.
     *
     * @param train Train whose seat is being released
     * @param seat  Seat to release
     */
    public void releaseSeat(Train train, Seat seat) {
        if (seat != null) {
            seat.releaseSeat();
            train.setAvailableSeats(train.getAvailableSeats() + 1);
        }
    }

    /**
     * Returns the physical seats list for a given train.
     */
    public List<Seat> getSeatsForTrain(int trainNumber) {
        return trainSeatsMap.get(trainNumber);
    }
}