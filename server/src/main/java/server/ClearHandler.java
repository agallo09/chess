package server;

import com.google.gson.Gson;
import model.AuthData;
import service.GameService;
import service.UserService;
import service.authService;
import model.GameData;
import service.authService;
import spark.Request;
import spark.Response;
import spark.Route;

public class ClearHandler implements Route {
    private final UserService userService;
    private final GameService gameService;
    private final authService authService;

    public ClearHandler(UserService userService, GameService gameService, authService authService) {
        this.userService = userService;
        this.gameService = gameService;
        this.authService = authService;

    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        //going to user map
        userService.clear();
        gameService.clear();
        authService.clear();
        return ("{}");
    }
}
