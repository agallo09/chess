package dataaccess;
import java.sql.*;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

public class SqlUserDao implements UserDaoInterface{

    public SqlUserDao() {
        // nothing in constructor
    }
    @Override
    public void createUser(UserData user) throws DataAccessException {
        String username = user.username();
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        String sql = "INSERT INTO Users (username, password, email) VALUES (?, ?, ?)";
        try(Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1,username);
            stmt.setString(2, hashedPassword);
            stmt.setString(3, user.email());
            stmt.executeUpdate();
        }catch (SQLException e) {
            throw new DataAccessException("Unable to create user", e);
        }
    }

    @Override
    public Object getUser(UserData user) throws DataAccessException {
        String sql = "SELECT * FROM Users WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.username());
            ResultSet values = stmt.executeQuery();
            if (values.next()) {
                String username = values.getString("username");
                String password = values.getString("password");
                String email = values.getString("email");
                return new UserData(username, password, email);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to get user", e);
        }
    }

    @Override
    public void clear() {
        String sql = "DELETE FROM Users";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean validate(UserData user) throws DataAccessException {
        String sql = "SELECT password FROM Users WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.username());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String storedHash = rs.getString("password");
                return BCrypt.checkpw(user.password(), storedHash);
            } else {
                return false; // user not found
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error validating user", e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
