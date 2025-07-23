package dataaccess;
import java.util.HashMap;
import java.util.Map;
import model.GameData;
import model.JoinRequest;

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

    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    public void clear() {
        games.clear();
        nextId = 1;
    }

    public Map<Integer, GameData> list() {
        return games;
    }

    public String checkColor(int ID, String color) {
        GameData gameData = games.get(ID);
        switch(color){
            case "WHITE":
                if (gameData.whiteUsername() == null){
                    return null;
                }
            case "BLACK":
                if (gameData.blackUsername() == null){
                    return null;
                }
        }
        return color;
    }

    public void updateGame(JoinRequest join, String username) {
        GameData gameData = games.get(join.gameID());
        switch(join.playerColor()){
            case "WHITE":
                games.put(join.gameID(), new GameData(join.gameID(), username, gameData.blackUsername(), gameData.gameName(), gameData.game()));
            case "BLACK":
                games.put(join.gameID(), new GameData(join.gameID(), gameData.whiteUsername(), username, gameData.gameName(), gameData.game()));
                }
        }
    }
