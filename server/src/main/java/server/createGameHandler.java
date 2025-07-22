package server;

import service.GameService;
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
        return null;
    }
}
