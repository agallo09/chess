package dataaccess;
import model.UserData;
import java.util.HashMap;
import java.util.Map;

public class DAOuserData {

    private final Map<String, UserData> users = new HashMap<>();

    //constructor
    public DAOuserData(){
        //nothing
    }


    // Create a new user
    public void createUser(UserData user) throws DataAccessException {
        if (users.containsKey(user.username())) {
            throw new DataAccessException("User already exists");
        }
        users.put(user.username(), user);
    }
    // Clear all users (e.g. for testing or reset endpoint)
    public void clear() {
        users.clear();
    }

    // For debug/logging
    public int countUsers() {
        return users.size();
    }
    // Find a user by username

    public Object getUser(UserData user) {
        String username = user.username();
        UserData userInfo = users.get(username);
        if (userInfo == null) {
            return null;
        }
        return user;
        }
    }
