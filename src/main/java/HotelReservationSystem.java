import dao.HotelDAO;
import dao.ReservationDAO;
import dao.UserDAO;
import model.Hotel;
import model.Reservation;
import service.AuthService;
import service.HotelService;
import service.ReservationService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;


public class HotelReservationSystem {
    private static final String URL = "jdbc:mysql://localhost:3306/hotelreservation_db";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC Driver not found. Check your classpath.");
            System.err.println(e.getMessage());
            return;
        }

        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             Scanner scanner = new Scanner(System.in)) {

            UserDAO userDAO = new UserDAO(connection);
            AuthService authService = new AuthService(userDAO);

            // 1. --- Authentication Flow ---
            if (!handleLogin(authService, scanner)) {
                System.out.println("Maximum login attempts reached or failed. System exiting.");
                return;
            }

            // 2. --- Hotel Selection Flow ---
            HotelDAO hotelDAO = new HotelDAO(connection);
            HotelService hotelService = new HotelService(hotelDAO);

//            Integer selectedHotelId = selectHotel(hotelService, scanner);
//
//            if (selectedHotelId == null) {
//                System.out.println("No hotel selected. Exiting system.");
//                return;
//            }
            boolean keepRunning = true;

            while (keepRunning) { // This loop keeps the program ALIVE

                // 1. Hotel Selection (This runs first every time)
                Integer selectedHotelId = selectHotel(hotelService, scanner);

                if (selectedHotelId == null) {
                    // User chose '0' from the selectHotel menu, so we shut down.
                    System.out.println("\nThankYou For Using Hotel Reservation System!!!");
                    keepRunning = false;
                    continue; // Break out of this iteration, then the loop ends.
                }

                // 2. Run the Hotel Menu
                try {

                    runSystemMenu(connection, scanner, selectedHotelId);

                } catch (SQLException e) {

                    keepRunning = false;
                }
            }

            // 3. --- MAIN SYSTEM ---
//            System.out.println("\nSuccessfully selected Hotel ID: " + selectedHotelId);
//            runSystemMenu(connection, scanner, selectedHotelId);

        } catch (SQLException e) {
            System.err.println("Database Connection Error. Check URL, Username, and Password.");
            System.err.println(e.getMessage());
        }
    }

    // --- Controller Logic: Login ---
    private static boolean handleLogin(AuthService authService, Scanner scanner) {
        int attempts = 0;
        final int MAX_ATTEMPTS = 3;

        while (attempts < MAX_ATTEMPTS) {
            System.out.println("\n--- SYSTEM LOGIN ---");
            System.out.print("Username: ");
            String username = scanner.next();
            System.out.print("Password: ");
            String password = scanner.next();

            try {
                if (authService.login(username, password)) {
                    return true;
                } else {
                    System.out.println("Invalid credentials. Attempt " + (attempts + 1) + " of " + MAX_ATTEMPTS);
                    attempts++;
                }
            } catch (SQLException e) {
                System.err.println("A database error occurred during login: " + e.getMessage());
                return false;
            }
        }
        return false;
    }

    // --- Controller Logic: Hotel Selection (NEW) ---
    private static Integer selectHotel(HotelService service, Scanner scanner) {
        try {
            List<Hotel> hotels = service.getAvailableHotels();

            if (hotels.isEmpty()) {
                System.out.println("No hotels are currently available in the system.");
                return null;
            }

            System.out.println("\n==========================================================================");
            System.out.println(" AVAILABLE HOTELS (Select Your Destination) ");
            System.out.println("==========================================================================");
            System.out.println("| ID   | Name                 | City            | Rating | Address");
            System.out.println("--------------------------------------------------------------------------");

            for (Hotel hotel : hotels) {
                System.out.println(hotel);
            }
            System.out.println("--------------------------------------------------------------------------");

            int selectedId = -1;
            while (service.findHotelById(selectedId) == null) {
                System.out.print("Enter the ID of the hotel you wish to book (0 to Exit): ");
                if (scanner.hasNextInt()) {
                    selectedId = scanner.nextInt();
                    if (selectedId == 0) return null;
                    if (service.findHotelById(selectedId) == null) {
                        System.out.println("Invalid Hotel ID. Please try again.");
                    }
                } else {
                    System.out.println("Invalid input. Please enter a number.");
                    scanner.next(); // consume invalid input
                }
            }
            return selectedId;

        } catch (SQLException e) {
            System.err.println("SQL Error during hotel retrieval: " + e.getMessage());
            return null;
        }
    }


    // --- Controller Logic: Menu and Delegation (MODIFIED) ---
    private static boolean runSystemMenu(Connection connection, Scanner scanner, int selectedHotelId) throws SQLException {
        ReservationDAO dao = new ReservationDAO(connection);
        ReservationService service = new ReservationService(dao);

        HotelService hotelService = new HotelService(new HotelDAO(connection));
        String hotelName = hotelService.findHotelById(selectedHotelId).getName();

        while (true) {
            System.out.println("\n--- Reservation Menu for: " + hotelName + " (ID: " + selectedHotelId + ") ---");
            System.out.println("1. Reserve a room");
            System.out.println("2. View Reservations");
            System.out.println("3. Get Room Number");
            System.out.println("4. Update Reservations");
            System.out.println("5. Delete Reservations");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        reserveRoom(service, scanner, selectedHotelId);
                        break;
                    case 2:
                        viewReservations(service, selectedHotelId);
                        break;
                    case 3:
                        getRoomNumber(service, scanner, selectedHotelId);
                        break;
                    case 4:
                        updateReservation(service, scanner);
                        break;
                    case 5:
                        deleteReservation(service, scanner);
                        break;
                    case 0:
                        System.out.println("Returning to Hotel Selection...");
                        return true;

                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next();
            }
        }
    }

    private static void reserveRoom(ReservationService service, Scanner scanner, int hotelId) {
        try {
            System.out.print("Enter guest name: ");
            scanner.nextLine();
            String guestName = scanner.nextLine();
            System.out.print("Enter room number (100-999): ");
            int roomNumber = scanner.nextInt();
            System.out.print("Enter contact number: ");
            String contactNumber = scanner.next();

            Reservation newReservation = new Reservation(guestName, roomNumber, contactNumber);

            // Pass the hotelId to the service layer
            if (service.makeReservation(newReservation, hotelId)) {
                System.out.println("Reservation successful at Hotel ID: " + hotelId + "!");
            } else {
                System.out.println("Reservation failed (Check room number range or DB error).");
            }
        } catch (SQLException e) {
            System.err.println("SQL Error during reservation: " + e.getMessage());
        }
    }

    private static void viewReservations(ReservationService service, int hotelId) {
        try {
            List<Reservation> reservations = service.findAllReservations(hotelId); // Filter by hotelId

            System.out.println("\nCurrent Reservations (Filterd by Hotel ID: " + hotelId + "):");
            System.out.println("+----------------+-----------------+---------------+----------------------+");
            System.out.println("| Reservation ID | Guest           | Room Number   | Contact Number       |");
            System.out.println("+----------------+-----------------+---------------+----------------------+");

            if (reservations.isEmpty()) {
                System.out.println("| No reservations found for this hotel.                                               |");
            } else {
                for (Reservation r : reservations) {
                    System.out.println(r);
                }
            }
            System.out.println("+----------------+-----------------+---------------+----------------------+");
        } catch (SQLException e) {
            System.err.println("SQL Error while viewing reservations: " + e.getMessage());
        }
    }

    private static void getRoomNumber(ReservationService service, Scanner scanner, int hotelId) {
        try {
            System.out.print("Enter reservation ID: ");
            int reservationId = scanner.nextInt();
            System.out.print("Enter guest name: ");
            String guestName = scanner.next();

            // Pass the hotelId for filtering
            Integer roomNumber = service.findRoomNumber(reservationId, guestName, hotelId);

            if (roomNumber != null) {
                System.out.println("Room number for Reservation ID " + reservationId + " at this hotel is: " + roomNumber);
            } else {
                System.out.println("Reservation not found for the given details in this hotel.");
            }
        } catch (SQLException e) {
            System.err.println("SQL Error during room number retrieval: " + e.getMessage());
        }
    }

    private static void updateReservation(ReservationService service, Scanner scanner) {
        try {
            System.out.print("Enter reservation ID to update: ");
            int id = scanner.nextInt();
            scanner.nextLine();

            if (!service.reservationExists(id)) {
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            System.out.print("Enter new guest name: ");
            String newGuestName = scanner.nextLine();
            System.out.print("Enter new room number: ");
            int newRoomNumber = scanner.nextInt();
            System.out.print("Enter new contact number: ");
            String newContactNumber = scanner.next();

            if (service.modifyReservation(id, newGuestName, newRoomNumber, newContactNumber)) {
                System.out.println("Reservation updated successfully!");
            } else {
                System.out.println("Reservation update failed (check room number range or DB error).");
            }
        } catch (SQLException e) {
            System.err.println("SQL Error during update: " + e.getMessage());
        }
    }

    private static void deleteReservation(ReservationService service, Scanner scanner) {
        try {
            System.out.print("Enter reservation ID to delete: ");
            int id = scanner.nextInt();

            if (!service.reservationExists(id)) {
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            if (service.cancelReservation(id)) {
                System.out.println("Reservation deleted successfully!");
            } else {
                System.out.println("Reservation deletion failed.");
            }
        } catch (SQLException e) {
            System.err.println("SQL Error during deletion: " + e.getMessage());
        }
    }
}


