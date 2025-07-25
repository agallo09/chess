package dataaccess;
import chess.ChessGame;

import java.util.*;
import dataaccess.DataAccessException;
import model.GameData;
import model.JoinRequest;
import model.ListData;
import model.ListDataObject;

public class DAOgameData {
    private final Map<Integer, GameData> games = new HashMap<>();
    private int nextId = 1;
    public String getGame(JoinRequest join){
        if (games.get(join.gameID()) == null){
            return null;
        }
        return "exists";
    }

    public GameData createGame(GameData game) {
        //new id number just by adding one
        int gameId = nextId++;
        GameData newGame = new GameData(gameId, null, null, game.gameName(), new ChessGame());
        games.put(gameId, newGame);
        return newGame;
    }

    public void clear() {
        games.clear();
        nextId = 1;
    }

    public Collection<GameData> list() {
        return games.values();
    }

    public ChessGame.TeamColor checkColor(int ID, ChessGame.TeamColor color) {
        GameData gameData = games.get(ID);
        switch(color){
            case ChessGame.TeamColor.WHITE:
                if (gameData.whiteUsername() == null){
                    return null;
                }
            case ChessGame.TeamColor.BLACK:
                if (gameData.blackUsername() == null){
                    return null;
                }
        }
        return color;
    }

    public void setWhiteUsername(int gameID, String username)throws DataAccessException{
        GameData game = games.get(gameID);
        if (game == null) {
            throw new DataAccessException("Game not found");
        }
        GameData updated = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
        games.put(gameID, updated);
    }
    public void setBlackUsername(int gameID, String username)throws DataAccessException{
        GameData game = games.get(gameID);
        if (game == null) {
            throw new DataAccessException("Game not found");
        }
        GameData updated = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
        games.put(gameID, updated);
    }

    public GameData checkGame(JoinRequest join) {
        return games.get(join.gameID());
    }
}
