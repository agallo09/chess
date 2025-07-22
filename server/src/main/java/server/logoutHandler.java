package server;

import com.google.gson.Gson;
import model.AuthData;
import model.UserData;
import service.authService;
import spark.Request;
import spark.Response;
import spark.Route;

public class logoutHandler implements Route {
    private authService authService;
    public logoutHandler(authService authService){
        this.authService = authService;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        Gson gson = new Gson();
        String jsonString = request.headers("authorization");
        AuthData user = gson.fromJson(jsonString, AuthData.class);
        String logoutResult = authService.logout(user);
        return gson.toJson(logoutResult);
    }
}
