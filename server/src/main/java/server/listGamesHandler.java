package server;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;
import java.util.Map;
import java.util.Map;

public class listGamesHandler implements Route {
    private final GameService gameService;
    public listGamesHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        Gson gson = new Gson();
        String jsonString = request.headers("authorization");
        AuthData user = gson.fromJson(jsonString, AuthData.class);
        Map<Integer, GameData> games = gameService.list(user);
        return gson.toJson(games);
    }
}
