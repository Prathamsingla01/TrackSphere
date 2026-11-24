package com.tracksphere.util;

import com.tracksphere.model.Train;
import com.tracksphere.service.PassengerService;
import com.tracksphere.service.SeatAllocationService;
import com.tracksphere.service.TrainService;

/**
 * RailwayData.java
 *
 * Pre-populates realistic Indian Railways train schedules, routes, fares,
 * and sample passengers for demonstration and evaluation.
 *
 * Evaluation: Programming in Java (25CSE0202).
 */
public class RailwayData {

    private RailwayData() {
    }

    /**
     * Initializes 10 realistic Indian Railways express trains across major routes.
     * (Delhi, Mumbai, Chandigarh, Jaipur, Lucknow, Kolkata, Amritsar, Bhopal).
     *
     * @param trainService          TrainService instance to register trains
     * @param seatAllocationService SeatAllocationService to create physical seats
     */
    public static void initializeTrains(TrainService trainService, SeatAllocationService seatAllocationService) {
        // Train 1: New Delhi - Bhopal Shatabdi Express
        Train t1 = new Train(12002, "Bhopal Shatabdi Express", "Delhi", "Bhopal",
                "06:00 AM", "02:25 PM", 1450.00, 18, 3, 3);

        // Train 2: Mumbai Tejas Rajdhani Express
        Train t2 = new Train(12952, "Mumbai Tejas Rajdhani", "Delhi", "Mumbai",
                "04:55 PM", "08:35 AM", 2850.00, 24, 4, 4);

        // Train 3: Kalka Shatabdi Express (Connecting to Chandigarh)
        Train t3 = new Train(12011, "Kalka Shatabdi Express", "Delhi", "Chandigarh",
                "07:40 AM", "11:05 AM", 820.00, 18, 3, 3);

        // Train 4: Lucknow Swarna Shatabdi Express
        Train t4 = new Train(12004, "Lucknow Swarna Shatabdi", "Delhi", "Lucknow",
                "06:10 AM", "12:40 PM", 1165.00, 20, 3, 3);

        // Train 5: Howrah Rajdhani Express
        Train t5 = new Train(12302, "Howrah Rajdhani Express", "Delhi", "Kolkata",
                "04:50 PM", "09:55 AM", 3120.00, 24, 4, 4);

        // Train 6: Chandigarh - New Delhi Shatabdi Express
        Train t6 = new Train(12046, "Chandigarh Shatabdi", "Chandigarh", "Delhi",
                "12:05 PM", "03:20 PM", 820.00, 18, 3, 3);

        // Train 7: Pooja Superfast Express (Jammu to Jaipur via Delhi)
        Train t7 = new Train(12414, "Pooja Superfast Express", "Delhi", "Jaipur",
                "04:15 AM", "09:35 AM", 560.00, 22, 4, 4);

        // Train 8: August Kranti Tejas Rajdhani
        Train t8 = new Train(12954, "August Kranti Rajdhani", "Delhi", "Mumbai",
                "05:15 PM", "10:05 AM", 2780.00, 24, 4, 4);

        // Train 9: Amritsar Intercity Express
        Train t9 = new Train(12460, "Amritsar Intercity Express", "Amritsar", "Delhi",
                "06:15 AM", "01:30 PM", 640.00, 20, 3, 3);

        // Train 10: Ajmer Shatabdi Express
        Train t10 = new Train(12015, "Ajmer Shatabdi Express", "Delhi", "Jaipur",
                "06:10 AM", "10:40 AM", 890.00, 18, 3, 3);

        Train[] trains = { t1, t2, t3, t4, t5, t6, t7, t8, t9, t10 };

        for (Train train : trains) {
            trainService.addTrain(train);
            seatAllocationService.initializeSeatsForTrain(train);
        }
    }

    /**
     * Initializes sample passengers (including senior citizens) for immediate
     * testing.
     *
     * @param passengerService PassengerService instance
     */
    public static void initializeSamplePassengers(PassengerService passengerService) {
        // Regular passengers
        passengerService.registerPassenger("Rohan Sharma", 28, "Male", "9876543210");
        passengerService.registerPassenger("Priya Desai", 24, "Female", "9811223344");
        passengerService.registerPassenger("Amit Verma", 35, "Male", "9822334455");

        // Senior Citizen passenger (Age >= 60 triggers SeniorCitizenPassenger subclass)
        passengerService.registerPassenger("Dr. Harish Chandra", 68, "Male", "9833445566");
        passengerService.registerPassenger("Shanti Devi", 65, "Female", "9844556677");
    }
}