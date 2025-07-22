package service;
import dataaccess.DAOauthToken;
import dataaccess.DAOgameData;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;

import java.util.Map;

public class GameService {
    private DAOauthToken tokenDAO;
    private DAOgameData gameDAO;
    public GameService(DAOauthToken tokenDAO, DAOgameData gameDAO) {

        this.tokenDAO = tokenDAO;
        this.gameDAO = gameDAO;
    }

    public GameData create(AuthData jsonHeader, GameData gameID) throws DataAccessException{
        //check if authToken exists
        Object userData = tokenDAO.getAuth(jsonHeader);
        if(userData == null){
            throw new DataAccessException("Unathorized");
        }
        //create and return game
        return gameDAO.createGame(gameID);
    }

    public Map<Integer, GameData> list(AuthData user) throws DataAccessException {
        //check if authToken exists
        Object userData = tokenDAO.getAuth(user);
        if(userData == null){
            throw new DataAccessException("Unathorized");
        }
        return gameDAO.list();
    }
}
