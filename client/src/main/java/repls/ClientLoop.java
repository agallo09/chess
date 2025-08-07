package repls;
import model.*;
import server.ServerFacade;
import java.util.Arrays;
import java.util.*;
import static repls.State.PRELOGIN;
import static repls.State.POSTLOGIN;

import ui.Board;



public class ClientLoop {
    String token = null;
    private ServerFacade server;
    private State status = PRELOGIN;
    private final Map<Integer,Integer> games = new HashMap<>();
    private Board board = new Board();



    public ClientLoop(String serverUrl) {
        this.server = new ServerFacade(serverUrl);
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "login" -> login(params);
                case "register" -> register(params);
                case "logout" -> logout(params);
                case "create" -> create(params);
                case "list" -> list(params);
                case "join" -> join(params);
                case "observe" -> observe(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private String create(String[] params) throws Exception {
        verifyPostLoginStatus();
        if(params.length != 1){
            throw new Exception("invalid input, try again.");
        }
        Integer id = server.createGame(params[0], token);
        return "Game created";
    }

    private String list(String[] params) throws Exception {
        verifyPostLoginStatus();
        StringBuilder output = new StringBuilder();
        if(params.length != 0){
            throw new Exception("invalid input, try again.");
        }
        Collection<GameData> games1 = server.listGames(token);
        if(games1.isEmpty()){
            return "No games created. Type 'help' to see commands";
        }
        // change not list ID
        int i = 1;
        for (GameData game : games1) {
            games.put(i, game.gameID());
            output.append(String.format(
                    "%d. Name: %s  , White: %s  , Black: %s%n",
                    i++,
                    game.gameName(),
                    game.whiteUsername() != null ? game.whiteUsername() : "none",
                    game.blackUsername() != null ? game.blackUsername() : "none"
            ));

        }
        return output.toString();
    }

    private String join(String[] params) throws Exception  {
        verifyPostLoginStatus();
        if(params.length != 2){
            throw new Exception("invalid input, try again.");
        }
        // check if the game exists first
        int gameNumber;
        try {
            gameNumber = Integer.parseInt(params[0]);
        } catch (NumberFormatException e) {
            throw new Exception("Invalid game number. Must be an integer.");
        }
        if (!games.containsKey(gameNumber)) {
            throw new Exception("Game not found. Please use 'list' to see available games.");
        }
        //join by color
        int gameID = games.get(gameNumber);
        String color = params[1].toLowerCase();
        if (color.equals("black")) {
            server.joinGame(gameID, "BLACK", token);
            board.drawBlack();

            return "Joined game as black.";
        } else if (color.equals("white")) {
            server.joinGame(gameID, "WHITE", token);
            board.drawWhite();
            return "Joined game as white.";
        } else {
            throw new Exception("Invalid color. Must be 'white' or 'black'.");
        }
    }

    private String observe(String[] params) throws Exception {
        verifyPostLoginStatus();
        if(params.length != 1){
            throw new Exception("invalid input, try again.");
        }
        int index = Integer.parseInt(params[0]);
        // change
        if (index < 0 || index > games.size()) {
            throw new Exception("No game with that ID.");
        }
        board.drawWhite();
        return "board";
    }

    private String logout(String[] params) throws Exception {
        verifyPostLoginStatus();
        if(params.length != 0){
            throw new Exception("invalid input, try again.");
        }
        server.logout(token);
        status = PRELOGIN;
        return "You logged out ";
    }

    private String login(String[] params) throws Exception{
        verifyPreLoginStatus();
        if(params.length != 2){
            throw new Exception("invalid input, try again.");
        }
        AuthData auth = server.login(params[0],params[1]);
        status = POSTLOGIN;
        this.token = auth.authToken();
        return "You logged in as " + auth.username();

    }

    private String register(String[] params) throws Exception{
        verifyPreLoginStatus();
        if(params.length != 3){
            throw new Exception("invalid input, try again.");
        }
        AuthData auth = server.register(params[0],params[1],params[2]);
        status = POSTLOGIN;
        this.token = auth.authToken();
        return "You registered as " + auth.username() + " , u are logged in now.";

    }


    public String help() {
        if (status == PRELOGIN) {
            return """
                    register <USERNAME> <PASSWORD> <EMAIL> - to create an account"
                    login <USERNAME> <PASSWORD> - to play chess
                    quit - playing chess
                    help - with possible commands
                    """;
        }
        if (status == POSTLOGIN) {
            return """
                    create <NAME> - a game
                    list - games
                    join <ID> [WHITE|BLACK] - a game
                    observe <ID> - a game
                    logout - when you are done
                    quit - playing chess
                    help - with possible commands
                    """;
        }
        else{
            return """
                    quit - playing chess
                    help - see possible commands
                    """;
        }
    }
    // status verification for the 3 levels,
    // first we verify they have logged in or register

    private void verifyPreLoginStatus() throws Exception {
        if (status == POSTLOGIN){
            throw new Exception("Wrong action, you are already signed in.");
        }
    }

    private void verifyPostLoginStatus() throws Exception {
        if (status == PRELOGIN){
            throw new Exception("Wrong action, you have to register or log in first.");
        }
    }

    public State getstate() {
        return status;
    }
}
