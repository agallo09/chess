package server;
import com.google.gson.Gson;
import model.AuthData;
import model.UserData;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

public class listGamesHandler implements Route {
    private final GameService gameService;
    public listGamesHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        Gson gson = new Gson();
        String jsonString = request.body();
        AuthData token = gson.fromJson(jsonString, AuthData.class);
        AuthData tokens = gameService.get(token);
        return gson.toJson(tokens);
    }
}
