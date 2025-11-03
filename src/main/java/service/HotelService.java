package service;

import dao.HotelDAO;
import model.Hotel;
import java.sql.SQLException;
import java.util.List;

public class HotelService {
    private final HotelDAO hotelDAO;

    public HotelService(HotelDAO hotelDAO) {
        this.hotelDAO = hotelDAO;
    }

    public List<Hotel> getAvailableHotels() throws SQLException {
        return hotelDAO.getAllHotels();
    }

    public Hotel findHotelById(int hotelId) throws SQLException {
        return hotelDAO.getHotelById(hotelId);
    }
}
