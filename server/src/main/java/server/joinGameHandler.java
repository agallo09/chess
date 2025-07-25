package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
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
        try {
            // body
            String jsonBody = request.body();
            JoinRequest joinRequest = gson.fromJson(jsonBody, JoinRequest.class);
            // header
            String token = request.headers("authorization");
            // service method
            String joinResult = gameService.join(token, joinRequest);
            // only display gameID
            return joinResult;
        }catch(DataAccessException e) {
            String msg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";

            if (msg.contains("unauthorized")) {
                response.status(401);
            } else if (msg.contains("bad request")) {
                response.status(400);
            } else if (msg.contains("already taken")){
                response.status(403);
            }else {
                response.status(500);
            }
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }
}
