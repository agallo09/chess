package server;

import service.*;
import dataaccess.*;
import spark.*;
import websocket.ServerWebSocketFacade;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        // Create and configure the database before DAOs are created
        try {
            // Create database if not exists
            DatabaseManager.createDatabase();
            // Create tables if not exists
            DatabaseManager.createTables();
        } catch (DataAccessException e) {
            throw new RuntimeException("Database setup failed: " + e.getMessage(), e);
        }

        // Initialize DAO objects after database setup
        SqlGameDao gameDAO = new SqlGameDao();
        SqlAuthTokenDao tokenDAO = new SqlAuthTokenDao();
        SqlUserDao userDAO = new SqlUserDao();

        // Initialize service objects
        UserService userService = new UserService(userDAO, tokenDAO);
        GameService gameService = new GameService(tokenDAO, gameDAO, userDAO);
        AuthService authService = new AuthService(tokenDAO);
        ServerWebSocketFacade websocketHandler = new ServerWebSocketFacade(gameService, userService);

        // Register endpoints with appropriate handlers
        Spark.post("/user", new RegistrationHandler(userService));
        Spark.post("/session", new LoginHandler(userService));
        Spark.delete("/session", new LogoutHandler(authService));
        Spark.get("/game", new ListGamesHandler(gameService));
        Spark.post("/game", new CreateGameHandler(authService, gameService));
        Spark.put("/game", new JoinGameHandler(authService, gameService));
        Spark.delete("/db", new ClearHandler(userService, gameService, authService));
        Spark.webSocket("/ws", websocketHandler);


        // Initialize the server and wait for it to start
        Spark.init();
        Spark.awaitInitialization();

        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
