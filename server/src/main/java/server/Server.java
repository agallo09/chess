package server;
import service.*;
import dataaccess.*;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        //Initialize objects for the DAOs and services,
        // so I do not have to create them inside classes

        SqlGameDao gameDAO = new SqlGameDao();
        SqlAuthTokenDao tokenDAO = new SqlAuthTokenDao();
        SqlUserDao userDAO = new SqlUserDao();
        UserService userService = new UserService(userDAO, tokenDAO);
        GameService gameService = new GameService(tokenDAO, gameDAO, userDAO);
        AuthService authService = new AuthService(tokenDAO);


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
