package com.tracksphere.model;

/**
 * Train.java
 *
 * Represents an Indian Railways train with scheduling, pricing, and seat quotas.
 * Implements Comparable<Train> to demonstrate natural ordering sorting (Lecture 23-25).
 * Contains a multidimensional array (coachLayout) to represent coach seating (Lecture 6-9).
 *
 * Evaluation: Programming in Java (25CSE0202).
 */
public class Train implements Comparable<Train> {

    private int trainNumber;
    private String trainName;
    private String source;
    private String destination;
    private String departureTime;
    private String arrivalTime;
    private double baseFare;
    private int totalSeats;
    private int availableSeats;
    private int racSeats;
    private int waitingListSeats;

    // Multidimensional array: 8 bays x 6 berths per bay in a standard sleeper/chair car layout
    // Bay index i, berth index j stores the berth designation (e.g. "LB", "MB", "UB", "SL", "SU")
    private String[][] coachLayout;

    /**
     * Parameterized constructor.
     */
    public Train(int trainNumber, String trainName, String source, String destination,
                 String departureTime, String arrivalTime, double baseFare,
                 int totalSeats, int racSeats, int waitingListSeats) {
        this.trainNumber = trainNumber;
        this.trainName = trainName;
        this.source = source;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.baseFare = baseFare;
        this.totalSeats = totalSeats;
        this.availableSeats = totalSeats;
        this.racSeats = racSeats;
        this.waitingListSeats = waitingListSeats;

        // Initialize 2D coach layout array (Lecture 6-9)
        initializeCoachLayout();
    }

    /**
     * Initializes the multidimensional array representing the coach berth arrangement.
     * Demonstrates 2D array manipulation and nested loops.
     */
    private void initializeCoachLayout() {
        // 8 compartments (bays), each having 6 standard positions:
        // [0]=LB, [1]=MB, [2]=UB, [3]=LB, [4]=MB, [5]=UB
        this.coachLayout = new String[8][6];
        String[] berthTypes = {"LB", "MB", "UB", "LB", "MB", "UB"};
        for (int i = 0; i < coachLayout.length; i++) {
            for (int j = 0; j < coachLayout[i].length; j++) {
                int seatNum = (i * 6) + (j + 1);
                coachLayout[i][j] = seatNum + ":" + berthTypes[j];
            }
        }
    }

    /**
     * Displays the 2D coach layout to the console.
     * Demonstrates multidimensional array traversal using nested loops.
     */
    public void displayCoachLayout() {
        System.out.println("\n--- Coach Berth Seating Layout for Train #" + trainNumber + " (" + trainName + ") ---");
        System.out.println("Format: [Seat#:BerthType] (LB: Lower, MB: Middle, UB: Upper)");
        for (int bay = 0; bay < coachLayout.length; bay++) {
            System.out.print("Bay " + (bay + 1) + " | ");
            for (int seat = 0; seat < coachLayout[bay].length; seat++) {
                System.out.printf("[%7s] ", coachLayout[bay][seat]);
            }
            System.out.println();
        }
        System.out.println("---------------------------------------------------------------------------------");
    }

    /**
     * Natural ordering comparison based on train number.
     * Demonstrates Comparable interface implementation (Lecture 23-25).
     *
     * @param other The other train to compare with
     * @return Negative integer, zero, or positive integer if this train number is less than, equal to, or greater
     */
    @Override
    public int compareTo(Train other) {
        if (other == null) {
            return 1;
        }
        return Integer.compare(this.trainNumber, other.trainNumber);
    }

    // Getters and Setters
    public int getTrainNumber() {
        return trainNumber;
    }

    public void setTrainNumber(int trainNumber) {
        this.trainNumber = trainNumber;
    }

    public String getTrainName() {
        return trainName;
    }

    public void setTrainName(String trainName) {
        this.trainName = trainName;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public double getBaseFare() {
        return baseFare;
    }

    public void setBaseFare(double baseFare) {
        this.baseFare = baseFare;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public int getRacSeats() {
        return racSeats;
    }

    public void setRacSeats(int racSeats) {
        this.racSeats = racSeats;
    }

    public int getWaitingListSeats() {
        return waitingListSeats;
    }

    public void setWaitingListSeats(int waitingListSeats) {
        this.waitingListSeats = waitingListSeats;
    }

    public String[][] getCoachLayout() {
        return coachLayout;
    }

    /**
     * Displays a clean formatted one-line summary of the train.
     */
    public void displayTrainInfo() {
        System.out.printf("| %-6d | %-28s | %-12s -> %-12s | %-8s -> %-8s | Rs.%-7.2f | Confirmed: %-3d | RAC: %-2d | WL: %-2d |\n",
                trainNumber, trainName, source, destination, departureTime, arrivalTime, baseFare, availableSeats, racSeats, waitingListSeats);
    }

    @Override
    public String toString() {
        return "Train #" + trainNumber + " - " + trainName + " (" + source + " to " + destination + ")";
    }
}