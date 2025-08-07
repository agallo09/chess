package websocket;

import com.google.gson.Gson;
import websocket.commands.UserGameCommand;
import websocket.commands.UserGameCommand.CommandType;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.function.Consumer;

@ClientEndpoint
public class WebSocketClientManager {

    private Session session;
    private final URI serverUri;
    private final Gson gson = new Gson();

    // Callback to process messages from server
    private Consumer<ServerMessage> onServerMessage;

    public WebSocketClientManager(String serverUri) throws Exception {
        this.serverUri = new URI(serverUri);
    }

    // Connect to server and open WebSocket session
    public void connect() throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.connectToServer(this, serverUri);
    }

    // Called when WebSocket connection is opened
    @OnOpen
    public void onOpen(Session session) {
        System.out.println("WebSocket Connected");
        this.session = session;
    }

    // Called when a message is received from the server
    @OnMessage
    public void onMessage(String message) {
        ServerMessage serverMsg = gson.fromJson(message, ServerMessage.class);
        if (onServerMessage != null) {
            onServerMessage.accept(serverMsg);
        } else {
            System.out.println("Received: " + message);
        }
    }

    // Called when connection closes
    @OnClose
    public void onClose(Session session, CloseReason reason) {
        System.out.println("WebSocket Closed: " + reason);
        this.session = null;
    }

    // Called on error
    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("WebSocket Error: " + throwable.getMessage());
    }

    // Send a UserGameCommand message to server
    public void sendCommand(UserGameCommand command) throws IOException {
        if (session != null && session.isOpen()) {
            String json = gson.toJson(command);
            session.getBasicRemote().sendText(json);
        } else {
            throw new IOException("WebSocket session is not open.");
        }
    }

    // Convenience methods for common commands

    public void sendConnect(String authToken, Integer gameID) throws IOException {
        UserGameCommand cmd = new UserGameCommand(CommandType.CONNECT, authToken, gameID);
        sendCommand(cmd);
    }

    public void sendLeave(String authToken, Integer gameID) throws IOException {
        UserGameCommand cmd = new UserGameCommand(CommandType.LEAVE, authToken, gameID);
        sendCommand(cmd);
    }

    public void sendResign(String authToken, Integer gameID) throws IOException {
        UserGameCommand cmd = new UserGameCommand(CommandType.RESIGN, authToken, gameID);
        sendCommand(cmd);
    }

    // Add more send methods for MAKE_MOVE with move object as needed

    // Set callback to handle incoming ServerMessages
    public void setOnServerMessage(Consumer<ServerMessage> callback) {
        this.onServerMessage = callback;
    }

    // Close WebSocket connection
    public void close() throws IOException {
        if (session != null && session.isOpen()) {
            session.close();
        }
    }
}
