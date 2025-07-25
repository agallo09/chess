package server;
import service.*;
import dataaccess.*;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        //Initialize objects for the DAOs and services so i dont have to create them inside classes

        DAOgameData gameDAO = new DAOgameData();
        DAOauthToken tokenDAO = new DAOauthToken();
        DAOuserData userDAO = new DAOuserData();
        UserService userService = new UserService(userDAO, tokenDAO);
        GameService gameService = new GameService(tokenDAO, gameDAO, userDAO);
        authService authService = new authService(tokenDAO);


        // Register your endpoints and handle exceptions here.
        Spark.post("/user", new RegistrationHandler(userService));
        Spark.post("/session", new LoginHandler(userService));
        Spark.delete("/session", new LogoutHandler(authService));
        Spark.get("/game", new ListGamesHandler(gameService));
        Spark.post("/game", new CreateGameHandler(authService, gameService));
        Spark.put("/game", new JoinGameHandler(authService, gameService));
        Spark.delete("/db", new ClearHandler(userService, gameService, authService));



        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
