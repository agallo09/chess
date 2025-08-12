package websocket;
import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.JoinRequest;
import sahredWebsocket.commands.*;
import sahredWebsocket.messages.*;
import service.GameService;
import service.UserService;
import java.io.IOException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import java.io.IOException;
import java.util.Timer;

@WebSocket
public class ServerWebSocketFacade{
    //fields
    private final ConnectionManager connectionManager = new ConnectionManager();
    private final GameService gameService;
    private final UserService userService;

    //constructor
    public ServerWebSocketFacade(GameService gameService, UserService userService) {

        this.gameService =  gameService;
        this.userService = userService;
    }

    // Called when  message is received from a client
    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        try {
            UserGameCommand command = parseCommand(message);
            switch (command.getCommandType()) {
                case CONNECT -> joinHandler(session, command);
                case MAKE_MOVE -> handleMakeMove(session, command);
                case LEAVE -> handleLeave(session, command);
                case RESIGN -> handleResign(session, command);
                case LEGAL_MOVES -> handleLegalMoves(session, command);
                default -> sendError(session, "Unknown command type.");
            }
        } catch (Exception e) {
            sendError(session, "Error processing command: " + e.getMessage());
        }
    }

    // Called when a connection closes
    public void onClose(Object session) {
        System.out.println("Connection closed: " + session);
        handleDisconnect(session);
    }

    // Called on errors
    public void onError(Object session, Throwable throwable) {
        System.err.println("Error on session " + session + ": " + throwable.getMessage());
    }

    private void joinHandler(Session session, UserGameCommand command) throws DataAccessException, IOException {
        Connect connectCmd = (Connect) command;
        String token = connectCmd.getAuthToken();
        ChessGame.TeamColor color = ChessGame.TeamColor.valueOf(connectCmd.getColor());
        JoinRequest joinRequest = new JoinRequest(color, connectCmd.getGameID());
        String message = gameService.join(token, joinRequest);
        //message for the client
        LoadGame notification = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, message);
        // Convert command to JSON
        String json = new Gson().toJson(notification);

        // Send JSON command via WebSocket asynchronously
        session.getRemote().sendString(json);

    }

    private void handleMakeMove(Object session, UserGameCommand command) {
        // Process move
    }

    private void handleLeave(Object session, UserGameCommand command) {
        // Handle leave
    }

    private void handleResign(Object session, UserGameCommand command) {
        // Handle resign
    }

    private void handleLegalMoves(Object session, UserGameCommand command) {
        // Send legal moves
    }

    // --- Helper methods ---

    private void sendError(Object session, String errorMsg) {
        //COMMENT
    }

    private String serializeMessage(ServerMessage msg) {
        // Serialize with Gson or other JSON lib
        return ""; // placeholder
    }

    private UserGameCommand parseCommand(String message) {
        // Parse JSON command here
        return null; // placeholder
    }

    private void sendToSession(Object session, String message) throws IOException {
        // Send the message to the client via your framework's method
    }

    private void handleDisconnect(Object session) {
        // Remove session from tracking, update game state
    }

    private static class UserSessionInfo {
        String authToken;
        int gameId;
        boolean isObserver;
        // other info
    }
}
