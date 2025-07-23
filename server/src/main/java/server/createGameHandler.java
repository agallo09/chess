package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.AuthData;
import service.GameService;
import model.GameData;
import service.authService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

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
        try {
            // body
            String jsonBody = request.body();
            GameData gameID = gson.fromJson(jsonBody, GameData.class);
            // header
            String jsonHeader = request.headers("authorization");
            AuthData authData = new AuthData(jsonHeader, null);
            // service method
            GameData createResult = gameService.create(authData, gameID);
            // only display gameID
            return gson.toJson(createResult);
        }catch(DataAccessException e) {
            String msg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";

            if (msg.contains("unauthorized")) {
                response.status(401);
            } else if (msg.contains("bad request")) {
                response.status(400);
            } else {
                response.status(500);
            }
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }
}
