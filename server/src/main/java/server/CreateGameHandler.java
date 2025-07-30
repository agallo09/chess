package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.AuthData;
import service.GameService;
import model.GameData;
import service.AuthService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

public class CreateGameHandler implements Route {
    private final AuthService authService;
    private final GameService gameService;
    public CreateGameHandler(AuthService authService, GameService gameService) {
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
        } catch (DataAccessException e) {
        return ResponseUtil.handleException(response, e);
        }catch (Exception e) {
            // This catches anything unexpected (e.g., misconfigured DB)
            response.status(500);
            return gson.toJson(Map.of("message", "Error: internal server error"));
        }
    }
}
