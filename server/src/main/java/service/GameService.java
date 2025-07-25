package service;
import chess.ChessGame;
import dataaccess.DAOauthToken;
import dataaccess.DAOgameData;
import dataaccess.DAOuserData;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.JoinRequest;
import spark.Request;
import java.util.ArrayList;
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

    public GameData create( AuthData jsonHeader, GameData gameID) throws DataAccessException{
        //check if authToken exists
        Object token = tokenDAO.getAuth(jsonHeader);
        if(token == null ){
            throw new DataAccessException("Error: unauthorized");
        }
        //bad request
        if (gameID.gameName() == null || gameID.gameName().isBlank()){
            throw new DataAccessException("Error: bad request");
        }

        //create and return game
        return gameDAO.createGame(gameID);
    }

    public ArrayList list(AuthData user) throws DataAccessException {
        //check if authToken exists
        Object userData = tokenDAO.getAuth(user);
        if(userData == null){
            throw new DataAccessException("Error: unauthorized");
        }
        return gameDAO.list();
    }

    public String join(AuthData authData, JoinRequest join) throws DataAccessException {
        //check if authToken exists
        Object token = tokenDAO.getAuth(authData);
        if(token == null ){
            throw new DataAccessException("Error: unauthorized");
        }

        // bad request 1
        ChessGame.TeamColor playerColor = join.playerColor();
        if(playerColor != ChessGame.TeamColor.WHITE && playerColor != ChessGame.TeamColor.WHITE ){
            throw new DataAccessException("Error: bad request");
        }
        // bad request 2
        if( gameDAO.checkGame(join) == null ){
            throw new DataAccessException("Error: bad request");
        }

        // get the username
        String username = tokenDAO.getUsername(authData.authToken());

        // check if color is already taken
        ChessGame.TeamColor gameDataColor =  gameDAO.checkColor(join.gameID(), join.playerColor());
        if(join.playerColor() != null && gameDataColor != null){
            throw new DataAccessException("Error: already taken");
        }
        //update game
        gameDAO.updateGame(join, username);
        return ("{}");
    }

    public void clear() {
        gameDAO.clear();
    }
}
