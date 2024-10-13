package bus;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

enum Berth {
    UPPER,
    LOWER
}

class Passenger {
    private int passengerId;
    private String name;
    private Berth prefBerth;
    private Berth allocatedBerth;

    Passenger(int pId, String name, Berth prefBerth) {
        this.passengerId = pId;
        this.name = name;
        this.prefBerth = prefBerth;
    }

    public int getPassengerId() {
        return passengerId;
    }

    public String getName() {
        return name;
    }

    public Berth getPrefBerth() {
        return prefBerth;
    }

    public Berth getAllocatedBerth() {
        return allocatedBerth;
    }

    public void setAllocatedBerth(Berth allocatedBerth) {
        this.allocatedBerth = allocatedBerth;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrefBerth(Berth prefBerth) {
        this.prefBerth = prefBerth;
    }

    @Override
    public String toString() {
        return "Passenger ID: " + passengerId + "\n" +
                "Name: " + name + "\n" +
                "Preferred Berth: " + prefBerth + "\n" +
                "Allocated Berth: " + allocatedBerth + "\n";
    }
}

public class Project2 {
    static int pId = 1;
    static int availableSeats = 5, upperB = 1, lowerB = 4, waitingListSeats = 6;
    static List<Passenger> bkdDetails = new ArrayList<>();
    static LinkedList<Passenger> waitingList = new LinkedList<>();

    static void allocateBerth(Passenger p) {
        switch (p.getPrefBerth()) {
            case UPPER:
                if (upperB <= 0 && lowerB > 0) {
                    p.setAllocatedBerth(Berth.LOWER);
                    lowerB--;
                } else {
                    p.setAllocatedBerth(Berth.UPPER);
                    upperB--;
                }
                break;
            case LOWER:
                if (lowerB <= 0 && upperB > 0) {
                    p.setAllocatedBerth(Berth.UPPER);
                    upperB--;
                } else {
                    p.setAllocatedBerth(Berth.LOWER);
                    lowerB--;
                }
                break;
        }
    }

    void bookTicket(Passenger p) {
        if (availableSeats < 1) {
            if (waitingList.size() >= 6) {
                System.out.println("Sorry, all seats and waiting list are full.");
                return;
            }
            waitingList.add(p);
            waitingListSeats--;
            System.out.println("No available seats right now, you're in the waiting list.");
            return;
        }
        allocateBerth(p);
        bkdDetails.add(p);
        availableSeats--;
        System.out.println("Ticket booked successfully for " + p.getName() + " (ID: " + p.getPassengerId() + ")");
    }

    void printTicket(int id) {
        if (waitingList.stream().anyMatch(e -> e.getPassengerId() == id)) {
            System.out.println("You're in the waiting list.");
            return;
        }
        Passenger passenger = bkdDetails.stream().filter(e -> e.getPassengerId() == id).findFirst().orElse(null);
        if (passenger != null) {
            System.out.println("Passenger details:");
            System.out.println(passenger);
        } else {
            System.out.println("You're not in any list.");
        }
    }

    static void cancelTicket(int id) {
        // Check if ID exists in the booked ticket list
        Passenger passenger = bkdDetails.stream().filter(e -> e.getPassengerId() == id).findFirst().orElse(null);

        if (passenger != null) {
            // ID found, cancel the ticket
            bkdDetails = bkdDetails.stream().filter(e -> e.getPassengerId() != id).collect(Collectors.toList());
            System.out.println("Your ticket with ID " + id + " is cancelled.");
            
            // Reallocate seat to a waiting list passenger, if any
            if (!waitingList.isEmpty()) {
                Passenger waitingPassenger = waitingList.removeFirst();
                allocateBerth(waitingPassenger);
                bkdDetails.add(waitingPassenger);
                waitingListSeats++;
            } else {
                availableSeats++;
            }
        } else {
            // ID not found, display message
            System.out.println("Passenger with ID " + id + " not found.");
        }
    }

    void modifyPassengerDetails(int id) {
        Scanner sc = new Scanner(System.in);
        Passenger passenger = bkdDetails.stream().filter(e -> e.getPassengerId() == id).findFirst().orElse(null);
        if (passenger != null) {
            System.out.println("Modify Details for Passenger ID: " + id);
            System.out.println("1. Change Name \t 2. Change Preferred Berth");
            int choice = sc.nextInt();
            switch (choice) {
                case 1:
                    System.out.println("Enter new name:");
                    String newName = sc.next();
                    passenger.setName(newName);
                    System.out.println("Name updated successfully.");
                    break;
                case 2:
                    System.out.println("Enter new Preferred Berth (UPPER/LOWER):");
                    String newBerth = sc.next();
                    passenger.setPrefBerth(Berth.valueOf(newBerth));
                    allocateBerth(passenger);
                    System.out.println("Preferred berth updated and reallocated.");
                    break;
            }
        } else {
            System.out.println("Passenger not found.");
        }
    }

    void showBookedPassengers() {
        if (bkdDetails.isEmpty()) {
            System.out.println("No passengers are currently booked.");
        } else {
            System.out.println("Booked Passengers:");
            for (Passenger p : bkdDetails) {
                System.out.println(p);
            }
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String choice = "Y";
        Project2 project = new Project2(); // Create a single instance to reuse methods

        while (choice.equalsIgnoreCase("Y")) {
            System.out.println("1. Book Ticket \t 2. Print Ticket \t 3. Cancel Ticket \t 4. Modify Passenger Details");
            System.out.println("Enter your choice:");
            String option = sc.next();
            switch (option) {
                case "1":
                    System.out.println("Please enter your name:");
                    String name = sc.next();

                    System.out.println("Please enter your preferred Berth (UPPER/LOWER):");
                    String prefBerth = sc.next();
                    Passenger newPassenger = new Passenger(pId, name, Berth.valueOf(prefBerth));
                    project.bookTicket(newPassenger);
                    pId++;
                    break;
                case "2":
                    System.out.println("Please enter your ID:");
                    int id = sc.nextInt();
                    project.printTicket(id);
                    break;
                case "3":
                    System.out.println("Please enter your ID:");
                    int cancelId = sc.nextInt();
                    cancelTicket(cancelId);
                    break;
                case "4":
                    System.out.println("Please enter your ID to modify details:");
                    int modifyId = sc.nextInt();
                    project.modifyPassengerDetails(modifyId);
                    break;
            }
            System.out.println("Do you want to continue? If yes, please enter Y:");
            choice = sc.next();
        }
        sc.close();
    }
}
