package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.AuthData;
import service.authService;
import spark.Request;
import spark.Response;
import spark.Route;
import server.ResponseUtil;
import java.util.Map;

public class LogoutHandler implements Route {
    private authService authService;
    public LogoutHandler(authService authService){
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
            return ResponseUtil.handleException(response, e);
        }
    }
}
