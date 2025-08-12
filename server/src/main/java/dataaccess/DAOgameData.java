package dataaccess;
import chess.ChessGame;
import java.util.*;
import dataaccess.GameDaoInterface;
import model.GameData;
import model.JoinRequest;
import model.ListData;
import model.ListDataObject;

public class DAOgameData implements GameDaoInterface{
    private final Map<Integer, GameData> games = new HashMap<>();
    private int nextId = 1;
    @Override
    public String getGame(JoinRequest join){
        if (games.get(join.gameID()) == null){
            return null;
        }
        return "exists";
    }
    @Override
    public GameData createGame(GameData game) {
        //new id number just by adding one
        int gameId = nextId++;
        GameData newGame = new GameData(gameId, null, null, game.gameName(), new ChessGame());
        games.put(gameId, newGame);
        return newGame;
    }

    @Override
    public void updateGameState(int gameID, ChessGame game) throws DataAccessException {
        //comment
    }

    @Override
    public void clear() {
        games.clear();
        nextId = 1;
    }

    @Override
    public ChessGame getChessGame(Integer Id) throws DataAccessException {
        return null;
    }

    @Override
    public Collection<GameData> list() {
        return games.values();
    }
    @Override
    public ChessGame.TeamColor checkColor(int id, ChessGame.TeamColor color) {
        GameData gameData = games.get(id);
        switch(color){
            case ChessGame.TeamColor.WHITE:
                if (gameData.whiteUsername() == null){
                    return null;
                }
                break;
            case ChessGame.TeamColor.BLACK:
                if (gameData.blackUsername() == null){
                    return null;
                }
                 break;
        }
        return color;
    }
    @Override
    public void setWhiteUsername(int gameID, String username) throws DataAccessException{
        GameData game = games.get(gameID);
        if (game == null) {
            throw new DataAccessException("Game not found");
        }
        GameData updated = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
        games.put(gameID, updated);
    }
    @Override
    public void setBlackUsername(int gameID, String username)throws DataAccessException{
        GameData game = games.get(gameID);
        if (game == null) {
            throw new DataAccessException("Game not found");
        }
        GameData updated = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
        games.put(gameID, updated);
    }
    @Override
    public GameData checkGame(JoinRequest join) {
        return games.get(join.gameID());
    }
}
