package websocket;
import chess.*;
import chess.ChessGame.TeamColor;
import com.google.gson.Gson;
import dataaccess.AuthTokenDaoInterface;
import dataaccess.DataAccessException;
import dataaccess.GameDaoInterface;
import dataaccess.UserDaoInterface;
import model.GameData;
import model.JoinRequest;
import websocket.commands.*;
import websocket.messages.*;

import java.io.IOException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import websocket.messages.Error;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class ServerWebSocketFacade{
    //fields
    private AuthTokenDaoInterface tokenDAO;
    private GameDaoInterface gameDAO;
    private UserDaoInterface userDAO;
    private final Map<Integer, Set<Session>> gameSessions = new HashMap<>();
    private final List<Integer> resignedGames = new ArrayList<>();

    //constructor
    public ServerWebSocketFacade(UserDaoInterface userDAO,AuthTokenDaoInterface tokenDAO,GameDaoInterface gameDAO) {
        this.userDAO =  userDAO;
        this.tokenDAO = tokenDAO;
        this.gameDAO = gameDAO;
    }

    // Called when  message is received from a client
    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        try {
            UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
            switch (command.getCommandType()) {
                case CONNECT -> joinHandler(session, command);
                case MAKE_MOVE -> handleMakeMove(session, message);
                case LEAVE -> handleLeave(session, command);
                case RESIGN -> handleResign(session, command);
                case LEGAL_MOVES -> handleLegalMoves(session, command);
                default -> sendError(session, "Unknown command type.");
            }
        } catch (Exception e) {
            sendError(session, "Error processing command: " + e.getMessage());
        }
    }

    private void joinHandler(Session session, UserGameCommand connectCmd) throws DataAccessException, IOException {
        // Store session for the game
        gameSessions.computeIfAbsent(connectCmd.getGameID(), k -> new HashSet<>()).add(session);
        //check gameID
        String checkGame = gameDAO.getGame(new JoinRequest(null, connectCmd.getGameID()));
        if (checkGame == null){
            String message = "invalid gameID";
            sendError(session, message);
            return;
        }
        // Resolve username from token
        String token = connectCmd.getAuthToken();
        String username = tokenDAO.getUsername(token);

        // Load game data
        GameData game = gameDAO.checkGame(new JoinRequest(null, connectCmd.getGameID()));

        // Send the game state to the joining session
        LoadGame loadGame = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, game);
        session.getRemote().sendString(new Gson().toJson(loadGame));

        // Determine join role
        String role = (username.equals(game.blackUsername()) || username.equals(game.whiteUsername()))
                ? "player"
                : "observer";

        // Notify all other sessions for this game
        sendNotification(connectCmd.getGameID(), session, username + " joined as " + role);
    }

    private void handleMakeMove(Session session, String message) throws DataAccessException, IOException, InvalidMoveException {
        MakeMove move1 = new Gson().fromJson(message, MakeMove.class);
        int gameID = move1.getGameID();
        ChessMove move = move1.getMove();
        ChessPosition source = move.getStartPosition();
        // Get game data
        GameData gameData = gameDAO.checkGame(new JoinRequest(null, gameID));
        ChessGame game = gameData.game();
        // team turn
        TeamColor color = game.getTeamTurn();
        //check if the person is in turn
        String token = move1.getAuthToken();
        String username = tokenDAO.getUsername(token);
        GameData game1 = gameDAO.checkGame(new JoinRequest(null, gameData.gameID()));
        String whiteUser = game1.whiteUsername();
        String blackUser = game1.blackUsername();
        //error game already resigned

        if ( resignedGames.contains(move1.getGameID())) {
            sendError(session, "A player resigned already.");
            return;
        }
        //error not your turn
        if ( (color == TeamColor.WHITE && !username.equals(whiteUser)) ||
                (color == TeamColor.BLACK && !username.equals(blackUser)) ) {
            sendError(session, "It's not your turn");
            return;
        }

        //getting piece color
        ChessBoard board = game.getBoard();
        ChessPiece sourcePiece = board.getPiece(source);
        //Piece color
        TeamColor pieceColor = sourcePiece.getTeamColor();

        //check if turn and piece the same color
        if (!color.equals(pieceColor)){
            Error errorMsg = new Error(ServerMessage.ServerMessageType.ERROR, "Not your piece");
            String errorJson = new Gson().toJson(errorMsg);
            session.getRemote().sendString(errorJson);
            return;
        }


        // Validate the move
        Collection<ChessMove> validMoves = game.validMoves(move.getStartPosition());
        if (validMoves.contains(move)) {
            // Apply the move to the game object
            game.makeMove(move);

            // Persist the updated game in the database
            gameDAO.updateGameState(gameID, game);
            // Create the updated LOAD_GAME message
            LoadGame loadGameMsg = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, gameData);
            String loadGameJson = new Gson().toJson(loadGameMsg);
            // Send LOAD_GAME to everyone in the game
            Set<Session> sessions = gameSessions.get(gameID);
            if (sessions != null) {
                for (Session s : sessions) {
                    try {
                        s.getRemote().sendString(loadGameJson);
                    } catch (IOException e) {
                        System.err.println("Failed to send load game: " + e.getMessage());
                    }
                }
            }

            // Notify others that a move was made
            sendNotification(gameID, session, "A move has been made");
        } else {
            sendError(session, "Invalid move");
        }
    }

    private void handleLeave(Session session, UserGameCommand connectCmd) throws IOException, DataAccessException {
        // 2 things have to be made: first eliminate the player from database and send a notification to all the people in the game
        // first eliminate the player from the database
        // second update the hashmap
        // third, send a notification to all sessions left

        //getting player info from database
        String token = connectCmd.getAuthToken();
        String username = tokenDAO.getUsername(token);
        GameData game1 = gameDAO.checkGame(new JoinRequest(null, connectCmd.getGameID()));
        String whiteUsername = game1.whiteUsername();
        String blackUsername = game1.blackUsername();

        if (username.equals(whiteUsername)) {
            // eliminate from database

            gameDAO.setWhiteUsername(connectCmd.getGameID(),null);
            // updating map and sending notifications
        }else if(username.equals(blackUsername)){
            // eliminate from database

            gameDAO.setBlackUsername(connectCmd.getGameID(),null);
            // updating map and sending notifications
        }
        Set<Session> sessions = gameSessions.get(connectCmd.getGameID());
        if (sessions != null) {
            sessions.remove(session);
        }
        // Notify remaining players only, exclude the leaving session
        sendNotification(connectCmd.getGameID(), session, "A player has left the game.");

    }

    private void handleResign(Session session, UserGameCommand command) throws DataAccessException {
        // first mark game es over is not allowing more moves only, so I can still leave or see legal moves
        //update in database
        //send notification informing the resigned
        // update make move, to check for the status that this handler will create in the table
        if (resignedGames.contains(command.getGameID())) {
            sendError(session, "Game already resigned.");
            return;
        }
        resignedGames.add(command.getGameID());
        String token = command.getAuthToken();
        String username = tokenDAO.getUsername(token);
        String message = "Player has resigned: " + username;

        // check if the username is a player
        GameData game = gameDAO.checkGame(new JoinRequest(null, command.getGameID()));
        String whiteUser = game.whiteUsername();
        String blackUser = game.blackUsername();
        // If not a player, send error and stop
        if (!username.equals(whiteUser) && !username.equals(blackUser)) {
            sendError(session, "Only players can resign a game.");
            return;
        }
        //sending notification to all
        Notification notificationToAll = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message);
        String jsonMsg = new Gson().toJson(notificationToAll);
        Set<Session> sessions = gameSessions.get(command.getGameID());

        for (Session s : sessions) {
                try {
                    s.getRemote().sendString(jsonMsg);
                } catch (IOException e) {
                    System.err.println("Failed to send notification: " + e.getMessage());
                }
            }
    }

    private void handleLegalMoves(Session session, UserGameCommand command) throws DataAccessException, IOException {
        MakeMove connectCmd = (MakeMove) command;
        //have to retrieve game from db and use the move to validate the move
        ChessMove move = connectCmd.getMove();
        //get game
        GameData gamedata = gameDAO.checkGame(new JoinRequest(null, connectCmd.getGameID()));
        //validate the move
        ChessGame game = gamedata.game();
        Collection<ChessMove> validMoves = game.validMoves(move.getStartPosition());
        if (!validMoves.isEmpty()){
            //message for the client
            Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, "valid move");
            // Convert command to JSON
            String json = new Gson().toJson(notification);
            // Send JSON command via WebSocket asynchronously
            session.getRemote().sendString(json);
        }
    }

    private void sendError(Session session, String errorMsg) {
        try {
            Error error = new Error(ServerMessage.ServerMessageType.ERROR, errorMsg);
            String json = new Gson().toJson(error);
            session.getRemote().sendString(json);
        } catch (IOException e) {
            System.err.println("Failed to send error message: " + e.getMessage());
        }
    }


    private void sendNotification(int gameID, Session joiningSession, String message) {
        Notification notif = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message);
        String jsonMsg = new Gson().toJson(notif);

        Set<Session> sessions = gameSessions.get(gameID);
        if (sessions == null) {
            return;
        }

        for (Session s : sessions) {
            if (!s.equals(joiningSession)) {
                try {
                    s.getRemote().sendString(jsonMsg);
                } catch (IOException e) {
                    System.err.println("Failed to send notification: " + e.getMessage());
                }
            }
        }
    }



}
