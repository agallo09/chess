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
        String sql = "INSERT INTO Users (username, password) VALUES (?, ?)";
        try(Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1,username);
            stmt.setString(2, hashedPassword);
            stmt.executeUpdate();
        }catch (SQLException e) {
            throw new DataAccessException("Unable to create user", e);
        }
    }

    @Override
    public Object getUser(UserData user) throws DataAccessException {
        
        try(Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1,username);
            stmt.setString(2, hashedPassword);
            stmt.executeUpdate();
        }catch (SQLException e) {
            throw new DataAccessException("Unable to create user", e);
        }
    }

    @Override
    public void clear() {

    }

    @Override
    public boolean validate(UserData user) {
        return false;
    }
}
