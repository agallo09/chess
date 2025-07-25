package server;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.AuthData;
import model.ListData;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;
import server.ResponseUtil;
import java.util.Map;


public class ListGamesHandler implements Route {
    private final GameService gameService;
    public ListGamesHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        Gson gson = new Gson();
        try {
            String jsonString = request.headers("authorization");
            AuthData user = new AuthData(jsonString, null);
            ListData games = gameService.list(user);
            response.status(200);
            return gson.toJson(games);
        }catch (DataAccessException e) {
            return ResponseUtil.handleException(response, e);
        }
    }
}
