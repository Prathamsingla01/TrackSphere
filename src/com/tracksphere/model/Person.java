package com.tracksphere.model;

/**
 * Person.java
 *
 * Abstract base class representing any human entity in the railway reservation system.
 * Demonstrates:
 *  1. Abstraction (cannot be instantiated directly, contains abstract method getPassengerType()).
 *  2. Encapsulation (protected fields with public getters and setters).
 *  3. StringBuilder usage for string formatting.
 *
 * Evaluation: Programming in Java (25CSE0202) - Lectures 1 to 5, 6 to 9.
 */
public abstract class Person {

    // Protected fields accessible by child classes (Passenger, SeniorCitizenPassenger)
    protected String name;
    protected int age;
    protected String gender;
    protected String mobileNumber;

    /**
     * Default constructor.
     */
    public Person() {
        this.name = "Unknown";
        this.age = 0;
        this.gender = "Unspecified";
        this.mobileNumber = "0000000000";
    }

    /**
     * Parameterized constructor.
     *
     * @param name         Passenger full name
     * @param age          Passenger age in years
     * @param gender       Gender (Male / Female / Transgender)
     * @param mobileNumber 10-digit mobile number
     */
    public Person(String name, int age, String gender, String mobileNumber) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.mobileNumber = mobileNumber;
    }

    /**
     * Abstract method to be implemented by child classes.
     * Demonstrates runtime polymorphism when invoked via a Person reference.
     *
     * @return String describing the passenger category.
     */
    public abstract String getPassengerType();

    // Getters and Setters demonstrating encapsulation
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    /**
     * Displays common personal details using StringBuilder.
     * Demonstrates mutable string manipulation (Lecture 6-9).
     */
    public void displayDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append("Name: ").append(name)
          .append(" | Age: ").append(age)
          .append(" | Gender: ").append(gender)
          .append(" | Mobile: ").append(mobileNumber)
          .append(" | Category: ").append(getPassengerType());
        System.out.println(sb.toString());
    }
}