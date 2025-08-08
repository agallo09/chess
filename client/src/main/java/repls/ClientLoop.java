package repls;
import model.*;
import server.ServerFacade;

import java.util.Arrays;
import java.util.*;
import static repls.State.PRELOGIN;
import static repls.State.POSTLOGIN;
import static repls.State.GAMESTATUS;
import static repls.State.OBSERVER;


import ui.Board;
import websocket.WebSocketFacade;


public class ClientLoop {
    String token = null;
    private final ServerFacade server;
    String serverUrl;
    private State status = PRELOGIN;
    private final Map<Integer,Integer> games = new HashMap<>();
    private final Board board = new Board();
    // websocket facade object
    private WebSocketFacade wsManager;
    private websocket.NotificationHandler notificationHandler;


    public ClientLoop(String serverUrl) {
        this.server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }
    // evaluating the input
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
                case "move" -> makeMove(params);
                case "redraw" -> redrawBoard(params);
                case "legal" -> legalMoves(params);
                case "resign" -> resign(params);
                case "leave" -> leave(params);
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private String leave(String[] params) throws Exception{
        verifyGameStatus();
        if (params.length != 0) {
            throw new Exception("Usage: move <source> <destination> [promotion]");
        }
        //websocket call method
        wsManager.leave(token);
        return "You just resigned.";
    }

    private String resign(String[] params) throws Exception{
        verifyGameStatus();
        if (params.length != 0) {
            throw new Exception("Usage: move <source> <destination> [promotion]");
        }
        //websocket call method
        wsManager.resign(token);
        return "You just resigned.";
    }

    private String legalMoves(String[] params) throws Exception {
        verifyGameStatus();
        if (params.length != 1) {
            throw new Exception("Usage: move <source> <destination> [promotion]");
        }
        //fielding
        String source = params[0].toLowerCase();
        // check source
        if (!source.matches("^[a-h][1-8]$")) {
            throw new Exception("Invalid source square: must be from a1 to h8.");
        }
        //websocket method call
        wsManager.legalMoves(token, source);
        return "You will see the legal moves on the board for piece on: " + source;


    }

    private String redrawBoard(String[] params) throws Exception{
        verifyGameStatus();
        if (params.length != 0) {
            throw new Exception("Usage: move <source> <destination> [promotion]");
        }
        //websocket call method
        wsManager.redrawBoard(token);
        return "Board redraw.";
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
        //verify status
        verifyPostLoginStatus();
        // check inout valid
        if(params.length != 2){
            throw new Exception("invalid input, try again.");
        }
        // check if the game exists first
        int gameNumber;
        //check if input is a number
        try {
            gameNumber = Integer.parseInt(params[0]);
        } catch (NumberFormatException e) {
            throw new Exception("Invalid game number. Must be an integer.");
        }
        //check if game exists
        if (!games.containsKey(gameNumber)) {
            throw new Exception("Game not found. Please use 'list' to see available games.");
        }
        int gameID = games.get(gameNumber);
        //check valid color
        String color = params[1].toLowerCase();
        if (!color.equals("black") && !color.equals("white")) {
            throw new Exception("Invalid color. Must be 'white' or 'black'.");
        }
        // Use WebSocket to join the game
        wsManager = new WebSocketFacade(serverUrl, notificationHandler);
        wsManager.joinGame(params[0], params[1], token);
        status = State.GAMESTATUS;
        return "Joined game as " + color + ". Waiting for game state...";
    }

    private String observe(String[] params) throws Exception {
        verifyPostLoginStatus();
        if(params.length != 1){
            throw new Exception("invalid input, try again.");
        }
        int gameNumber = Integer.parseInt(params[0]);
        if (!games.containsKey(gameNumber)) {
            throw new Exception("Game not found. Please use 'list' to see available games.");
        }

        int gameID = games.get(gameNumber);

        wsManager = new WebSocketFacade(serverUrl, notificationHandler);
        wsManager.observeGame(params[0], token);
        status = State.OBSERVER;
        return "Observing game. Waiting for game state...";
    }

    private String makeMove(String[] params) throws Exception {
        verifyGameStatus();

        if (params.length < 2 || params.length > 3) {
            throw new Exception("Usage: move <source> <destination> [promotion]");
        }

        String source = params[0].toLowerCase();
        String destination = params[1].toLowerCase();
        String promotion = (params.length == 3) ? params[2].toLowerCase() : null;

        // check source
        if (!source.matches("^[a-h][1-8]$")) {
            throw new Exception("Invalid source square: must be from a1 to h8.");
        }
        //check destination
        if (!destination.matches("^[a-h][1-8]$")) {
            throw new Exception("Invalid destination square: must be from a1 to h8.");
        }
        // check not equal
        if (source.equals(destination)){
            throw new Exception("Source and destination squares must be different.");
        }
        // Validate promotion if provided
        if (promotion != null && !promotion.matches("^[qrbn]$")) {
            throw new Exception("Invalid promotion piece. Use one of: q, r, b, n");
        }

        wsManager.makeMove(token, source, destination, promotion);

        return "Move done: " + source + " → " + destination + (promotion != null ? " promoted to " + promotion.toUpperCase() : "");
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

    // help messages depending on the status
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
        if (status == GAMESTATUS) {
            return """
                    Make a move: "move" <source> <destination> <optional promotion>(e.g. f5 e4 q)
                    Redraw Board: "redraw"
                    Legal Moves :"legal" <location>
                    Resign from game: "resign"
                    Leave game: "leave"
                    """;
        }
        if (status == OBSERVER) {
            return """
                    Leave game: "leave"
                    """;
        }
        else{
            return """
                    quit - playing chess
                    help - see possible commands
                    """;
        }
    }


    // status verification
    private void verifyPreLoginStatus() throws Exception {
        if (status != PRELOGIN){
            throw new Exception("Wrong action, you are already signed in.");
        }
    }
    private void verifyPostLoginStatus() throws Exception {
        if (status != POSTLOGIN){
            throw new Exception("Wrong action, you have to register or log in first.");
        }
    }
    private void verifyGameStatus() throws Exception {
        if (status != GAMESTATUS ||status != OBSERVER ){
            throw new Exception("Wrong action, you have to register or log in first, and join or observe a game.");
        }
    }

    //status report
    public State getstate() {
        return status;
    }
}
