package server;

import com.google.gson.Gson;
import model.AuthData;
import service.GameService;
import model.GameData;
import service.authService;
import spark.Request;
import spark.Response;
import spark.Route;

public class createGameHandler implements Route {
    private final authService authService;
    private final GameService gameService;
    public createGameHandler(authService authService, GameService gameService) {
        this.authService = authService;
        this.gameService = gameService;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        Gson gson = new Gson();
        // body
        String jsonBody = request.body();
        GameData gameID = gson.fromJson(jsonBody, GameData.class);
        // header
        String jsonHeader = request.headers("authorization");
        AuthData authData = gson.fromJson(jsonHeader, AuthData.class);
        // service method
        GameData createResult = gameService.create(authData, gameID);
        // only display gameID
        return gson.toJson(createResult.gameID());
    }
}
