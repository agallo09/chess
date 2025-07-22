package server;
import com.google.gson.Gson;
import model.UserData;
import model.AuthData;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;


public class loginHandler implements Route {
    private final UserService userService;

    public loginHandler(service.UserService userService){
        this.userService = userService;
    }
    @Override
    public Object handle(Request request, Response response) throws Exception {
        Gson gson = new Gson();
        String jsonString = request.body();
        UserData user = gson.fromJson(jsonString, UserData.class);
        AuthData token = userService.login(user);
        return gson.toJson(token);
        }
}
