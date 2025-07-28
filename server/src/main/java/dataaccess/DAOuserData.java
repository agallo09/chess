package dataaccess;
import model.UserData;
import java.util.HashMap;
import java.util.Map;

public class DAOuserData implements UserDaoInterface {
    private final Map<String, UserData> users = new HashMap<>();

    //constructor
    public DAOuserData(){
        //nothing
    }


    // Create a new user
    @Override
    public void createUser(UserData user) throws DataAccessException {
        if (users.containsKey(user.username())) {
            throw new DataAccessException("User already exists");
        }
        users.put(user.username(), user);
    }
    // Clear all users (e.g. for testing or reset endpoint)
    @Override
    public void clear() {
        users.clear();
    }

    @Override
    public Object getUser(UserData user) {
        String username = user.username();
        UserData userInfo = users.get(username);
        if (userInfo == null) {
            return null;
        }
        return user;
        }

    @Override
    public boolean validate(UserData user) {
        UserData mapUser = users.get(user.username());
        if (mapUser == null){
            return false;
        }
        return mapUser.password().equals(user.password());
    }
}