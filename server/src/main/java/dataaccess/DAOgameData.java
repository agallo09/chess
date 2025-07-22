package dataaccess;
import java.util.HashMap;
import java.util.Map;
import model.GameData;

public class DAOgameData {
    private final Map<Integer, GameData> games = new HashMap<>();
    private int nextId = 1;

    public GameData createGame(GameData game) {
        //new id number just by adding one
        int gameId = nextId++;
        int gameID = game.gameID();
        games.put(gameID,game);
        return game;
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

    public String checkColor(GameData game) {
        GameData gameData = games.get(game.gameID());
        if (game.!= null)
    }
}
