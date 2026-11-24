# TrackSphere - Complete Viva Preparation & Code Explanation Guide
## Course: Programming in Java (25CSE0202) – Continuous Evaluation 1
### Chitkara University Institute of Engineering & Technology

This guide provides simple, beginner-friendly explanations in plain English for all 21 classes, design patterns, and core Java concepts used in **TrackSphere**. Use this document to confidently prepare for your viva and answer any examiner question.

---

## PART 1: Core OOP Concepts & Viva Q&A

### Q1. Why did we use Inheritance in TrackSphere?
**Simple English Answer:**  
Inheritance allows a child class to inherit common fields and methods from a parent class, avoiding code duplication.  
In TrackSphere:
- `Person` is the parent class containing common attributes: `name`, `age`, `gender`, and `mobileNumber`.
- `Passenger` extends `Person`, adding a unique `passengerId`.
- `SeniorCitizenPassenger` extends `Passenger`, adding a 40% fare concession and automated preference for Lower Berths.  
This demonstrates **Multilevel Inheritance** (`Person` -> `Passenger` -> `SeniorCitizenPassenger`).

### Q2. How is Runtime Polymorphism (Dynamic Method Dispatch) demonstrated?
**Simple English Answer:**  
Polymorphism means "many forms". At runtime, Java decides which method to call based on the actual object created in memory, not the reference variable type.  
In TrackSphere:
```java
Person p1 = new Passenger("PS-101", "Aarav", 25, "Male", "9876543210");
Person p2 = new SeniorCitizenPassenger("PS-102", "Dr. Harish", 68, "Male", "9833445566");

p1.displayDetails(); // Calls Passenger's displayDetails() -> General Passenger
p2.displayDetails(); // Calls SeniorCitizenPassenger's displayDetails() -> Senior Citizen with 40% discount
```
The exact same method call `displayDetails()` exhibits different behavior at runtime depending on the object type.

### Q3. Why is `Person` declared as an `abstract` class?
**Simple English Answer:**  
In a real railway system, a passenger is never just a "generic person"—they are always a specific type of traveler (General Passenger or Senior Citizen).  
Making `Person` abstract prevents direct instantiation (`new Person()` is invalid) and forces child classes to provide a concrete implementation of `public abstract String getPassengerType();`.

### Q4. What is the difference between `Comparable` and `Comparator`?
**Simple English Answer:**  
- **`Comparable` (Natural Sorting):** Implemented inside the `Train` class using `compareTo()`. It defines the default sorting order of trains by their Train Number (`Integer.compare(this.trainNumber, other.trainNumber)`). It requires modifying the class itself.
- **`Comparator` (Custom Sorting):** Implemented externally without modifying the `Train` class. We used anonymous `Comparator<Train>` classes in `TrainService` to sort trains by **Lowest Fare** and by **Departure Time**.

---

## PART 2: Java Collections Framework Justification

### Q5. Why did you choose `HashMap` for storing tickets instead of an `ArrayList`?
**Simple English Answer:**  
An `ArrayList` requires searching sequentially from index 0 to $N-1$ ($O(N)$ time complexity). If there are 10,000 tickets, searching for a PNR would take up to 10,000 comparisons.  
A `HashMap<String, Ticket>` uses hash buckets and key-value mapping. It locates any ticket using its 10-digit PNR in **$O(1)$ constant time**, which is instantaneous.

### Q6. Why did you choose `LinkedList` for RAC and Waiting List queues?
**Simple English Answer:**  
A railway waitlist functions on a strict First-In, First-Out (FIFO) basis.  
In an `ArrayList`, deleting the first element requires shifting all remaining elements to the left, which takes $O(N)$ time.  
In a `LinkedList`, removing the head element (`removeFirst()`) or adding to the tail (`addLast()`) merely updates node pointers in **$O(1)$ constant time**, making it ideal for queue operations.

### Q7. Why did you use `HashSet` for PNR numbers?
**Simple English Answer:**  
IRCTC tickets must have 100% unique 10-digit PNR numbers. A `HashSet` does not permit duplicate entries and provides $O(1)$ lookup via `contains(pnr)`. Before issuing any ticket, the system checks the `HashSet` to guarantee no duplicate PNR is ever generated.

### Q8. Where are `Iterator` and `ListIterator` used?
**Simple English Answer:**  
- `Iterator<Train>` is used in `TrainService.searchTrains()` to safely traverse the list of trains and filter by source/destination city.
- `ListIterator<Passenger>` is used in `PassengerService.displayAllPassengers()` and `WaitlistService.renumberQueue()` because `ListIterator` supports bidirectional traversal and safe element inspection without index out-of-bounds risks.

---

## PART 3: Multithreading & Synchronization

### Q9. What is a Race Condition? How did you demonstrate it?
**Simple English Answer:**  
A race condition occurs when two or more threads access shared mutable data concurrently, and the final outcome depends on the unpredictable timing or thread scheduling of the operating system.  
**Demonstration in TrackSphere (Option 12):**  
- Scenario: Train #12952 has **only 1 remaining seat** (`availableSeats = 1`).
- Two threads (Rahul and Simran) try to book that single seat at the same instant.
- Both threads call `inventory.bookSeatUnsafe()`.
- Thread-1 reads `availableSeats > 0` (True). While Thread-1 sleeps for 120ms (simulating server/network latency), Thread-2 runs and ALSO reads `availableSeats > 0` (True).
- Both threads decrement the counter and allocate the seat.
- Result: `availableSeats` drops to `-1`, and both passengers receive the same seat number! This is the classic **double-booking bug**.

### Q10. How does `synchronized` solve the race condition?
**Simple English Answer:**  
In `SharedSeatInventory.bookSeatSafe()`, we added the `synchronized` keyword to the method signature.  
In Java, every object has an **intrinsic lock (monitor lock)**.
- When Thread-1 calls `bookSeatSafe()`, it acquires the lock on the `SharedSeatInventory` object.
- While Thread-1 holds the lock, Thread-2 cannot enter the method and is forced into the `BLOCKED` state.
- Thread-1 checks availability, assigns Seat #1, decrements available seats to 0, and releases the lock.
- Thread-2 enters the method, sees `availableSeats == 0`, and places the passenger into RAC or Waiting List.
- Result: **Zero duplicate bookings** and 100% inventory integrity.

### Q11. Explain the Thread Lifecycle demonstrated in Option 14.
**Simple English Answer:**  
Using `Thread.getState()`, we track actual observable thread states:
1. **NEW:** When `new Thread(runnable)` is instantiated before `start()` is called.
2. **RUNNABLE:** Immediately after calling `thread.start()`. The thread is executing or ready to execute in the JVM.
3. **TIMED_WAITING:** When the thread executes `Thread.sleep(600)`. It pauses for a specified duration.
4. **TERMINATED:** Once the `run()` method finishes and `thread.join()` returns in the main thread.
We also demonstrated:
- **Daemon Threads:** Set using `thread.setDaemon(true)` to run background monitor tasks.
- **Thread Interrupt:** Using `thread.interrupt()` to wake up a sleeping thread and catch `InterruptedException`.

---

## PART 4: Class-by-Class Walkthrough (All 21 Classes)

### Model Package (7 Classes)

#### 1. `Person.java`
- **Purpose:** Abstract base entity representing any human in the system.
- **Why created:** To avoid duplicating name, age, gender, and mobile attributes.
- **Data stored:** `name`, `age`, `gender`, `mobileNumber`.
- **Key methods:** `getPassengerType()` (abstract), `displayDetails()`, getters/setters.
- **Collaborates with:** `Passenger`, `SeniorCitizenPassenger`.
- **Java concept:** Abstraction, Encapsulation, StringBuilder.
- **Viva explanation:** "Person is an abstract parent class holding common personal data that cannot be directly instantiated."

#### 2. `Passenger.java`
- **Purpose:** Represents a standard general railway passenger.
- **Why created:** To store passenger-specific identification (`passengerId`).
- **Data stored:** Inherits from `Person`, plus `passengerId`.
- **Key methods:** `getPassengerType()`, `displayDetails()`, `toString()`.
- **Collaborates with:** `Person`, `Ticket`, `PassengerService`.
- **Java concept:** Single Inheritance, `super` keyword, method overriding.
- **Viva explanation:** "Passenger extends Person and overrides getPassengerType() to return 'General Passenger'."

#### 3. `SeniorCitizenPassenger.java`
- **Purpose:** Represents an elderly passenger (age >= 60) entitled to concessions and lower-berth priority.
- **Why created:** To model special railway rules for senior citizens cleanly through OOP.
- **Data stored:** Inherits from `Passenger`, plus `concessionPercentage` (40%) and `prefersLowerBerth` (true).
- **Key methods:** `calculateDiscountedFare()`, `displayDetails()`.
- **Collaborates with:** `PassengerService`, `BookingService`, `SeatAllocationService`.
- **Java concept:** Multilevel Inheritance, Runtime Polymorphism.
- **Viva explanation:** "Demonstrates runtime polymorphism where senior citizens automatically get a 40% fare discount and lower-berth preference."

#### 4. `Train.java`
- **Purpose:** Represents a scheduled railway train.
- **Why created:** To manage train routes, timings, fares, and seat quotas.
- **Data stored:** `trainNumber`, `trainName`, `source`, `destination`, `baseFare`, quotas (`totalSeats`, `availableSeats`, `racSeats`, `waitingListSeats`), and `coachLayout[][]`.
- **Key methods:** `compareTo()` (Comparable), `displayTrainInfo()`, `displayCoachLayout()`.
- **Collaborates with:** `TrainService`, `Ticket`, `SeatAllocationService`.
- **Java concept:** Comparable interface, Multidimensional Array (`String[][] coachLayout`).
- **Viva explanation:** "Train stores railway schedules and implements Comparable to enable natural sorting by train number."

#### 5. `Seat.java`
- **Purpose:** Represents an individual physical berth inside a train.
- **Why created:** To track berth types (`LOWER`, `MIDDLE`, `UPPER`, `SIDE_LOWER`, `SIDE_UPPER`) and booking state.
- **Data stored:** `seatNumber`, `berthType`, `isBooked`, `allocatedPassengerId`.
- **Key methods:** `bookSeat(passengerId)`, `releaseSeat()`.
- **Collaborates with:** `SeatAllocationService`, `Ticket`.
- **Java concept:** Encapsulation and state management.
- **Viva explanation:** "Seat tracks whether an individual berth is booked, its berth type, and who is sitting there."

#### 6. `Ticket.java`
- **Purpose:** Represents an issued railway passenger reservation.
- **Why created:** To tie passenger, train, seat, fare, and status together.
- **Data stored:** `pnr`, `passenger`, `train`, `seat`, `fare`, `bookingStatus` (`CONFIRMED`, `RAC`, `WAITING_LIST`, `CANCELLED`), `bookingDate`.
- **Key methods:** `generateTicketSummary()` (uses StringBuilder), `getFormattedStatus()`.
- **Collaborates with:** `Passenger`, `Train`, `Seat`, `BookingService`.
- **Java concept:** Association, StringBuilder, String formatting.
- **Viva explanation:** "Ticket connects the passenger, train, and seat, and formats the printable boarding pass."

#### 7. `Booking.java`
- **Purpose:** Represents a complete booking transaction record.
- **Why created:** To maintain financial and audit records (payment mode, transaction status).
- **Data stored:** `bookingId`, `ticket`, `bookingTime`, `paymentMode`, `paymentStatus`.
- **Key methods:** `displayBookingReceipt()`.
- **Collaborates with:** `Ticket`, `BookingService`.
- **Java concept:** Composition, Encapsulation.
- **Viva explanation:** "Booking stores the financial transaction details and payment status for each reservation."

---

### Service Package (6 Classes)

#### 8. `TrainService.java`
- **Purpose:** Manages the catalog of trains, route search, and sorting.
- **Data stored:** `ArrayList<Train>`, `TreeMap<Integer, Train>`, `TreeSet<Integer>`.
- **Key methods:** `searchTrains()`, `sortByFare()`, `sortByDepartureTime()`, `sortByTrainNumber()`.
- **Java concept:** Collections (`List`, `TreeMap`, `TreeSet`), `Iterator`, `Comparator`.
- **Viva explanation:** "TrainService searches and sorts trains using custom Comparators without lambda expressions."

#### 9. `PassengerService.java`
- **Purpose:** Registers passengers and retrieves passenger records.
- **Data stored:** `ArrayList<Passenger>`, `HashMap<String, Passenger>`.
- **Key methods:** `registerPassenger()`, `getPassengerById()`, `displayAllPassengers()`.
- **Java concept:** Polymorphic instantiation, `ListIterator` traversal.
- **Viva explanation:** "PassengerService automatically creates a SeniorCitizenPassenger if the age entered is 60 or higher."

#### 10. `SeatAllocationService.java`
- **Purpose:** Allots physical berths and handles passenger berth preferences.
- **Data stored:** `HashMap<Integer, List<Seat>>` mapping train numbers to their list of physical seats.
- **Key methods:** `initializeSeatsForTrain()`, `allocateSeat()`, `releaseSeat()`.
- **Java concept:** Business logic encapsulation, priority fallback allocation.
- **Viva explanation:** "SeatAllocationService checks berth availability, gives lower berths to seniors, and avoids duplicate seat allocation."

#### 11. `WaitlistService.java`
- **Purpose:** Manages RAC and Waiting List queues with FIFO promotion.
- **Data stored:** `HashMap<Integer, LinkedList<Ticket>>` for RAC and Waiting List queues.
- **Key methods:** `addToRAC()`, `addToWaitingList()`, `promoteNextPassenger()`.
- **Java concept:** `LinkedList` as a FIFO Queue (`addLast()`, `removeFirst()`).
- **Viva explanation:** "WaitlistService uses LinkedList to maintain FIFO queues. When a confirmed seat is cancelled, RAC-1 promotes to Confirmed and WL-1 promotes to RAC-1."

#### 12. `CancellationService.java`
- **Purpose:** Cancels tickets, releases seats, triggers promotions, and computes refunds.
- **Key methods:** `cancelTicket()`, `displayCancellationReceipt()`.
- **Java concept:** Custom exception handling (`InvalidBookingException`), cross-service orchestration.
- **Viva explanation:** "CancellationService calculates itemized refunds (80% Confirmed, 90% RAC, 95% WL) and promotes the next waiting passenger."

#### 13. `BookingService.java`
- **Purpose:** Central coordinator for booking tickets, generating unique PNRs, and managing booking history.
- **Data stored:** `HashMap<String, Ticket>` (PNR -> Ticket), `HashSet<String>` (unique PNRs), `ArrayList<Booking>`, `LinkedHashSet<String>`.
- **Key methods:** `generateUniquePNR()`, `bookTicket()`, `displayBookingHistory()`.
- **Java concept:** Method Overloading, `HashSet`, `LinkedHashSet`.
- **Viva explanation:** "BookingService orchestrates the booking process and guarantees unique 10-digit PNRs."

---

### Concurrency Package (3 Classes)

#### 14. `BookingTask.java`
- **Purpose:** `Runnable` worker executing a passenger's booking request in an independent thread.
- **Key methods:** `run()`.
- **Java concept:** `Runnable` interface, `Thread.currentThread().getName()`, `Thread.sleep()`.
- **Viva explanation:** "BookingTask implements Runnable so each passenger's booking attempt can run in its own thread."

#### 15. `SharedSeatInventory.java`
- **Purpose:** Maintains shared seat counters accessed concurrently by threads.
- **Key methods:** `bookSeatSafe()` (`synchronized`), `bookSeatUnsafe()` (unsynchronized).
- **Java concept:** Critical sections, race conditions, intrinsic monitor locks, `StringBuffer`.
- **Viva explanation:** "Contains the unsafe booking method that triggers a race condition and the synchronized method that completely eliminates it."

#### 16. `BookingManager.java`
- **Purpose:** Spawns and manages multi-threaded booking simulations.
- **Key methods:** `simulateConcurrentBookings()`, `demonstrateRaceCondition()`, `demonstrateSynchronization()`, `demonstrateThreadLifecycle()`.
- **Java concept:** `Thread`, `start()`, `join()`, `setPriority()`, `setDaemon()`, `interrupt()`, `getState()`.
- **Viva explanation:** "Coordinates concurrent threads using join() so the main program waits for all threads to finish before printing results."

---

### Exception & Utility Packages (4 Classes)

#### 17. `InvalidBookingException.java`
- **Purpose:** Custom checked exception for business rule violations (e.g. invalid PNR, repeat cancellation).
- **Java concept:** Custom Checked Exceptions, Exception hierarchy.

#### 18. `SeatNotAvailableException.java`
- **Purpose:** Custom checked exception thrown when all quotas (Confirmed, RAC, and WL) are completely full.
- **Java concept:** Custom Exception handling.

#### 19. `InputValidator.java`
- **Purpose:** Reusable static validation methods and crash-proof console input readers.
- **Key methods:** `isValidName()`, `isValidMobile()`, `readValidInteger()`.
- **Java concept:** Regex, String manipulation, `try-catch` for `NumberFormatException`.

#### 20. `RailwayData.java`
- **Purpose:** Preloads 10 scheduled Indian Railways express trains and sample passengers.
- **Java concept:** Static initializer methods, data preloading.

---

### Main Application (1 Class)

#### 21. `Main.java`
- **Purpose:** Entry point (`main()`) presenting the streamlined 15-option console menu.
- **Java concept:** Clean architectural coordination, separation of concerns.
