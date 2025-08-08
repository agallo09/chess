
package websocket;
import com.google.gson.Gson;
import repls.ResponseException;
import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

//need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {

    //fields
    Session session;
    NotificationHandler notificationHandler;

    // constructor
    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    NotificationHandler notification = new Gson().fromJson(message, NotificationHandler.class);
                    notificationHandler.notify(notification);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }
    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void joinGame(String gameId, String color, String authToken) {
        var action = new Action(Action.Type.JOIN_PLAYER, gameId, color, authToken);
        String json = new Gson().toJson(action);
        this.session.getAsyncRemote().sendText(json);
    }

    public void observeGame(String param, String param1) {
    }

    private void send(String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            throw new RuntimeException("WebSocket send failed: " + e.getMessage());
        }
    }

    public void makeMove(String token, String source, String destination, String promotion) {

    }

    public void legalMoves(String token, String source) {

    }

    public void resign(String token) {
    }

    public void redrawBoard(String token) {
    }

    public void leave(String token) {
    }
}