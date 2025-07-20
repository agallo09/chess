package server;
import dataaccess.*;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", new registrationHandler());
        Spark.post("/session", new loginHandler());
        Spark.delete("/session", new logoutHandler());
        Spark.get("/game", new listGamesHandler());
        Spark.post("/game", new createGameHandler());
        Spark.put("/game", new joinGameHandler());


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
