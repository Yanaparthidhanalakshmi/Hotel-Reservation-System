package dao;
import model.Hotel;
import java.sql.*;
        import java.util.ArrayList;
import java.util.List;

public class HotelDAO {
    private final Connection connection;

    public HotelDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Hotel> getAllHotels() throws SQLException {
        List<Hotel> hotels = new ArrayList<>();
        String sql = "SELECT hotel_id, name, city, rating, address FROM hotels ORDER BY rating DESC";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                Hotel hotel = new Hotel(
                        resultSet.getInt("hotel_id"),
                        resultSet.getString("name"),
                        resultSet.getString("city"),
                        resultSet.getDouble("rating"),
                        resultSet.getString("address")
                );
                hotels.add(hotel);
            }
        }
        return hotels;
    }

    public Hotel getHotelById(int hotelId) throws SQLException {
        String sql = "SELECT hotel_id, name, city, rating, address FROM hotels WHERE hotel_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, hotelId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new Hotel(
                            resultSet.getInt("hotel_id"),
                            resultSet.getString("name"),
                            resultSet.getString("city"),
                            resultSet.getDouble("rating"),
                            resultSet.getString("address")
                    );
                }
            }
        }
        return null;
    }
}
