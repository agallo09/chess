package dataaccess;
import chess.ChessGame;

import java.util.*;

import model.GameData;
import model.JoinRequest;
import model.ListData;
import model.ListDataObject;

public class DAOgameData {
    private final Map<Integer, GameData> games = new HashMap<>();
    private int nextId = 1;

    public GameData createGame(GameData game) {
        //new id number just by adding one
        int gameId = nextId++;
        GameData newGame = new GameData(gameId, null, null, game.gameName(), null);
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
                break;
            case ChessGame.TeamColor.BLACK:
                if (gameData.blackUsername() == null){
                    return null;
                }
                break;
        }
        return color;
    }

    public void updateGame(JoinRequest join, String username) {
        GameData gameData = games.get(join.gameID());
        switch(join.playerColor()){
            case ChessGame.TeamColor.WHITE:
                games.put(join.gameID(), new GameData(join.gameID(), username, gameData.blackUsername(), gameData.gameName(), gameData.game()));
            case ChessGame.TeamColor.BLACK:
                games.put(join.gameID(), new GameData(join.gameID(), gameData.whiteUsername(), username, gameData.gameName(), gameData.game()));
                }
        }

    public GameData checkGame(JoinRequest join) {
        return games.get(join.gameID());
    }
}
