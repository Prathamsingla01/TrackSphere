package com.tracksphere.service;

import com.tracksphere.model.Passenger;
import com.tracksphere.model.SeniorCitizenPassenger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * PassengerService.java
 *
 * Handles passenger registration, identification, and record management.
 * Demonstrates:
 * 1. ArrayList for ordered passenger list.
 * 2. HashMap for O(1) ID-based passenger retrieval.
 * 3. ListIterator for bidirectional traversal (Lecture 20-22).
 * 4. Inheritance and dynamic polymorphism (SeniorCitizenPassenger creation for
 * age >= 60).
 *
 * Evaluation: Programming in Java (25CSE0202).
 */
public class PassengerService {

    private List<Passenger> passengerList;
    private Map<String, Passenger> passengerMap;
    private int nextPassengerNumber;

    /**
     * Constructor initializing collections and ID counter.
     */
    public PassengerService() {
        this.passengerList = new ArrayList<Passenger>();
        this.passengerMap = new HashMap<String, Passenger>();
        this.nextPassengerNumber = 101; // First passenger ID will be PS-101
    }

    /**
     * Registers a new passenger.
     * Automatically creates a SeniorCitizenPassenger if age >= 60.
     *
     * @param name         Full name of passenger
     * @param age          Age in years
     * @param gender       Gender (Male/Female/Other)
     * @param mobileNumber 10-digit mobile number
     * @return Registered Passenger (or SeniorCitizenPassenger) object
     */
    public Passenger registerPassenger(String name, int age, String gender, String mobileNumber) {
        String passengerId = "PS-" + nextPassengerNumber;
        nextPassengerNumber++;

        Passenger passenger;
        if (age >= 60) {
            // Polymorphism: Subclass instantiated for seniors
            passenger = new SeniorCitizenPassenger(passengerId, name, age, gender, mobileNumber);
        } else {
            // Standard passenger
            passenger = new Passenger(passengerId, name, age, gender, mobileNumber);
        }

        passengerList.add(passenger);
        passengerMap.put(passengerId, passenger);
        return passenger;
    }

    /**
     * Retrieves a passenger by their unique passenger ID.
     *
     * @param passengerId e.g. "PS-101"
     * @return Passenger object or null if not found
     */
    public Passenger getPassengerById(String passengerId) {
        if (passengerId == null) {
            return null;
        }
        return passengerMap.get(passengerId.trim().toUpperCase());
    }

    /**
     * Returns the list of all registered passengers.
     *
     * @return List of passengers
     */
    public List<Passenger> getAllPassengers() {
        return passengerList;
    }

    /**
     * Displays all registered passengers using a ListIterator.
     * Demonstrates ListIterator traversal (Lecture 20-22).
     */
    public void displayAllPassengers() {
        if (passengerList.isEmpty()) {
            System.out.println("[Notice] No passengers have been registered yet.");
            return;
        }

        System.out.println(
                "\n==========================================================================================");
        System.out
                .println("                                REGISTERED PASSENGERS LIST                                ");
        System.out
                .println("==========================================================================================");

        ListIterator<Passenger> listIterator = passengerList.listIterator();
        while (listIterator.hasNext()) {
            Passenger p = listIterator.next();
            p.displayDetails();
        }
        System.out
                .println("==========================================================================================");
    }

    public int getRegisteredPassengerCount() {
        return passengerList.size();
    }
}