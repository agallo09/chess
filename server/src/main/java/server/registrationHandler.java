package server;

import com.google.gson.Gson;
import model.AuthData;
import model.UserData;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class registrationHandler implements Route {
    private final UserService userService;
    public registrationHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        Gson gson = new Gson();
        String jsonString = request.body();
        UserData user = gson.fromJson(jsonString, UserData.class);
        AuthData token = userService.register(user);
        return gson.toJson(token);
    }
}
