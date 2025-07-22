package service;
import dataaccess.DAOauthToken;
import dataaccess.DAOuserData;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;

public class UserService {

    private final DAOuserData userDAO;
    private final DAOauthToken tokenDAO;

    public UserService(DAOuserData userDAO, DAOauthToken tokenDAO){
        this.userDAO = userDAO;
        this.tokenDAO = tokenDAO;
    }

    public AuthData login(UserData user) throws DataAccessException {
        String username = user.username();
        String password = user.password();

        //check if user exists
        Object userData = userDAO.getUser(user);
        if(userData == null){
            throw new DataAccessException("Username does not exists");
        }
        //convert object to UserData object
        UserData newUserData = (UserData) userData;
        // Check password match
        if (!newUserData.password().equals(password)) {
            throw new DataAccessException("Error: password does not match username");
        }
        //after checking create auth token
        String token = java.util.UUID.randomUUID().toString();
        //add info to authDAO map
        AuthData tokens = new AuthData(token, user.username());
        tokenDAO.createAuth(tokens);
        //Returning log in result
        return new AuthData(token, username);
    }

    public AuthData register(UserData user) throws DataAccessException {
        String username = user.username();
        UserData userdata = null;
        //check if user exists
        Object userData = userDAO.getUser(user);
        if(userData != null){
            throw new DataAccessException("Username already exists");
        }
        //create user
        userDAO.createUser(user);
        // Generate token (dummy example, you'd use something more secure in production)
        String token = java.util.UUID.randomUUID().toString();
        //add AuthData
        AuthData tokens = new AuthData(token, user.username());
        tokenDAO.createAuth(tokens);
        //register result return
        return new AuthData(token, username);
    }
}