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

        UserData userdata = null;
        try {
            userdata = userDAO.findUser(username);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        if (userdata == null) {
            throw new DataAccessException("Error: user doesnt exist");
        }

        // Check password match
        if (!userdata.password().equals(password)) {
            throw new DataAccessException("Error: password doesnt match username");
        }

        // Generate token (dummy example, you'd use something more secure in production)
        String token = java.util.UUID.randomUUID().toString();
        //add info to authDAO map
        AuthData tokens = new AuthData(token, user.username());
        tokenDAO.createAuth(tokens);
        return new AuthData(token, username);
    }

    public AuthData register(UserData user) throws DataAccessException {
        String username = user.username();
        UserData userdata = null;

        //check and add the user object to the map
        userDAO.createUser(user);
        // Generate token (dummy example, you'd use something more secure in production)
        String token = java.util.UUID.randomUUID().toString();
        //add info to authDAO map
        AuthData tokens = new AuthData(token, user.username());
        tokenDAO.createAuth(tokens);
        return new AuthData(token, username);
    }
}