
package service;

import dao.ReservationDAO;
import model.Reservation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.sql.SQLException;
import static org.mockito.Mockito.*;
        import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @Mock
    private ReservationDAO mockDao; // 1. Mock the dependency (DAO)

    @InjectMocks
    private ReservationService service; // 2. Inject the mock into the service being tested

    @Test
    void makeReservation_shouldSucceedForValidRoom() throws SQLException {
        // Arrange
        Reservation validRes = new Reservation("Test Guest", 105, "555-1234");
        // Mocking: Define expected DAO behavior
        when(mockDao.createReservation(validRes)).thenReturn(true);

        // Act
        boolean result = service.makeReservation(validRes);

        // Assert
        assertTrue(result, "Reservation should be successful for a valid room (105).");
        // Verify: Ensure the DAO was called
        verify(mockDao, times(1)).createReservation(validRes);
    }

    @Test
    void makeReservation_shouldFailForInvalidRoom() throws SQLException {
        // Arrange
        Reservation invalidRes = new Reservation("Jane Doe", 99, "555-5678"); // Room 99 is invalid by our service rule

        // Act
        boolean result = service.makeReservation(invalidRes);

        // Assert
        assertFalse(result, "Reservation should fail because room 99 is out of range.");
        // Verify: Ensure the DAO was NOT called because the Service layer blocked it
        verify(mockDao, never()).createReservation(any());
    }

    @Test
    void cancelReservation_shouldCallDaoDelete() throws SQLException {
        // Arrange
        int reservationId = 10;
        when(mockDao.deleteReservation(reservationId)).thenReturn(true);

        // Act
        boolean result = service.cancelReservation(reservationId);

        // Assert
        assertTrue(result, "Deletion should be successful.");
        verify(mockDao, times(1)).deleteReservation(reservationId);
    }
}
