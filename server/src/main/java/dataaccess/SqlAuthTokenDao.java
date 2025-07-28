package dataaccess;
import java.sql.*;
import model.AuthData;
public class SqlAuthTokenDao implements AuthTokenDaoInterface {

    @Override
    public void createAuth(AuthData authdata) throws  DataAccessException{
        String username = authdata.username();
        String token = authdata.authToken();
        String sql = "INSERT INTO AuthTokens (token, username) VALUES (?, ?)";
        try(Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1,token);
            stmt.setString(2, username);
            stmt.executeUpdate();
        }catch (SQLException e) {
            throw new DataAccessException("Unable to create user", e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void deleteAuthInfo(String token) throws DataAccessException {
        String sql = "DELETE FROM AuthTokens WHERE token = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            stmt.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object getAuth(AuthData token) throws DataAccessException {
        String sql = "SELECT * FROM AuthTokens WHERE token = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token.authToken());
            ResultSet values = stmt.executeQuery();
            if (values.next()) {
                String username = values.getString("username");
                String Token = values.getString("token");
                return new AuthData(username,Token);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to get user", e);
        }    }

    @Override
    public void clear() {
        String sql = "DELETE FROM AuthTokens";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getUsername(String token) throws DataAccessException {
        String sql = "SELECT * FROM AuthTokens WHERE token = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            ResultSet values = stmt.executeQuery();
            if (values.next()) {
                String username = values.getString("username");
                String Token = values.getString("token");
                return username;
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to get user", e);
        }
    }
}
