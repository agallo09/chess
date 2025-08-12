package service;
import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.JoinRequest;
import model.ListData;
import model.ListDataObject;
import java.util.ArrayList;
import java.util.List;



public class GameService {
    private AuthTokenDaoInterface tokenDAO;
    private GameDaoInterface gameDAO;
    private UserDaoInterface userDAO;
    public GameService(AuthTokenDaoInterface tokenDAO, GameDaoInterface gameDAO, UserDaoInterface userDAO) {

        this.tokenDAO = tokenDAO;
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;
    }

    public GameData create( AuthData jsonHeader, GameData gameData) throws DataAccessException{
        //check if authToken exists
        Object token = tokenDAO.getAuth(jsonHeader);
        if(token == null ){
            throw new DataAccessException("Error: unauthorized");
        }
        //bad request
        if (gameData.gameName() == null || gameData.gameName().isBlank()){
            throw new DataAccessException("Error: bad request");
        }

        //create and return game
        return gameDAO.createGame(gameData);
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
            return ("Joined successfully, white");
        } else {
            gameDAO.setBlackUsername(join.gameID(), username);
            return ("Joined successfully, black");

        }
    }

    public void clear() throws DataAccessException {
        gameDAO.clear();
    }

}
