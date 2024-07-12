import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.InputMismatchException;
import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

class Reservation {
    private int id;
    private String name;
    private LocalDate date;
    private int numberOfGuests;

    public Reservation(int id, String name, LocalDate date, int numberOfGuests) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.numberOfGuests = numberOfGuests;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setNumberOfGuests(int numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }
}

class ReservationSystem {
    private List<Reservation> reservations = new ArrayList<>();
    private int nextId = 1;
    private static final Logger logger = Logger.getLogger(ReservationSystem.class.getName());

    public ReservationSystem() {
        try {
            FileHandler fh = new FileHandler("reservations.log", true);
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Reservation makeReservation(String name, LocalDate date, int numberOfGuests) {
        Reservation reservation = new Reservation(nextId++, name, date, numberOfGuests);
        reservations.add(reservation);
        logger.info("Reservation made: " + reservation.getId() + " - " + name + " - " + date + " - " + numberOfGuests);
        return reservation;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public Reservation getReservationById(int id) {
        for (Reservation reservation : reservations) {
            if (reservation.getId() == id) {
                return reservation;
            }
        }
        return null;
    }

    public boolean cancelReservation(int id) {
        Reservation reservation = getReservationById(id);
        if (reservation != null) {
            reservations.remove(reservation);
            logger.info("Reservation canceled: " + id);
            return true;
        }
        return false;
    }

    public boolean updateReservation(int id, String name, LocalDate date, int numberOfGuests) {
        Reservation reservation = getReservationById(id);
        if (reservation != null) {
            reservation.setName(name);
            reservation.setDate(date);
            reservation.setNumberOfGuests(numberOfGuests);
            logger.info("Reservation updated: " + id + " - " + name + " - " + date + " - " + numberOfGuests);
            return true;
        }
        return false;
    }

    public List<Reservation> searchReservations(String query) {
        List<Reservation> result = new ArrayList<>();
        for (Reservation reservation : reservations) {
            if (reservation.getName().toLowerCase().contains(query.toLowerCase()) ||
                    reservation.getDate().toString().equals(query)) {
                result.add(reservation);
            }
        }
        return result;
    }
}
public class ReservationSystemUI {
    private ReservationSystem reservationSystem = new ReservationSystem();
    private Scanner scanner = new Scanner(System.in);
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void start() {
        while (true) {
            System.out.println("1. Make a reservation");
            System.out.println("2. View all reservations");
            System.out.println("3. Cancel a reservation");
            System.out.println("4. Update a reservation");
            System.out.println("5. Search reservations");
            System.out.println("6. Exit");

            int choice = getIntInput("Choose an option: ");
            switch (choice) {
                case 1:
                    makeReservation();
                    break;
                case 2:
                    viewAllReservations();
                    break;
                case 3:
                    cancelReservation();
                    break;
                case 4:
                    updateReservation();
                    break;
                case 5:
                    searchReservations();
                    break;
                case 6:
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice, please try again.");
            }
            System.out.println();
        }
    }

    private void makeReservation() {
        String name = getStringInput("Name: ");
        LocalDate date = getDateInput("Date (YYYY-MM-DD): ");
        int numberOfGuests = getIntInput("Number of guests: ");
        Reservation reservation = reservationSystem.makeReservation(name, date, numberOfGuests);
        System.out.println("Reservation made with ID " + reservation.getId());
    }

    private void viewAllReservations() {
        System.out.println("Reservations:");
        for (Reservation r : reservationSystem.getReservations()) {
            System.out.println(r.getId() + " - " + r.getName() + " - " + r.getDate() + " - " + r.getNumberOfGuests());
        }
    }

    private void cancelReservation() {
        int id = getIntInput("Reservation ID to cancel: ");
        if (reservationSystem.cancelReservation(id)) {
            System.out.println("Reservation canceled");
        } else {
            System.out.println("Reservation not found");
        }
    }

    private void updateReservation() {
        int id = getIntInput("Reservation ID to update: ");
        String name = getStringInput("New Name: ");
        LocalDate date = getDateInput("New Date (YYYY-MM-DD): ");
        int numberOfGuests = getIntInput("New Number of guests: ");
        if (reservationSystem.updateReservation(id, name, date, numberOfGuests)) {
            System.out.println("Reservation updated");
        } else {
            System.out.println("Reservation not found");
        }
    }

    private void searchReservations() {
        String query = getStringInput("Enter name or date to search: ");
        List<Reservation> results = reservationSystem.searchReservations(query);
        if (results.isEmpty()) {
            System.out.println("No reservations found");
        } else {
            System.out.println("Search results:");
            for (Reservation r : results) {
                System.out.println(r.getId() + " - " + r.getName() + " - " + r.getDate() + " - " + r.getNumberOfGuests());
            }
        }
    }

    private String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private int getIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input, please enter a number.");
                scanner.next();
            }
        }
    }

    private LocalDate getDateInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return LocalDate.parse(scanner.next(), dateFormatter);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format, please enter in YYYY-MM-DD format.");
            }
        }
    }

    public static void main(String[] args) {
        ReservationSystemUI ui = new ReservationSystemUI();
        ui.start();
    }
}
