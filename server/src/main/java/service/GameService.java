package service;
import chess.ChessGame;
import dataaccess.DAOauthToken;
import dataaccess.GameDaoInterface;
import dataaccess.UserDaoInterface;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.JoinRequest;
import model.ListData;
import model.ListDataObject;
import java.util.ArrayList;
import java.util.List;



public class GameService {
    private DAOauthToken tokenDAO;
    private GameDaoInterface gameDAO;
    private UserDaoInterface userDAO;
    public GameService(DAOauthToken tokenDAO, GameDaoInterface gameDAO, UserDaoInterface userDAO) {

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

    public ListData list(AuthData user) throws DataAccessException {
        //check if authToken exists
        Object userData = tokenDAO.getAuth(user);
        if(userData == null){
            throw new DataAccessException("Error: unauthorized");
        }

        List<ListDataObject> gamesList = new ArrayList<>();

        List<GameData> games = new ArrayList<>(gameDAO.list());
        for (GameData game : games) {
            gamesList.add(new ListDataObject (game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName()));
        }
        return new ListData(gamesList);
    }

    public String join(String token, JoinRequest join) throws DataAccessException {
        //check if authToken exists
        if(token == null ){
            throw new DataAccessException("Error: unauthorized");
        }

        // bad request 1
        ChessGame.TeamColor playerColor = join.playerColor();
        if(playerColor != ChessGame.TeamColor.WHITE && playerColor != ChessGame.TeamColor.BLACK ){
            throw new DataAccessException("Error: bad request");
        }
        // bad request 2
        if( gameDAO.checkGame(join) == null ){
            throw new DataAccessException("Error: bad request");
        }
        // bad request 3
        if(gameDAO.getGame(join) == null ){
            throw new DataAccessException("Error: bad request");
        }

        // check if color is already taken
        ChessGame.TeamColor gameDataColor =  gameDAO.checkColor(join.gameID(), join.playerColor());
        if(gameDataColor != null){
            throw new DataAccessException("Error: already taken");
        }

        // get the username
        String username = tokenDAO.getUsername(token);
        //update game
        if (playerColor == ChessGame.TeamColor.WHITE) {
            gameDAO.setWhiteUsername(join.gameID(), username);
        } else {
            gameDAO.setBlackUsername(join.gameID(), username);
        }
        return ("{}");
    }

    public void clear() {
        gameDAO.clear();
    }
}
