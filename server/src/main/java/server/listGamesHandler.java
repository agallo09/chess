package server;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;
import java.util.Map;
import java.util.Map;
import java.util.ArrayList;

public class listGamesHandler implements Route {
    private final GameService gameService;
    public listGamesHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        Gson gson = new Gson();
        try {
            String jsonString = request.headers("authorization");
            AuthData user = new AuthData(jsonString, null);
            Arraylist<> games = gameService.list(user);
            response.status(200);
            return gson.toJson(games);
        }catch(DataAccessException e) {
            String msg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
            if (msg.contains("unauthorized")) {
                response.status(401);
            } else {
                response.status(500);
            }
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }
}
