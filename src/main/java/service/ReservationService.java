package service;

import dao.ReservationDAO;
import model.Reservation;
import java.sql.SQLException;
import java.util.List;

public class ReservationService {
    private final ReservationDAO reservationDAO;

    public ReservationService(ReservationDAO reservationDAO) {
        this.reservationDAO = reservationDAO;
    }

    // 1. CREATE RESERVATION (MODIFIED to accept hotelId)
    public boolean makeReservation(Reservation reservation, int hotelId) throws SQLException {
        // Business Rule: Room number must be between 100 and 999
        if (reservation.getRoomNumber() < 100 || reservation.getRoomNumber() > 999) {
            System.out.println("Service Error: Room number must be between 100 and 999.");
            return false;
        }
        return reservationDAO.createReservation(reservation, hotelId); // Pass hotelId to DAO
    }

    // 2. READ ALL RESERVATIONS (MODIFIED to filter by hotelId)
    public List<Reservation> findAllReservations(int hotelId) throws SQLException {
        return reservationDAO.getAllReservations(hotelId);
    }

    // 3. GET ROOM NUMBER (MODIFIED to filter by hotelId)
    public Integer findRoomNumber(int reservationId, String guestName, int hotelId) throws SQLException {
        return reservationDAO.getRoomNumberByDetails(reservationId, guestName, hotelId);
    }

    // 4. UPDATE RESERVATIONS (Unchanged Logic, relies on existing ID)
    public boolean modifyReservation(int id, String newGuestName, int newRoomNumber, String newContactNumber) throws SQLException {
        Reservation existingReservation = reservationDAO.getReservationById(id);
        if (existingReservation == null) {
            return false;
        }

        existingReservation.setGuestName(newGuestName);
        existingReservation.setRoomNumber(newRoomNumber);
        existingReservation.setContactNumber(newContactNumber);

        return reservationDAO.updateReservation(existingReservation);
    }

    // 5. DELETE RESERVATIONS (Unchanged Logic)
    public boolean cancelReservation(int reservationId) throws SQLException {
        return reservationDAO.deleteReservation(reservationId);
    }

    // Helper to check if reservation exists
    public boolean reservationExists(int reservationId) throws SQLException {
        return reservationDAO.getReservationById(reservationId) != null;
    }
}