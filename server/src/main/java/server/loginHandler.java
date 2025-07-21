package server;
import com.google.gson.Gson;
import model.UserData;
import model.AuthData;
import spark.Request;
import spark.Response;
import spark.Route;


public class loginHandler implements Route {
    public loginHandler(){
    }
    @Override
    public Object handle(Request request, Response response) throws Exception {
        Gson gson = new Gson();
        String jsonString = request.body();
        UserData user = gson.fromJson(jsonString, UserData.class);
        server.UserService userservice = new server.UserService(userDAO);
        AuthData token = userservice.login(user);
        String jsonReturnString = gson.toJson(token);
        return jsonReturnString;
    }
}
