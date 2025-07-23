package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import service.authService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

public class logoutHandler implements Route {
    private authService authService;
    public logoutHandler(authService authService){
        this.authService = authService;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        Gson gson = new Gson();
        try {
            String jsonString = request.headers("authorization");
            AuthData user = new AuthData(jsonString, null);
            AuthData logoutResult = authService.logout(user);
            return gson.toJson(logoutResult);
        }catch (DataAccessException e) {
            String msg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
            if (msg.contains("unauthorized")) {
                response.status(401);
            } else {
                response.status(500);
            }
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }
}
