package server;
import com.google.gson.Gson;
import model.UserData;
import model.AuthData;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;
import dataaccess.DataAccessException;
import java.util.Map;
import server.ResponseUtil;


public class LoginHandler implements Route {
    private final UserService userService;

    public LoginHandler(service.UserService userService){
        this.userService = userService;
    }
    @Override
    public Object handle(Request request, Response response) {
        Gson gson = new Gson();
        try {
            String jsonString = request.body();
            UserData user = gson.fromJson(jsonString, UserData.class);
            AuthData token = userService.login(user);
            response.status(200);
            return gson.toJson(token);
        } catch (DataAccessException e) {
            return ResponseUtil.handleException(response, e);
        } catch (Exception e) {
            response.status(500);
            return gson.toJson(Map.of("message", "Error: internal server error"));
        }
    }

}
