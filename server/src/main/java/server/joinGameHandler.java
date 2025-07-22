package server;

import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.JoinRequest;
import service.GameService;
import service.authService;
import spark.Request;
import spark.Response;
import spark.Route;
import java.util.Map;

public class joinGameHandler implements Route {
    private final authService authService;
    private final GameService gameService;
    public joinGameHandler(authService authService, GameService gameService) {
        this.authService = authService;
        this.gameService = gameService;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        Gson gson = new Gson();
        // body
        String jsonBody = request.body();
        JoinRequest joinRequest = gson.fromJson(jsonBody, JoinRequest.class);
        // header
        String jsonHeader = request.headers("authorization");
        AuthData authData = gson.fromJson(jsonHeader, AuthData.class);
        // service method
        String joinResult = gameService.join(authData, joinRequest);
        // only display gameID
        return joinResult;
    }
}
