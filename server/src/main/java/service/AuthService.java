package service;

import dataaccess.DAOauthToken;
import dataaccess.DataAccessException;
import model.AuthData;

public class AuthService {
    private final DAOauthToken tokenDAO;

    public AuthService(DAOauthToken token){

        this.tokenDAO =token;
    }

    public AuthData logout(AuthData user) throws DataAccessException {
        Object userData = tokenDAO.getAuth(user);
        if(userData == null){
            throw new DataAccessException("Error: unauthorized");
        }
        //convert object to AuthData object
        AuthData newAuthData = (AuthData) userData;
        // delete auth data
        String token = newAuthData.authToken();
        tokenDAO.deleteAuthInfo(token);
        return newAuthData;
    }

    public void clear() {
        tokenDAO.clear();
    }
}
