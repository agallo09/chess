package websocket;
import chess.ChessMove;
import chess.ChessPiece.PieceType;
import chess.ChessPosition;
import com.google.gson.Gson;
import repls.ResponseException;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import websocket.messages.*;
import websocket.commands.*;
import websocket.messages.Error;
import ui.Board;


//need to extend Endpoint for websocket to work properly
@ClientEndpoint
public class ClientWebSocketFacade {

    //fields
    Session session;
    private final Gson gson = new Gson();
    private String gameId;
    private NotificationHandler handler;
    private Board board;

    public ClientWebSocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.handler = notificationHandler;
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();

            this.session = container.connectToServer(this, socketURI);
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @OnMessage
                public void onMessage(String message) {
                    System.out.println("Received message: " + message);  // <--- Add this log
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    var messageType = serverMessage.getServerMessageType();
                    switch (messageType) {
                        case NOTIFICATION -> {
                            Notification notificationMessage = new Gson().fromJson(message, Notification.class);
                            handler.notify(notificationMessage);
                        }
                        case ERROR -> {
                            Error errorMessage = new Gson().fromJson(message, Error.class);
                            handler.notify(errorMessage);
                        }
                        case LOAD_GAME -> {
                            loadGameHandler(message);
                        }
                    }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }
    //Endpoint requires this method, but you don't have to do anything
    @OnOpen
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        System.out.println("Successful connection....");
    }

    public void joinGame(String gameId, String color, String authToken) throws IOException {
        // Create a command with CONNECT type to join the game
        this.gameId = gameId;
        Connect command = new Connect(UserGameCommand.CommandType.CONNECT, authToken,Integer.parseInt(gameId), color);
        // Convert command to JSON
        String json = new Gson().toJson(command);
        // Send JSON command via WebSocket asynchronously
        this.session.getBasicRemote().sendText(json);
    }

    public void observeGame(String gameId, String token) {
        Connect command = new Connect(UserGameCommand.CommandType.CONNECT, token,Integer.parseInt(gameId), null);// convert gameId String to Integer);
        // Convert command to JSON
        String json = new Gson().toJson(command);
        // Send JSON command via WebSocket asynchronously
        this.session.getAsyncRemote().sendText(json);
    }

    public void makeMove(String token, String source, String destination, String promotion) {
        // making the chess move object to pass
        ChessPosition from = toPosition(source);
        ChessPosition to = toPosition(destination);
        PieceType promoPiece = toPieceType(promotion);
        ChessMove move = new ChessMove(from, to, promoPiece);
        //make move command object
        MakeMove command = new MakeMove(
                UserGameCommand.CommandType.MAKE_MOVE, token, Integer.parseInt(gameId), move);
        //calling the method of the websocket
        String json = new Gson().toJson(command);
        this.session.getAsyncRemote().sendText(json);
    }

    public void legalMoves(String token, String source) {
    // making the source to a chess position
        ChessPosition position = toPosition(source);
        //make move command object
        LegalMoves command = new LegalMoves(
                UserGameCommand.CommandType.LEGAL_MOVES, token, Integer.parseInt(gameId), position);
        //calling the method of the websocket
        String json = new Gson().toJson(command);
        this.session.getAsyncRemote().sendText(json);
    }

    public void resign(String token) {
        UserGameCommand command = new UserGameCommand(
                UserGameCommand.CommandType.RESIGN, token, Integer.parseInt(gameId));
        //calling the method of the websocket
        String json = new Gson().toJson(command);
        this.session.getAsyncRemote().sendText(json);
    }

    public void redrawBoard(String token) {

    }

    public void leave(String token) {
        UserGameCommand command = new UserGameCommand(
                UserGameCommand.CommandType.LEAVE, token, Integer.parseInt(gameId));
        //calling the method of the websocket
        String json = new Gson().toJson(command);
        this.session.getAsyncRemote().sendText(json);
    }

    //helping methods
    private void loadGameHandler(String message) {
        System.out.println("board:");
        if (message.contains("white")){
            board.drawWhite();
        }else if (message.contains("black")){
            board.drawBlack();
        }
        System.out.println("board drawn");

    }
    private ChessPosition toPosition(String pos) {
        int col = pos.charAt(0) - 'a';     // 'a'->0, 'b'->1, ..., 'h'->7
        int row = 8 - (pos.charAt(1) - '0'); // '1'->7, '8'->0 if row 0 is top rank 8
        return new ChessPosition(row, col);
    }
    private PieceType toPieceType(String promo) {
        if (promo == null) {
            return null;
        } // no promotion
        switch (promo.toLowerCase()) {
            case "q": return PieceType.QUEEN;
            case "r": return PieceType.ROOK;
            case "b": return PieceType.BISHOP;
            case "n": return PieceType.KNIGHT;
            default: return null; // or throw exception for invalid promo
        }
    }


}