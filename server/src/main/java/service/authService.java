package service;

import dataaccess.DAOauthToken;
import model.AuthData;
import model.UserData;

public class authService {
    private final DAOauthToken tokenDAO;

    public authService(DAOauthToken token){

        this.tokenDAO =token;
    }

    public AuthData logout(AuthData user) {
        String token = user.authToken();

        //check the
        tokenDAO.deleteAuthInfo(token);
        return null;
    }
}
