package service;

import dao.UserDAO;
import model.User;
import java.sql.SQLException;

public class AuthService {
    private final UserDAO userDAO;

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Authenticates a user.
     * In a production environment, this should use BCrypt to securely verify the password.
     */
    public boolean login(String username, String password) throws SQLException {
        User user = userDAO.getUserByUsername(username);

        if (user != null) {
            // Placeholder Security Check:
            // This is UNSAFE. It checks if the entered password matches the known plaintext
            // AND the stored placeholder hash. This needs to be replaced with BCrypt.
            if (username.equals("admin") && password.equals("Admin@123") && user.getPasswordHash().equals("Admin@123_HASHED")) {
                return true;
            }
        }
        return false;
    }
}
