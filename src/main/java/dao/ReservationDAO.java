package dao;

import model.Reservation;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {
    private final Connection connection;

    public ReservationDAO(Connection connection) {
        this.connection = connection;
    }

    // CREATE
    public boolean createReservation(Reservation reservation, int hotelId) throws SQLException {
        String sql = "INSERT INTO reservations (guest_name, room_number, contact_number, hotel_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, reservation.getGuestName());
            preparedStatement.setInt(2, reservation.getRoomNumber());
            preparedStatement.setString(3, reservation.getContactNumber());
            preparedStatement.setInt(4, hotelId); // Set the selected hotel ID
            return preparedStatement.executeUpdate() > 0;
        }
    }

    // READ ALL
    public List<Reservation> getAllReservations(int hotelId) throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT reservation_id, guest_name, room_number, contact_number FROM reservations WHERE hotel_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, hotelId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Reservation reservation = new Reservation(
                            resultSet.getInt("reservation_id"),
                            resultSet.getString("guest_name"),
                            resultSet.getInt("room_number"),
                            resultSet.getString("contact_number")
                    );
                    reservations.add(reservation);
                }
            }
        }
        return reservations;
    }

    // READ ONE
    public Reservation getReservationById(int reservationId) throws SQLException {
        String sql = "SELECT reservation_id, guest_name, room_number, contact_number FROM reservations WHERE reservation_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, reservationId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new Reservation(
                            resultSet.getInt("reservation_id"),
                            resultSet.getString("guest_name"),
                            resultSet.getInt("room_number"),
                            resultSet.getString("contact_number")
                    );
                }
            }
        }
        return null;
    }

    // READ ROOM NUMBER
    public Integer getRoomNumberByDetails(int reservationId, String guestName, int hotelId) throws SQLException {
        String sql = "SELECT room_number FROM reservations " +
                "WHERE reservation_id = ? AND guest_name = ? AND hotel_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, reservationId);
            preparedStatement.setString(2, guestName);
            preparedStatement.setInt(3, hotelId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("room_number");
                }
            }
        }
        return null;
    }

    // UPDATE
    public boolean updateReservation(Reservation reservation) throws SQLException {
        String sql = "UPDATE reservations SET guest_name = ?, room_number = ?, contact_number = ? WHERE reservation_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, reservation.getGuestName());
            preparedStatement.setInt(2, reservation.getRoomNumber());
            preparedStatement.setString(3, reservation.getContactNumber());
            preparedStatement.setInt(4, reservation.getId());
            return preparedStatement.executeUpdate() > 0;
        }
    }

    // DELETE
    public boolean deleteReservation(int reservationId) throws SQLException {
        String sql = "DELETE FROM reservations WHERE reservation_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, reservationId);
            return preparedStatement.executeUpdate() > 0;
        }
    }
}