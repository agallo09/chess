package service;
import dataaccess.DAOauthToken;
import model.AuthData;

public class GameService {
    private DAOauthToken tokenDAO;
    public GameService(DAOauthToken tokenDAO) {
        this.tokenDAO = tokenDAO;
    }

    public AuthData get(AuthData token) {
        AuthData checkedToken = tokenDAO.find(token);

    }
}
