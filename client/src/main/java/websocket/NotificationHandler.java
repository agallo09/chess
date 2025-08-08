package websocket;

import sahredWebsocket.messages.ServerMessage;

public interface NotificationHandler {
    void notify(ServerMessage message);
}
