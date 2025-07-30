package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

public class RegistrationHandler implements Route {
    private final UserService userService;
    public RegistrationHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        Gson gson = new Gson();
        try {
            String jsonString = request.body();
            UserData user = gson.fromJson(jsonString, UserData.class);
            AuthData token = userService.register(user);
            return gson.toJson(token);
        }catch(DataAccessException e){
            String msg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";

            if (msg.contains("already taken")) {
                response.status(403);
            } else if (msg.contains("bad request")) {
                response.status(400);
            } else {
                response.status(500);
            }
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        } catch (Exception e) {
            // This catches anything unexpected (e.g., misconfigured DB)
            response.status(500);
            return gson.toJson(Map.of("message", "Error: internal server error"));
        }
    }
}
