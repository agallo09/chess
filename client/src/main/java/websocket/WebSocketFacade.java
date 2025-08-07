
package websocket;
import com.google.gson.Gson;
import repls.ResponseException;
import server.ServerFacade;
import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

//need to extend Endpoint for websocket to work properly
public class WebSocketClientManager extends Endpoint {

    //fields
    Session session;
    NotificationHandler notificationHandler;

    public WebSocketClientManager(String url, NotificationHandler notificationHandler) throws ResponseException {
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


    public void joinGame(String color, String gameId) {
        server.joinGame(authToken, gameID);
        this.ws = new WebSocketFacade(serverUrl, notificationHandler);
        this.ws.connect(authToken, gameID);
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
}