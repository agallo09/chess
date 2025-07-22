package service;
import dataaccess.DAOauthToken;
import dataaccess.DAOgameData;
import dataaccess.DAOuserData;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.JoinRequest;


import java.util.Map;

public class GameService {
    private DAOauthToken tokenDAO;
    private DAOgameData gameDAO;
    private DAOuserData userDAO;
    public GameService(DAOauthToken tokenDAO, DAOgameData gameDAO, DAOuserData userDAO) {

        this.tokenDAO = tokenDAO;
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;
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

    public String join(AuthData authData, JoinRequest join) throws DataAccessException {
        //check if authToken exists
        Object userData = tokenDAO.getAuth(authData);
        if(userData == null){
            throw new DataAccessException("Unathorized");
        }
        // check if game exists
        GameData gameData =  gameDAO.getGame(join.gameID());
        if(gameData == null){
            throw new DataAccessException("Bad Request");
        }
        // get the username
        String username = userDAO.getUsername(authData.authToken());

        // check if color is already taken
        String gameDataColor =  gameDAO.checkColor(join.gameID(), join.playerColor());
        if(gameDataColor != null){
            throw new DataAccessException("Already Taken");
        }
        //update game
        gameDAO.updateGame(join, username);
        return ("{}");
    }
}
