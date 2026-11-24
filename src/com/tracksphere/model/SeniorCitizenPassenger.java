package com.tracksphere.model;

/**
 * SeniorCitizenPassenger.java
 *
 * Represents a passenger aged 60 or above who receives preferential berth allotment
 * (Lower Berth priority) and simulated fare concession.
 * Extends Passenger to demonstrate multilevel inheritance and runtime polymorphism.
 *
 * Evaluation: Programming in Java (25CSE0202) - Lectures 1 to 5.
 */
public class SeniorCitizenPassenger extends Passenger {

    private double concessionPercentage;
    private boolean prefersLowerBerth;

    /**
     * Parameterized constructor.
     *
     * @param passengerId  Unique passenger ID
     * @param name         Full name
     * @param age          Age in years (should be >= 60)
     * @param gender       Gender
     * @param mobileNumber 10-digit mobile number
     */
    public SeniorCitizenPassenger(String passengerId, String name, int age, String gender, String mobileNumber) {
        super(passengerId, name, age, gender, mobileNumber);
        this.concessionPercentage = 40.0; // 40% senior citizen concession
        this.prefersLowerBerth = true;     // Lower berth is preferred for seniors
    }

    public double getConcessionPercentage() {
        return concessionPercentage;
    }

    public void setConcessionPercentage(double concessionPercentage) {
        this.concessionPercentage = concessionPercentage;
    }

    public boolean isPrefersLowerBerth() {
        return prefersLowerBerth;
    }

    public void setPrefersLowerBerth(boolean prefersLowerBerth) {
        this.prefersLowerBerth = prefersLowerBerth;
    }

    /**
     * Calculates the discounted fare for senior citizens.
     *
     * @param baseFare Original ticket fare
     * @return Discounted ticket fare
     */
    public double calculateDiscountedFare(double baseFare) {
        double discount = (baseFare * concessionPercentage) / 100.0;
        return baseFare - discount;
    }

    /**
     * Overrides getPassengerType() to return Senior Citizen.
     * Demonstrates runtime polymorphism.
     */
    @Override
    public String getPassengerType() {
        return "Senior Citizen (Concession 40%)";
    }

    /**
     * Overrides displayDetails() to show senior citizen specific privileges.
     */
    @Override
    public void displayDetails() {
        super.displayDetails();
        System.out.println("   --> Senior Citizen Benefits: 40% Concession | Auto Lower-Berth Priority: Active");
    }
}