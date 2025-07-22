package dataaccess;
import model.UserData;
import java.util.HashMap;
import java.util.Map;

public class DAOuserData {

    private final Map<String, UserData> users = new HashMap<>();

    //constructor
    public DAOuserData(){}
    //nothing much especific

    // Create a new user
    public void createUser(UserData user) throws DataAccessException {
        if (users.containsKey(user.username())) {
            throw new DataAccessException("User already exists");
        }
        users.put(user.username(), user);
    }

    // Find a user by username
    public UserData findUser(String username) throws DataAccessException {
        UserData user = users.get(username);
        if (user == null) {
            throw new DataAccessException("User not found");
        }
        return user;
    }

    // Clear all users (e.g. for testing or reset endpoint)
    public void clear() {
        users.clear();
    }

    // For debug/logging
    public int countUsers() {
        return users.size();
    }

}
