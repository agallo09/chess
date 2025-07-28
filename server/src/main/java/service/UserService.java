package service;
import dataaccess.DAOauthToken;
import dataaccess.UserDaoInterface;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;

public class UserService {

    private final UserDaoInterface userDAO;
    private final DAOauthToken tokenDAO;

    public UserService(UserDaoInterface userDAO, DAOauthToken tokenDAO){
        this.userDAO = userDAO;
        this.tokenDAO = tokenDAO;
    }

    public AuthData login(UserData user) throws DataAccessException {
        String username = user.username();
        String password = user.password();
        //check for a bad request
        if(user.username() == null || user.password() == null){
            throw new DataAccessException("Error: bad request");
        }
        boolean check = userDAO.validate(user);
        // Check password match
        if (!check) {
            throw new DataAccessException("Error: unauthorized");
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
        //check for a bad request
        if(user.username() == null || user.password() == null){
            throw new DataAccessException("Error: bad request");
        }
        //check if user exists
        Object userData = userDAO.getUser(user);
        if(userData != null){
            throw new DataAccessException("Error: already taken");
        }
        //create user
        userDAO.createUser(user);
        // Generate token
        String token = java.util.UUID.randomUUID().toString();
        //add AuthData
        AuthData tokens = new AuthData(token, user.username());
        tokenDAO.createAuth(tokens);
        //register result return
        return new AuthData(token, username);
    }

    public void clear() {
        userDAO.clear();
    }
}