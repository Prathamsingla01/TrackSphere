package com.tracksphere.model;

/**
 * Passenger.java
 *
 * Represents a registered railway passenger.
 * Extends Person to demonstrate single inheritance and method overriding.
 *
 * Evaluation: Programming in Java (25CSE0202) - Lectures 1 to 5.
 */
public class Passenger extends Person {

    private String passengerId;

    /**
     * Default constructor.
     */
    public Passenger() {
        super();
        this.passengerId = "P-000";
    }

    /**
     * Parameterized constructor calling the superclass constructor using super().
     *
     * @param passengerId  Unique identifier for passenger (e.g. PS-101)
     * @param name         Full name
     * @param age          Age in years
     * @param gender       Gender
     * @param mobileNumber 10-digit mobile number
     */
    public Passenger(String passengerId, String name, int age, String gender, String mobileNumber) {
        super(name, age, gender, mobileNumber); // Invokes Person constructor
        this.passengerId = passengerId;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(String passengerId) {
        this.passengerId = passengerId;
    }

    /**
     * Overriding abstract method from Person.
     *
     * @return "General Passenger"
     */
    @Override
    public String getPassengerType() {
        return "General Passenger";
    }

    /**
     * Overriding displayDetails() to include Passenger ID along with personal info.
     * Demonstrates code reuse via super.displayDetails().
     */
    @Override
    public void displayDetails() {
        System.out.print("[Passenger ID: " + passengerId + "] ");
        super.displayDetails();
    }

    @Override
    public String toString() {
        return "Passenger{" +
                "passengerId='" + passengerId + '\'' +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                ", mobileNumber='" + mobileNumber + '\'' +
                ", category='" + getPassengerType() + '\'' +
                '}';
    }
}