# TrackSphere - Evaluation 1 Test Cases & Verification Report
## Course: Programming in Java (25CSE0202) – November 2026
### Target: 20 Core Verification Scenarios

This report documents the verification results for all 20 evaluation test cases executed via `com.tracksphere.TrackSphereTestSuite`.

---

## Summary Matrix

| Test ID | Test Description | Module / Component | Expected Outcome | Actual Outcome | Status |
|:---:|---|---|---|---|:---:|
| **TC-01** | Passenger Registration | PassengerService | Assigns `PS-` ID and stores record | Created `PS-106` (`General Passenger`) | **PASS** |
| **TC-02** | Invalid Passenger Validation | InputValidator | Rejects numeric names, age 150, short mobile | All 3 invalid inputs rejected | **PASS** |
| **TC-03** | Train Search by Route | TrainService | Finds trains from Delhi to Bhopal | Found Train #12002 (Bhopal Shatabdi) | **PASS** |
| **TC-04** | Train Sorting by Fare | TrainService | Ascending order by ticket fare | Lowest: Rs.560 -> Highest: Rs.3120 | **PASS** |
| **TC-05** | Confirmed Ticket Booking | BookingService | Confirmed ticket with 10-digit PNR & Seat # | Confirmed PNR issued with Lower berth | **PASS** |
| **TC-06** | RAC Quota Allocation | WaitlistService | Assigns RAC status when confirmed full | Ticket assigned `RAC-1` | **PASS** |
| **TC-07** | Waiting-List Allocation | WaitlistService | Assigns WL status when RAC full | Ticket assigned `WL-1` | **PASS** |
| **TC-08** | Unique PNR Generation | BookingService | 50 sequential PNRs without collision | 50 unique 10-digit PNRs generated | **PASS** |
| **TC-09** | PNR Ticket Lookup | BookingService | $O(1)$ HashMap search returns ticket | Ticket found for Rohan Sharma | **PASS** |
| **TC-10** | Ticket Cancellation | CancellationService | Status CANCELLED & seat released | Ticket cancelled with refund calculated | **PASS** |
| **TC-11** | Itemized Refund Policy | CancellationService | 80% Confirmed, 90% RAC, 95% WL | Deductions and refunds verified | **PASS** |
| **TC-12** | FIFO Waitlist Promotion | WaitlistService | RAC-1 -> Confirmed; WL-1 -> RAC-1 | Both passengers promoted sequentially | **PASS** |
| **TC-13** | Senior Concession & Berth | Passenger & Booking | 40% discount & auto Lower berth | Senior passenger given 40% off & Lower berth | **PASS** |
| **TC-14** | 2D Coach Layout Array | Train Model | 8 bays x 6 berths 2D matrix | Validated 48 berth coach matrix | **PASS** |
| **TC-15** | Multi-Threaded Bookings | Concurrency | 8 concurrent threads execute with `join()` | All 8 threads completed cleanly | **PASS** |
| **TC-16** | Race Condition Demo | Concurrency | Unsafe booking causes negative seats | Negative inventory / double booking seen | **PASS** |
| **TC-17** | Synchronization Demo | Concurrency | Mutex lock prevents double booking | Exactly 1 confirmed seat allotted | **PASS** |
| **TC-18** | Invalid PNR Handling | CancellationService | Throws `InvalidBookingException` | Non-existent PNR handled safely | **PASS** |
| **TC-19** | Duplicate Cancellation | CancellationService | Rejects repeat cancellation request | Repeat cancellation blocked safely | **PASS** |
| **TC-20** | Final Seat Integrity | System Verification | Seat counters remain non-negative | All train counters mathematically sound | **PASS** |

---

## Detailed Test Case Evidence

### Test Case 12: FIFO Waitlist Promotion Evidence
```text
CANCELLATION & PROMOTION LOG:
  [Cancellation] Passenger Rohan Sharma cancelled Confirmed Ticket (PNR: 6671803606).
  [Promotion] RAC Ticket (PNR: 8320039075, Passenger: Priya Desai) promoted to CONFIRMED (Seat #1).
  [Promotion] WL Ticket (PNR: 8122508629, Passenger: Amit Verma) promoted to RAC-1.
```
*Verification:* Demonstrates the multi-stage queue progression required by Indian Railways.

### Test Case 16: Race Condition (Unsafe Execution) Evidence
```text
SCENARIO: availableSeats = 1
  [UNSAFE] Rahul Khanna  by BookingThread-Rahul  --> Allocated Seat #1 (Remaining: -1)
  [UNSAFE] Simran Roy    by BookingThread-Simran --> Allocated Seat #2 (Remaining: -1)
>>> FLAW DETECTED (RACE CONDITION CONFIRMED!) <<<
CRITICAL ERROR: Multiple threads read availableSeats=1 BEFORE either thread updated it!
Duplicate Seat Allocations: Both passengers were allotted tickets when only 1 existed.
```

### Test Case 17: Synchronization (Thread-Safe Solution) Evidence
```text
SCENARIO: availableSeats = 1, racSeats = 1
  [CONFIRMED] Sunita Rao       by SafeThread-Sunita --> Seat #1 | Remaining Confirmed: 0
  [RAC]       Vikram Malhotra  by SafeThread-Vikram --> RAC Slot Allotted | Remaining RAC: 0
>>> OUTCOME (MUTUAL EXCLUSION CONFIRMED) <<<
SUCCESS: Exactly ONE passenger received Confirmed Seat #1!
Duplicate Seat Allocations: ZERO (0). Double-booking is 100% prevented.
```

### Overall Suite Result
```text
==========================================================================================
   TEST EXECUTION SUMMARY: 20 / 20 TESTS PASSED (100% SUCCESS RATE)
==========================================================================================
```
