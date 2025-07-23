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


public class loginHandler implements Route {
    private final UserService userService;

    public loginHandler(service.UserService userService){
        this.userService = userService;
    }
    @Override
    public Object handle(Request request, Response response) throws Exception {
        Gson gson = new Gson();
        try {
            String jsonString = request.body();
            UserData user = gson.fromJson(jsonString, UserData.class);
            AuthData token = userService.login(user);
            response.status(200);
            return gson.toJson(token);
        } catch (DataAccessException e) {
            String msg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";

            if (msg.contains("unauthorized")) {
                response.status(401);
            } else if (msg.contains("bad request")) {
                response.status(400);
            } else {
                response.status(500);
            }
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
        }

}
