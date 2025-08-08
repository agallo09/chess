package sahredWebsocket.messages;

import chess.ChessGame;

public class LoadGame extends ServerMessage{
    private final ChessGame game;
    private boolean white;

    public LoadGame(ServerMessageType type, ChessGame game, boolean displayWhite) {
        super(type);
        this.game = game;
        this.white = displayWhite;
    }

    public ChessGame getGame(){
        return game;
    }

    public boolean shouldDisplayWhite() {
        return white;
    }
}
