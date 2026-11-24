package com.tracksphere.service;

import com.tracksphere.model.Train;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * TrainService.java
 *
 * Manages train inventory, train searching, and multi-criteria sorting.
 * Demonstrates:
 * 1. ArrayList for dynamic train records.
 * 2. TreeMap for sorted key-based lookups (train number -> Train).
 * 3. TreeSet for unique sorted train numbers.
 * 4. Iterator for safe collection traversal (Lecture 20-22).
 * 5. Comparable and Comparator for sorting (Lecture 23-25).
 * 6. No lambdas or streams (Syllabus compliance).
 *
 * Evaluation: Programming in Java (25CSE0202).
 */
public class TrainService {

    // Main collection of trains
    private List<Train> trainList;

    // TreeMap maintains trains sorted by Train Number in natural order
    private Map<Integer, Train> trainMap;

    // TreeSet maintains sorted unique train numbers
    private Set<Integer> trainNumberSet;

    /**
     * Constructor initializing Collections.
     */
    public TrainService() {
        this.trainList = new ArrayList<Train>();
        this.trainMap = new TreeMap<Integer, Train>();
        this.trainNumberSet = new TreeSet<Integer>();
    }

    /**
     * Adds a new train to all backing collections.
     *
     * @param train Train object to add
     */
    public void addTrain(Train train) {
        if (train != null) {
            trainList.add(train);
            trainMap.put(train.getTrainNumber(), train);
            trainNumberSet.add(train.getTrainNumber());
        }
    }

    /**
     * Returns the complete list of trains.
     *
     * @return List of trains
     */
    public List<Train> getAllTrains() {
        return trainList;
    }

    /**
     * Retrieves a train by its unique train number in O(log n) time using TreeMap.
     *
     * @param trainNumber Train number to look up
     * @return Train object or null if not found
     */
    public Train findTrainByNumber(int trainNumber) {
        return trainMap.get(trainNumber);
    }

    /**
     * Searches for trains between a given source and destination city.
     * Uses standard Iterator traversal (Lecture 20-22).
     *
     * @param source      Departure city
     * @param destination Arrival city
     * @return List of matching trains
     */
    public List<Train> searchTrains(String source, String destination) {
        List<Train> matchingTrains = new ArrayList<Train>();
        Iterator<Train> iterator = trainList.iterator();

        while (iterator.hasNext()) {
            Train train = iterator.next();
            boolean matchSource = train.getSource().equalsIgnoreCase(source.trim());
            boolean matchDest = train.getDestination().equalsIgnoreCase(destination.trim());

            if (matchSource && matchDest) {
                matchingTrains.add(train);
            }
        }
        return matchingTrains;
    }

    /**
     * Sorts the trains by lowest fare using a custom Comparator anonymous class.
     * Demonstrates Comparator interface (Lecture 23-25) without lambda expressions.
     */
    public void sortByFare() {
        Collections.sort(trainList, new Comparator<Train>() {
            @Override
            public int compare(Train t1, Train t2) {
                return Double.compare(t1.getBaseFare(), t2.getBaseFare());
            }
        });
    }

    /**
     * Sorts the trains by departure time using a custom Comparator anonymous class.
     */
    public void sortByDepartureTime() {
        Collections.sort(trainList, new Comparator<Train>() {
            @Override
            public int compare(Train t1, Train t2) {
                return t1.getDepartureTime().compareTo(t2.getDepartureTime());
            }
        });
    }

    /**
     * Sorts the trains naturally by train number using Comparable.compareTo().
     */
    public void sortByTrainNumber() {
        Collections.sort(trainList); // Relies on Train.compareTo()
    }

    /**
     * Displays all registered trains in a neat formatted table.
     */
    public void displayAllTrains() {
        System.out.println(
                "\n========================================================================================================================================");
        System.out.println(
                "                                                    TRACKSPHERE SCHEDULED TRAIN DIRECTORY                                               ");
        System.out.println(
                "========================================================================================================================================");
        System.out.printf("| %-6s | %-28s | %-28s | %-19s | %-10s | %-15s | %-9s | %-8s |\n",
                "NO.", "TRAIN NAME", "ROUTE", "SCHEDULE (DEP->ARR)", "BASE FARE", "CONFIRMED SEATS", "RAC SEATS",
                "WL SEATS");
        System.out.println(
                "----------------------------------------------------------------------------------------------------------------------------------------");

        for (Train train : trainList) {
            train.displayTrainInfo();
        }
        System.out.println(
                "========================================================================================================================================");
    }

    /**
     * Displays seat availability details for a given train.
     *
     * @param trainNumber Number of the train to inspect
     */
    public void displaySeatAvailability(int trainNumber) {
        Train train = findTrainByNumber(trainNumber);
        if (train == null) {
            System.out.println("[Error] No train found with number: " + trainNumber);
            return;
        }

        System.out.println("\n----------------- SEAT AVAILABILITY STATUS -----------------");
        System.out.println("Train No & Name  : " + train.getTrainNumber() + " - " + train.getTrainName());
        System.out.println("Route            : " + train.getSource() + " -> " + train.getDestination());
        System.out.println("Confirmed Seats  : " + train.getAvailableSeats() + " / " + train.getTotalSeats());
        System.out.println("RAC Capacity     : " + train.getRacSeats() + " remaining");
        System.out.println("Waitlist Capacity: " + train.getWaitingListSeats() + " remaining");
        System.out.println("Base Ticket Fare : Rs. " + train.getBaseFare());
        System.out.println("-------------------------------------------------------------");
    }

    public Set<Integer> getTrainNumberSet() {
        return trainNumberSet;
    }
}