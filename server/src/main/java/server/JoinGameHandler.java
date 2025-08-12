package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.JoinRequest;
import service.GameService;
import service.AuthService;
import spark.Request;
import spark.Response;
import spark.Route;
import websocket.ServerWebSocketFacade;

import java.util.Map;

public class JoinGameHandler implements Route {
    private final AuthService authService;
    private final GameService gameService;
    private ServerWebSocketFacade serverSocket;
    public JoinGameHandler(AuthService authService, GameService gameService, ServerWebSocketFacade serverSocket) {
        this.authService = authService;
        this.gameService = gameService;
        this.serverSocket = serverSocket;
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
            System.out.println(joinResult);
            // only display gameID
            response.status(200);
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
